package id.ac.campus.antiexam.client;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.data.SesiUjianData;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import javax.swing.BorderFactory;
import javax.swing.Timer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ExamClientFrame extends JFrame {

    private final int sessionId;
    private final String studentName;
    private final String token;

    private int remainingSeconds;
    private int violationCount = 0;
    private boolean locked = false;

    private JLabel lblTimer;
    private JLabel lblStatus;
    private JTextArea txtAnswer;
    private JPanel lockOverlay;

    private Timer countdownTimer;
    private Timer commandTimer;

    private final SesiUjianData sessionRepository = new SesiUjianData();

    public ExamClientFrame(int sessionId, String studentName, String token, int durationMinutes) {
        this.sessionId = sessionId;
        this.studentName = studentName;
        this.token = token;

        this.remainingSeconds = durationMinutes * 60;
        initUi();
        initTimers();
        initFocusListener();
    }

    private void initUi() {
        setTitle("Ujian Berlangsung");
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new BorderLayout());
        top.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel lblInfo = new JLabel("Peserta: " + studentName + " | Token: " + token);
        lblInfo.setFont(new Font("SansSerif", Font.PLAIN, 16));
        lblTimer = new JLabel("00:00", SwingConstants.RIGHT);
        lblTimer.setFont(new Font("SansSerif", Font.BOLD, 18));
        top.add(lblInfo, BorderLayout.WEST);
        top.add(lblTimer, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        JPanel center = new JPanel(new BorderLayout(10, 10));
        center.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        JLabel lblSoal = new JLabel("Soal: Jelaskan konsep OOP secara singkat.", SwingConstants.LEFT);
        lblSoal.setFont(new Font("SansSerif", Font.PLAIN, 18));
        txtAnswer = new JTextArea();
        txtAnswer.setLineWrap(true);
        txtAnswer.setWrapStyleWord(true);
        txtAnswer.setFont(new Font("SansSerif", Font.PLAIN, 16));
        txtAnswer.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        center.add(lblSoal, BorderLayout.NORTH);
        center.add(new JScrollPane(txtAnswer), BorderLayout.CENTER);
        add(center, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 2));
        bottom.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        lblStatus = new JLabel("Status: Ujian aktif", SwingConstants.LEFT);
        JButton btnFinish = new JButton("Kumpulkan Jawaban");
        btnFinish.addActionListener(e -> confirmFinish());
        bottom.add(lblStatus);
        bottom.add(btnFinish);
        add(bottom, BorderLayout.SOUTH);

        lockOverlay = new JPanel(new BorderLayout());
        lockOverlay.setBackground(new Color(0, 0, 0, 200));
        JLabel lblLocked = new JLabel("UJIAN DIKUNCI\nSilakan hubungi dosen.", SwingConstants.CENTER);
        lblLocked.setForeground(Color.WHITE);
        lblLocked.setFont(new Font("SansSerif", Font.BOLD, 32));
        lockOverlay.add(lblLocked, BorderLayout.CENTER);
        lockOverlay.setVisible(false);
        getLayeredPane().add(lockOverlay, Integer.valueOf(Integer.MAX_VALUE));
        lockOverlay.setBounds(0, 0, getWidth(), getHeight());

        addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent e) {
                lockOverlay.setBounds(0, 0, getWidth(), getHeight());
            }
        });
    }

    private void initTimers() {
        countdownTimer = new Timer(1000, e -> tickCountdown());
        countdownTimer.start();
        commandTimer = new Timer(3000, e -> checkCommands());
        commandTimer.start();
    }

    private void initFocusListener() {
        addWindowListener(new WindowAdapter() {
            public void windowLostFocus(WindowEvent e) {
                handlePelanggaran("FOCUS_LOST");
            }
        });
    }

    private void tickCountdown() {
        if (locked) {
            return;
        }
        if (remainingSeconds <= 0) {
            finishUjian(true);
            return;
        }
        remainingSeconds--;
        int m = remainingSeconds / 60;
        int s = remainingSeconds % 60;
        lblTimer.setText(String.format("%02d:%02d", m, s));
    }

    private void handlePelanggaran(String type) {
        if (locked) {
            return;
        }
        try {
            violationCount = sessionRepository.logPelanggaran(sessionId, type);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (violationCount == 1) {
            lblStatus.setText("Status: Pelanggaran 1 terdeteksi");
            JOptionPane.showMessageDialog(this, "Peringatan: jangan keluar dari aplikasi ujian.");
        } else if (violationCount >= 2) {
            locked = true;
            lblStatus.setText("Status: Ujian dikunci, menunggu dosen");
            lockOverlay.setVisible(true);
            try {
                sessionRepository.updateStatus(sessionId, "LOCKED");
            } catch (Exception e) {
                e.printStackTrace();
            }
            showLockedDialog();
        }
    }

    private void checkCommands() {
        if (sessionId <= 0) {
            return;
        }
        try {
            Connection conn = KoneksiDatabase.getConnection();
            String sql = "SELECT id, command FROM admin_commands WHERE session_id=? AND processed=0";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int cmdId = rs.getInt("id");
                String cmd = rs.getString("command");
                if ("UNLOCK".equalsIgnoreCase(cmd)) {
                    applyUnlock();
                }
                if ("FORCE_FINISH".equalsIgnoreCase(cmd)) {
                    finishUjian(false);
                }
                markCommandProcessed(cmdId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void markCommandProcessed(int id) {
        try {
            Connection conn = KoneksiDatabase.getConnection();
            String sql = "UPDATE admin_commands SET processed=1 WHERE id=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void applyUnlock() {
        locked = false;
        violationCount = 0;
        lockOverlay.setVisible(false);
        lblStatus.setText("Status: Ujian aktif (dibuka oleh dosen)");
        try {
            sessionRepository.updateStatus(sessionId, "ONGOING");
        } catch (Exception e) {
            e.printStackTrace();
        }
        JOptionPane.showMessageDialog(this, "Dosen telah membuka kunci ujian. Silakan lanjut.");
    }

    private void confirmFinish() {
        if (locked) {
            JOptionPane.showMessageDialog(this, "Ujian sedang dikunci. Hubungi dosen.");
            return;
        }
        int result = JOptionPane.showConfirmDialog(this, "Kumpulkan jawaban sekarang?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            finishUjian(false);
        }
    }

    private void finishUjian(boolean timeUp) {
        countdownTimer.stop();
        commandTimer.stop();
        try {
            sessionRepository.updateStatus(sessionId, "FINISHED");
        } catch (Exception e) {
            e.printStackTrace();
        }
        saveAnswer();
        String msg = timeUp ? "Waktu habis. Jawaban otomatis dikumpulkan." : "Jawaban berhasil dikumpulkan.";
        JOptionPane.showMessageDialog(this, msg);
        dispose();
        showEndDialog();
    }

    private void saveAnswer() {
        try {
            Connection conn = KoneksiDatabase.getConnection();
            String sql = "INSERT INTO exams_answers(session_id, answer_text) VALUES(?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, sessionId);
            ps.setString(2, txtAnswer.getText());
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showLockedDialog() {
        JDialog dlg = new JDialog(this, "Ujian Dikunci", true);
        dlg.setSize(350, 150);
        dlg.setLocationRelativeTo(this);
        JLabel lbl = new JLabel("Ujian dikunci. Silakan hubungi dosen.", SwingConstants.CENTER);
        dlg.add(lbl);
        dlg.setVisible(true);
    }

    private void showEndDialog() {
        JDialog dlg = new JDialog((JFrame) null, "Ujian Selesai", true);
        dlg.setSize(300, 140);
        dlg.setLocationRelativeTo(null);
        JLabel lbl = new JLabel("Terima kasih. Ujian selesai.", SwingConstants.CENTER);
        dlg.add(lbl);
        dlg.setVisible(true);
    }
}
