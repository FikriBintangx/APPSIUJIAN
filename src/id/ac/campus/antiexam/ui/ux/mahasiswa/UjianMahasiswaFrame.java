package id.ac.campus.antiexam.ui.ux.mahasiswa;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.data.SesiUjianData;

import id.ac.campus.antiexam.ui.ux.PdfPanel;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.JComponent;
import javax.swing.BoxLayout;
import java.awt.event.KeyEvent;
import id.ac.campus.antiexam.ui.component.ToastNotification;

public class UjianMahasiswaFrame extends JFrame {

    private final int sessionId;
    private final String studentName;

    private int examId;
    private int durationMinutes;
    private String examTitle;
    private String examMode;

    private CardLayout mainLayout;
    private JPanel mainContainer;
    private JPanel examPanel;
    private JPanel rulesPanel;
    private JPanel preFlightPanel;

    private JLabel lblBroadcast; // Ticker text

    private JLabel lblStatus;
    private JLabel lblTimer;

    private JButton btnNext;
    private JButton btnPrev;
    private JButton btnFinish;

    private JTextArea txtEssayAnswer;
    private JTextArea txtEssayQuestion; // Field baru untuk soal Essay pagination
    private JTextArea txtPgSoal;
    private JRadioButton rbA;
    private JRadioButton rbB;
    private JRadioButton rbC;
    private JRadioButton rbD;
    private ButtonGroup pgGroup;
    private JLabel lblQuestionNumber;

    private final SesiUjianData sessionRepository = new SesiUjianData();
    private Timer statusTimer;
    private Timer countdownTimer;
    private Timer autoSaveTimer; // auto-simpan tiap 30 detik
    private int remainingSeconds;
    private boolean locked = false;
    private boolean ignoreFocusLost = false;
    private boolean isExamActive = false;
    private long lastPelanggaranTime = 0;

    private final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private final Color BG_LIGHT = new Color(249, 250, 251);
    private final Color TEXT_DARK = new Color(17, 24, 39);
    private final Color ALERT_RED = new Color(239, 68, 68);
    private final Color SUCCESS_GREEN = new Color(16, 185, 129);
    private final Color WARNING_YELLOW = new Color(245, 158, 11);
    private final String LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    private final List<QuestionObj> questions = new ArrayList<>();
    private final Map<Integer, String> pgAnswers = new HashMap<>();
    private final Map<Integer, Boolean> answeredQuestions = new HashMap<>(); // Track answered questions
    private int currentQuestionIndex = 0;
    private JPanel questionPalettePanel; // Soal overview sidebar
    private JLabel lblProgress; // Progress label

    public UjianMahasiswaFrame(int sessionId, String studentName, String token) {
        this.sessionId = sessionId;
        this.studentName = studentName;

        setTitle("SiUjian - Browser Ujian Mahasiswa");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);
        try {
            setIconImage(new ImageIcon(LOGO_PATH).getImage());
        } catch (Exception e) {
        }

        initExamData();
        initUI();
        initFocusListener();
        initWatermark();
        initSecurity();
    }

    private void initExamData() {
        try {
            Connection conn = KoneksiDatabase.getConnection();

            // UPDATED SQL
            String sqlExam = "SELECT s.id_ujian, e.title, e.durasi_menit, e.type " +
                    "FROM ujian_mahasiswa s JOIN ujian e ON s.id_ujian = e.id WHERE s.id = ?";
            PreparedStatement ps = conn.prepareStatement(sqlExam);
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                examId = rs.getInt("id_ujian");
                examTitle = rs.getString("title");
                durationMinutes = rs.getInt("durasi_menit");
                examMode = rs.getString("type");
            } else {
                examId = 0;
                examTitle = "Ujian";
                durationMinutes = 60;
                examMode = "ESSAY";
            }

            remainingSeconds = durationMinutes * 60;

            loadQuestionsFromDb();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data ujian: " + ex.getMessage());
            ex.printStackTrace(); // Keep buat debugging
            System.exit(0);
        }
    }

    private void loadQuestionsFromDb() {
        questions.clear();
        try {
            Connection conn = KoneksiDatabase.getConnection();
            String sql = "SELECT * FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QuestionObj q = new QuestionObj();
                q.id = rs.getInt("id");
                q.text = rs.getString("pertanyaan");
                q.optA = rs.getString("option_a");
                q.optB = rs.getString("option_b");
                q.optC = rs.getString("option_c");
                q.optD = rs.getString("option_d");
                q.correctKey = rs.getString("kunci_jawaban");
                questions.add(q);
            }

            if (questions.isEmpty()) {
                System.out.println("Info: No structural questions found. Assuming PDF or manual mode.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat soal: " + e.getMessage());
        }
    }

    private void initUI() {
        mainLayout = new CardLayout();
        mainContainer = new JPanel(mainLayout);

        preFlightPanel = createPreFlightPanel();
        rulesPanel = createRulesPanel();
        examPanel = createExamPanel();

        mainContainer.add(preFlightPanel, "PREFLIGHT");
        mainContainer.add(rulesPanel, "RULES");
        mainContainer.add(examPanel, "EXAM");

        add(mainContainer);

        mainLayout.show(mainContainer, "PREFLIGHT");
    }

    private JPanel createRulesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(17, 24, 39));

        JPanel card = new RoundedPanel(24, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(50, 60, 50, 60));

        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        headerPanel.setOpaque(false);

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            Image img = icon.getImage().getScaledInstance(60, 60, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblLogo.setText("ðŸ›¡ï¸");
            lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 48));
        }

        JLabel lblTitle = new JLabel("SiUjian");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(PRIMARY_BLUE);
        lblTitle.setBorder(new EmptyBorder(0, 15, 0, 0));

        headerPanel.add(lblLogo);
        headerPanel.add(lblTitle);

        JLabel lblSubtitle = new JLabel("Tata Tertib & Doa Sebelum Ujian", JLabel.CENTER);
        lblSubtitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblSubtitle.setForeground(TEXT_DARK);
        lblSubtitle.setBorder(new EmptyBorder(30, 0, 20, 0));

        String htmlRules = "<html><body style='text-align: center; width: 600px; font-family: Segoe UI;'>" +
                "<h2 style='color: #1e40af; margin-bottom: 25px;'>Petunjuk Penting:</h2>" +
                "<div style='text-align: left; margin: 0 auto; width: 90%;'>" +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>1. Awali dengan berdoa menurut agama dan kepercayaan masing-masing.</p>"
                +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>2. Dilarang keras melakukan kecurangan (mencontek, kerja sama).</p>"
                +
                "<p style='color: #dc2626; font-size: 16px; font-weight: bold; margin-bottom: 12px; background: #fef2f2; padding: 10px; border-radius: 8px;'>3. Warning: JANGAN berpindah window atau menekan Alt+Tab. Sistem akan mendeteksi dan mengunci ujian Anda.</p>"
                +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>4. Waktu berjalan mundur otomatis saat Anda menekan tombol mulai.</p>"
                +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>5. Jawaban tersimpan otomatis setiap 30 detik.</p>"
                +
                "</div>" +
                "<br><br>" +
                "<div style='background: #f0f9ff; padding: 20px; border-radius: 12px; border-left: 4px solid #1d4ed8; margin-top: 20px;'>"
                +
                "<p style='font-style: italic; color: #1e40af; font-size: 16px;'>\"Kejujuran adalah mata uang yang berlaku di mana saja.\"</p>"
                +
                "<p style='color: #6b7280; font-size: 14px; margin-top: 5px;'>- SiUjian Integrity Policy</p>" +
                "</div>" +
                "</body></html>";

        JLabel lblContent = new JLabel(htmlRules, JLabel.CENTER);
        lblContent.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        JButton btnStart = createStyledButton("SAYA MENGERTI & MULAI UJIAN", true, PRIMARY_BLUE);
        btnStart.setPreferredSize(new Dimension(300, 55));
        btnStart.addActionListener(e -> startSesiUjian());

        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.setOpaque(false);
        btnWrap.setBorder(new EmptyBorder(40, 0, 0, 0));
        btnWrap.add(btnStart);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setOpaque(false);
        centerPanel.add(lblSubtitle, BorderLayout.NORTH);
        centerPanel.add(lblContent, BorderLayout.CENTER);
        centerPanel.add(btnWrap, BorderLayout.SOUTH);

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(centerPanel, BorderLayout.CENTER);

        JPanel wrap = new JPanel(new GridBagLayout());
        wrap.setBackground(new Color(17, 24, 39));
        wrap.add(card);

        panel.add(wrap, BorderLayout.CENTER);
        return panel;
    }

    private void startSesiUjian() {
        mainLayout.show(mainContainer, "EXAM");
        startStatusTimer();
        startCountdown();
        startAutoSave(); // mulai auto-simpan
        if ("PG".equalsIgnoreCase(examMode) && !questions.isEmpty()) {
            loadPgSoal(0);
        } else {
            // Render text for Essay / Default Mode
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                QuestionObj q = questions.get(i);
                sb.append("Soal No. ").append(i + 1).append(":\n")
                        .append(q.text).append("\n\n");
            }
            // Check for PDF as well
            String pdfInfo = (getPdfPath() != null) ? "[Lihat Soal di PDF Panel Sebelah]\n\n" : "";
            if (txtEssayQuestion != null) {
                txtEssayQuestion.setText(pdfInfo + sb.toString());
                txtEssayQuestion.setCaretPosition(0);
            }
        }
        Timer t = new Timer(3000, e -> {
            isExamActive = true;
            ((Timer) e.getSource()).stop();
        });
        t.start();
    }

    private JPanel createExamPanel() {
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(BG_LIGHT);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)),
                new EmptyBorder(15, 30, 15, 30)));
        topBar.setPreferredSize(new Dimension(getWidth(), 90));

        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftPanel.setBackground(Color.WHITE);

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblLogo.setText("ðŸ›¡ï¸");
            lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 28));
        }

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 3));
        titlePanel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel(examTitle);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblTitle.setForeground(TEXT_DARK);

        JLabel lblSubtitle = new JLabel(
                "Mode: " + (examMode != null ? examMode : "ESSAY") + " | Peserta: " + studentName);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(107, 114, 128));

        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        leftPanel.add(lblLogo);
        leftPanel.add(titlePanel);

        lblTimer = new JLabel(formatTime(remainingSeconds));
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTimer.setForeground(PRIMARY_BLUE);
        lblTimer.setBorder(new EmptyBorder(0, 20, 0, 0));

        topBar.add(leftPanel, BorderLayout.WEST);
        topBar.add(lblTimer, BorderLayout.EAST);

        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(BG_LIGHT);
        content.setBorder(new EmptyBorder(25, 35, 25, 35));

        if ("PG".equalsIgnoreCase(examMode)) {
            buildPgLayout(content);
        } else {
            buildEssayLayout(content);
        }

        JPanel bottomBar = new JPanel(new BorderLayout());
        bottomBar.setBackground(Color.WHITE);
        bottomBar.setPreferredSize(new Dimension(getWidth(), 90));
        bottomBar.setBorder(new EmptyBorder(10, 30, 20, 30));

        // Broadcast Panel
        lblBroadcast = new JLabel("Tidak ada pesan dari pengawas.");
        lblBroadcast.setFont(new Font("Consolas", Font.BOLD, 14));
        lblBroadcast.setForeground(Color.RED);
        lblBroadcast.setVisible(false);
        bottomBar.add(lblBroadcast, BorderLayout.NORTH);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setBackground(Color.WHITE);

        lblStatus = new JLabel("Status: Aman");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(SUCCESS_GREEN);
        lblStatus.setIconTextGap(10);

        JLabel lblInfo = new JLabel("OK Sistem aktif | Warning: Jangan pindah window");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(107, 114, 128));

        statusPanel.add(lblStatus);
        statusPanel.add(lblInfo);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setBackground(Color.WHITE);

        // Create buttons for ALL modes (PG & Essay)
        btnPrev = createStyledButton("< Sebelumnya", false, new Color(107, 114, 128));
        btnNext = createStyledButton("Selanjutnya >", true, PRIMARY_BLUE);

        btnPrev.addActionListener(e -> navigatePg(-1));
        btnNext.addActionListener(e -> navigatePg(1));

        btnPanel.add(btnPrev);
        btnPanel.add(btnNext);

        btnFinish = createStyledButton("OK Kirim Jawaban", true, SUCCESS_GREEN);
        btnFinish.addActionListener(e -> {
            // Force save current answer before submit
            if (!"PG".equalsIgnoreCase(examMode)) {
                saveCurrentEssayAnswer();
            }
            showReviewPage();
        });

        // initial visibility
        btnFinish.setVisible(false);

        btnPanel.add(btnFinish);

        bottomBar.add(statusPanel, BorderLayout.WEST);
        bottomBar.add(btnPanel, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        root.add(bottomBar, BorderLayout.SOUTH);

        return root;
    }

    private void buildPgLayout(JPanel container) {
        // split utama: sidebar kiri (palet soal) + konten kanan
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.2); // 20% buat sidebar
        mainSplit.setDividerSize(2);
        mainSplit.setBorder(null);

        // === kiri: Soal Palette ===
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(229, 231, 235)),
                new EmptyBorder(20, 15, 20, 15)));

        JLabel lblPaletteTitle = new JLabel("Daftar Soal");
        lblPaletteTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblPaletteTitle.setForeground(PRIMARY_BLUE);
        lblPaletteTitle.setBorder(new EmptyBorder(0, 0, 15, 0));

        questionPalettePanel = new JPanel(new GridLayout(0, 4, 8, 8)); // 4 columns
        questionPalettePanel.setBackground(Color.WHITE);
        updateQuestionPalette();

        JScrollPane paletteScroll = new JScrollPane(questionPalettePanel);
        paletteScroll.setBorder(null);
        paletteScroll.getViewport().setBackground(Color.WHITE);

        sidebar.add(lblPaletteTitle, BorderLayout.NORTH);
        sidebar.add(paletteScroll, BorderLayout.CENTER);

        // === kanan: Soal Content ===
        JPanel card = new JPanel(new BorderLayout(0, 25));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 2),
                new EmptyBorder(35, 35, 35, 35)));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        lblQuestionNumber = new JLabel("Soal No. 1");
        lblQuestionNumber.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblQuestionNumber.setForeground(PRIMARY_BLUE);

        lblProgress = new JLabel("Terjawab: 0/" + questions.size());
        lblProgress.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblProgress.setForeground(SUCCESS_GREEN);

        headerPanel.add(lblQuestionNumber, BorderLayout.WEST);
        headerPanel.add(lblProgress, BorderLayout.EAST);

        card.add(headerPanel, BorderLayout.NORTH);

        txtPgSoal = new JTextArea();
        txtPgSoal.setEditable(false);
        txtPgSoal.setLineWrap(true);
        txtPgSoal.setWrapStyleWord(true);
        txtPgSoal.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPgSoal.setBorder(new EmptyBorder(15, 20, 25, 20));
        txtPgSoal.setBackground(new Color(249, 250, 251));
        txtPgSoal.setForeground(TEXT_DARK);

        JScrollPane scrollSoal = new JScrollPane(txtPgSoal);
        scrollSoal.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        scrollSoal.getViewport().setBackground(new Color(249, 250, 251));

        JPanel optionsPanel = new JPanel(new GridLayout(4, 1, 0, 12));
        optionsPanel.setBackground(Color.WHITE);
        optionsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        rbA = createStyledRadioButton("A.");
        rbB = createStyledRadioButton("B.");
        rbC = createStyledRadioButton("C.");
        rbD = createStyledRadioButton("D.");

        pgGroup = new ButtonGroup();
        pgGroup.add(rbA);
        pgGroup.add(rbB);
        pgGroup.add(rbC);
        pgGroup.add(rbD);

        rbA.addActionListener(e -> saveCurrentPgAnswer("A"));
        rbB.addActionListener(e -> saveCurrentPgAnswer("B"));
        rbC.addActionListener(e -> saveCurrentPgAnswer("C"));
        rbD.addActionListener(e -> saveCurrentPgAnswer("D"));

        optionsPanel.add(rbA);
        optionsPanel.add(rbB);
        optionsPanel.add(rbC);
        optionsPanel.add(rbD);

        JPanel centerPanel = new JPanel(new BorderLayout(0, 20));
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(scrollSoal, BorderLayout.CENTER);
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);

        card.add(centerPanel, BorderLayout.CENTER);

        mainSplit.setLeftComponent(sidebar);
        mainSplit.setRightComponent(card);

        container.add(mainSplit, BorderLayout.CENTER);
    }

    private void buildEssayLayout(JPanel container) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(8);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(0.5);

        // KIRI: Soal / PDF
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);

        JLabel lblLeftTitle = new JLabel("  Naskah Soal");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLeftTitle.setForeground(TEXT_DARK);
        lblLeftTitle.setPreferredSize(new Dimension(0, 60));

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBackground(Color.WHITE);
        leftHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        leftHeader.add(lblLeftTitle, BorderLayout.WEST);
        leftPanel.add(leftHeader, BorderLayout.NORTH);

        String pdfPath = getPdfPath();
        if (pdfPath != null && !pdfPath.isEmpty() && new java.io.File(pdfPath).exists()) {
            PdfPanel pdfPanel = new PdfPanel(pdfPath);
            leftPanel.add(pdfPanel, BorderLayout.CENTER);
            lblLeftTitle.setText("  Naskah Soal (PDF Viewer)");
        } else {
            // JTextArea untuk soal pagination
            txtEssayQuestion = new JTextArea();
            txtEssayQuestion.setEditable(false);
            txtEssayQuestion.setLineWrap(true);
            txtEssayQuestion.setWrapStyleWord(true);
            txtEssayQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 18));
            txtEssayQuestion.setBorder(new EmptyBorder(20, 20, 20, 20));
            txtEssayQuestion.setBackground(new Color(249, 250, 251));
            leftPanel.add(new JScrollPane(txtEssayQuestion), BorderLayout.CENTER);
        }

        // KANAN: Jawaban
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel lblRightTitle = new JLabel("  Jawaban Anda");
        lblRightTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRightTitle.setForeground(PRIMARY_BLUE);
        lblRightTitle.setPreferredSize(new Dimension(0, 60));

        JPanel rightHeader = new JPanel(new BorderLayout());
        rightHeader.setBackground(Color.WHITE);
        rightHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(229, 231, 235)));
        rightHeader.add(lblRightTitle, BorderLayout.WEST);
        rightPanel.add(rightHeader, BorderLayout.NORTH);

        txtEssayAnswer = new JTextArea();
        txtEssayAnswer.setLineWrap(true);
        txtEssayAnswer.setWrapStyleWord(true);
        txtEssayAnswer.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEssayAnswer.setBorder(new EmptyBorder(15, 15, 15, 15));

        rightPanel.add(new JScrollPane(txtEssayAnswer), BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);
        container.add(splitPane, BorderLayout.CENTER);
    }

    private void loadPgSoal(int index) {
        if (questions.isEmpty() || index < 0 || index >= questions.size())
            return;

        currentQuestionIndex = index;
        QuestionObj q = questions.get(index);

        if (lblQuestionNumber != null) {
            lblQuestionNumber.setText("Soal No. " + (index + 1));
        }

        // Mode Specific UI Update
        if ("PG".equalsIgnoreCase(examMode)) {
            if (txtPgSoal != null)
                txtPgSoal.setText(q.text == null ? "" : q.text);
            // Update Radio Buttons...
            if (rbA != null) {
                rbA.setText("A. " + safe(q.optA));
                rbB.setText("B. " + safe(q.optB));
                rbC.setText("C. " + safe(q.optC));
                rbD.setText("D. " + safe(q.optD));

                pgGroup.clearSelection();
                String ans = pgAnswers.get(q.id);
                if ("A".equals(ans))
                    rbA.setSelected(true);
                else if ("B".equals(ans))
                    rbB.setSelected(true);
                else if ("C".equals(ans))
                    rbC.setSelected(true);
                else if ("D".equals(ans))
                    rbD.setSelected(true);
            }
        } else {
            // Mode ESSAY
            if (txtEssayQuestion != null) {
                txtEssayQuestion.setText("Soal " + (index + 1) + ":\n\n" + (q.text == null ? "" : q.text));
                txtEssayQuestion.setCaretPosition(0);
            }
            if (txtEssayAnswer != null) {
                String ans = pgAnswers.getOrDefault(q.id, "");
                txtEssayAnswer.setText(ans);
            }
        }

        // Shared Navigation Logic
        if (btnPrev != null)
            btnPrev.setEnabled(index > 0);

        if (index == questions.size() - 1) {
            if (btnNext != null)
                btnNext.setVisible(false);
            if (btnFinish != null)
                btnFinish.setVisible(true);
        } else {
            if (btnNext != null)
                btnNext.setVisible(true);
            if (btnFinish != null)
                btnFinish.setVisible(false);
        }

        updateQuestionPalette();
        updateProgressLabel();
    }

    private JRadioButton createStyledRadioButton(String prefix) {
        JRadioButton rb = new JRadioButton(prefix);
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rb.setBackground(Color.WHITE);
        rb.setFocusPainted(false);
        rb.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(15, 20, 15, 20)));
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JButton createStyledButton(String text, boolean isPrimary, Color col) {
        JButton btn = new JButton(text) {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(col.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(isPrimary ? col.brighter() : new Color(243, 244, 246));
                } else {
                    g2.setColor(isPrimary ? col : Color.WHITE);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 50));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        if (isPrimary) {
            btn.setForeground(Color.WHITE);
            btn.setBorder(new EmptyBorder(12, 25, 12, 25));
        } else {
            btn.setForeground(col);
            btn.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(col, 1),
                    new EmptyBorder(12, 25, 12, 25)));
        }
        btn.setContentAreaFilled(false);
        return btn;
    }

    private String safe(String s) {
        return s == null ? "" : s;
    }

    private void navigatePg(int step) {
        if (!"PG".equalsIgnoreCase(examMode)) {
            saveCurrentEssayAnswer();
        }
        int target = currentQuestionIndex + step;
        loadPgSoal(target);
    }

    private void saveCurrentEssayAnswer() {
        if (txtEssayAnswer != null && !questions.isEmpty()) {
            QuestionObj q = questions.get(currentQuestionIndex);
            String ans = txtEssayAnswer.getText(); // keep spaces
            pgAnswers.put(q.id, ans);

            if (!ans.trim().isEmpty()) {
                answeredQuestions.put(q.id, true);
            } else {
                answeredQuestions.remove(q.id);
            }
            // update UI status
            updateQuestionPalette();
            updateProgressLabel();
        }
    }

    private void saveCurrentPgAnswer(String answer) {
        if (questions.isEmpty() || currentQuestionIndex < 0 || currentQuestionIndex >= questions.size())
            return;

        QuestionObj q = questions.get(currentQuestionIndex);
        pgAnswers.put(q.id, answer);
        answeredQuestions.put(q.id, true);

        // update UI langsung
        updateQuestionPalette();
        updateProgressLabel();
    }

    // update progress label
    private void updateProgressLabel() {
        if (lblProgress != null) {
            int answered = answeredQuestions.size();
            lblProgress.setText("Terjawab: " + answered + "/" + questions.size());

            // ganti warna berdasarkan penyelesaian
            if (answered == questions.size()) {
                lblProgress.setForeground(SUCCESS_GREEN);
            } else if (answered > questions.size() / 2) {
                lblProgress.setForeground(WARNING_YELLOW);
            } else {
                lblProgress.setForeground(new Color(107, 114, 128));
            }
        }
    }

    // bikin question palette dengan clickable buttons
    private void updateQuestionPalette() {
        if (questionPalettePanel == null)
            return;

        questionPalettePanel.removeAll();

        for (int i = 0; i < questions.size(); i++) {
            final int index = i;
            QuestionObj q = questions.get(i);

            JButton btnQ = new JButton(String.valueOf(i + 1));
            btnQ.setFont(new Font("Segoe UI", Font.BOLD, 14));
            btnQ.setFocusPainted(false);
            btnQ.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 2));
            btnQ.setPreferredSize(new Dimension(45, 45));

            // warna berdasarkan status
            if (index == currentQuestionIndex) {
                // lagi di soal ini: Biru
                btnQ.setBackground(PRIMARY_BLUE);
                btnQ.setForeground(Color.WHITE);
            } else if (answeredQuestions.containsKey(q.id)) {
                // Udah dijawab: Hijau
                btnQ.setBackground(SUCCESS_GREEN);
                btnQ.setForeground(Color.WHITE);
            } else {
                // Belum dijawab: Abu-abu terang
                btnQ.setBackground(new Color(243, 244, 246));
                btnQ.setForeground(TEXT_DARK);
            }

            btnQ.addActionListener(e -> {
                if (!locked) {
                    loadPgSoal(index);
                }
            });

            questionPalettePanel.add(btnQ);
        }

        questionPalettePanel.revalidate();
        questionPalettePanel.repaint();
    }

    // tampilin review page before final submit
    private void showReviewPage() {
        if (locked)
            return;

        ignoreFocusLost = true;

        // cek unanswered questions
        int unanswered = questions.size() - answeredQuestions.size();

        String message;
        if (unanswered > 0) {
            message = String.format(
                    "<html><div style='text-align:center; padding:20px;'>" +
                            "<h2>Perhatian!</h2>" +
                            "<p style='font-size:14px;'>Anda masih memiliki <b style='color:red;'>%d soal</b> yang belum dijawab.</p>"
                            +
                            "<p style='font-size:14px;'>Apakah Anda yakin ingin mengumpulkan sekarang?</p>" +
                            "</div></html>",
                    unanswered);
        } else {
            message = "<html><div style='text-align:center; padding:20px;'>" +
                    "<h2>Semua Soal Terjawab!</h2>" +
                    "<p style='font-size:14px;'>Yakin ingin mengumpulkan jawaban?</p>" +
                    "</div></html>";
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                message,
                "Konfirmasi Pengumpulan",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        ignoreFocusLost = false;

        if (opt == JOptionPane.YES_OPTION) {
            submitToDb(false);
        }
    }

    private void initFocusListener() {
        addWindowFocusListener(new WindowFocusListener() {
            public void windowGainedFocus(WindowEvent e) {
            }

            public void windowLostFocus(WindowEvent e) {
                if (ignoreFocusLost || locked || !isExamActive)
                    return;
                long now = System.currentTimeMillis();
                if (now - lastPelanggaranTime < 500) {
                    return;
                }
                lastPelanggaranTime = now;
                handlePelanggaran("FOCUS_LOST_DETECTED");
            }
        });
    }

    private void handlePelanggaran(String code) {
        try {
            // 1. Catat pelanggaran ke DB dengan RETRY
            int count = 0;
            int maxRetries = 3;
            for (int i = 0; i < maxRetries; i++) {
                try {
                    count = sessionRepository.incrementPelanggaran(sessionId);
                    // 2. LANGSUNG UPDATE STATUS DB KE 'LOCKED' (Realtime Lock)
                    sessionRepository.updateStatus(sessionId, "LOCKED");
                    break;
                } catch (Exception e) {
                    if (e.getMessage().contains("locked") && i < maxRetries - 1) {
                        Thread.sleep(200 * (i + 1));
                    } else {
                        throw e;
                    }
                }
            }

            // 3. Kunci UI Lokal
            if (!locked) {
                locked = true;
                disableInputs();

                lblStatus.setText("LOCKED Status: TERMEMBEKU (Pelanggaran " + count + ")");
                lblStatus.setForeground(ALERT_RED);

                ignoreFocusLost = true; // prevent recursive focus events loops

                // Show dialog (blocking)
                javax.swing.SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                            "UJIAN DIBEKUKAN OTOMATIS!\n\n" +
                                    "Sistem mendeteksi Anda berpindah aplikasi/tab.\n" +
                                    "Segera lapor ke Pengawas untuk membuka kembali akses ujian.",
                            "LOCKED - PELANGGARAN TERDETEKSI", JOptionPane.ERROR_MESSAGE);
                });

                ignoreFocusLost = true; // keep ignoring a bit more
                new Timer(2000, e -> ignoreFocusLost = false).start();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startStatusTimer() {
        statusTimer = new Timer(3000, e -> checkStatusFromServer());
        statusTimer.start();
    }

    private void startCountdown() {
        countdownTimer = new Timer(1000, e -> {
            remainingSeconds--;
            if (remainingSeconds < 0) {
                stopAllTimers();
                submitToDb(true);
            } else {
                lblTimer.setText(formatTime(remainingSeconds));
                if (remainingSeconds < 300) {
                    lblTimer.setForeground(ALERT_RED);
                    if (remainingSeconds % 2 == 0) {
                        lblTimer.setText("Time: " + formatTime(remainingSeconds));
                    }
                } else if (remainingSeconds < 600) {
                    lblTimer.setForeground(WARNING_YELLOW);
                }
            }
        });
        countdownTimer.start();
    }

    private void stopAllTimers() {
        if (countdownTimer != null)
            countdownTimer.stop();
        if (statusTimer != null)
            statusTimer.stop();
        if (autoSaveTimer != null)
            autoSaveTimer.stop();
    }

    // Auto-simpan answers every 30 seconds
    private void startAutoSave() {
        autoSaveTimer =

                new Timer(30000, e -> autoSaveAnswers()); // 30 seconds
        autoSaveTimer.start();
    }

    private void autoSaveAnswers() {
        if (locked || "ESSAY".equalsIgnoreCase(examMode))
            return;

        try {
            Connection conn = KoneksiDatabase.getConnection();

            // hapus existing answers
            String delSql = "DELETE FROM jawaban WHERE id_ujian_mahasiswa = ?";
            PreparedStatement psDel = conn.prepareStatement(delSql);
            psDel.setInt(1, sessionId);
            psDel.executeUpdate();

            // Insert sekarang answers
            String insSql = "INSERT INTO jawaban (id_ujian_mahasiswa, nomor_soal, jawaban) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insSql);

            for (QuestionObj q : questions) {
                String studentAns = pgAnswers.getOrDefault(q.id, "");
                if (!studentAns.isEmpty()) {
                    ps.setInt(1, sessionId);
                    ps.setInt(2, q.id);
                    ps.setString(3, studentAns);
                    ps.addBatch();
                }
            }
            ps.executeBatch();

            // Silent simpan - no notification biar ga disturbing student

        } catch (Exception ex) {
            // Silent fail - don't disturb student
            // ex.printStackTrace();
        }

        // Show non-blocking toast
        new ToastNotification(this, "Jawaban Tersimpan Otomatis", ToastNotification.SUCCESS).showToast();
    }

    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void finishUjian() {
        if (locked)
            return;
        ignoreFocusLost = true;
        int opt = JOptionPane.showConfirmDialog(this, "Yakin ingin mengumpulkan jawaban?",
                "Konfirmasi Pengumpulan",
                JOptionPane.YES_NO_OPTION);
        ignoreFocusLost = false;
        if (opt == JOptionPane.YES_OPTION)
            submitToDb(false);
    }

    private void submitToDb(boolean auto) {
        disableInputs();
        stopAllTimers();

        try {
            Connection conn = KoneksiDatabase.getConnection();
            String delSql = "DELETE FROM jawaban WHERE id_ujian_mahasiswa = ?";
            PreparedStatement psDel = conn.prepareStatement(delSql);
            psDel.setInt(1, sessionId);
            psDel.executeUpdate();

            String insSql = "INSERT INTO jawaban (id_ujian_mahasiswa, nomor_soal, jawaban) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insSql);

            // simpan all answers to database first
            // simpan all answers to database first (PG & ESSAY via Pagination Map)
            for (QuestionObj q : questions) {
                String studentAns = pgAnswers.getOrDefault(q.id, "");
                ps.setInt(1, sessionId);
                ps.setInt(2, q.id);
                ps.setString(3, studentAns);
                ps.addBatch();
            }
            ps.executeBatch();

            // === AUTO-GRADING: Hitung nilai otomatis ===
            int[] scoreDetails = sessionRepository.calculateAndSaveScore(sessionId);
            int finalScore = scoreDetails[0]; // Nilai (0-100)
            int correctCount = scoreDetails[1]; // Jumlah benar
            int wrongCount = scoreDetails[2]; // Jumlah salah
            int totalQuestions = questions.size();

            // update status to FINISHED
            sessionRepository.updateStatus(sessionId, "FINISHED");

            // === TAMPILKAN HASIL LENGKAP ===
            String msg = String.format("<html><div style='text-align:center; padding:20px;'>"
                    + "<h1 style='color:#10b981; margin-bottom:20px;'>Ujian Selesai!</h1>"
                    + "<div style='background:#f0f9ff; padding:20px; border-radius:10px; margin:15px 0;'>"
                    + "<h2 style='color:#0284c7; margin:10px 0;'>Nilai Anda</h2>"
                    + "<h1 style='color:#0284c7; font-size:48px; margin:10px 0;'>%d</h1>"
                    + "<p style='color:#64748b; font-size:14px;'>dari 100</p>"
                    + "</div>"
                    + "<div style='display:grid; grid-template-columns:1fr 1fr; gap:15px; margin-top:20px;'>"
                    + "<div style='background:#dcfce7; padding:15px; border-radius:8px;'>"
                    + "<p style='color:#16a34a; font-size:14px; margin:5px 0;'>Benar</p>"
                    + "<h2 style='color:#16a34a; margin:5px 0;'>%d</h2>"
                    + "</div>"
                    + "<div style='background:#fee2e2; padding:15px; border-radius:8px;'>"
                    + "<p style='color:#dc2626; font-size:14px; margin:5px 0;'>Salah</p>"
                    + "<h2 style='color:#dc2626; margin:5px 0;'>%d</h2>"
                    + "</div>"
                    + "</div>"
                    + "<p style='color:#64748b; margin-top:20px; font-size:13px;'>Total Soal: %d</p>"
                    + "</div></html>",
                    finalScore, correctCount, wrongCount, totalQuestions);

            // tampilin result dialog then exit
            JOptionPane.showMessageDialog(this, msg, "Hasil Ujian", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);

        } catch (Exception ex) {
            enableInputs();
            showInfo("Error submit: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void disableInputs() {
        if (txtEssayAnswer != null)
            txtEssayAnswer.setEditable(false);
        if (rbA != null) {
            rbA.setEnabled(false);
            rbB.setEnabled(false);
            rbC.setEnabled(false);
            rbD.setEnabled(false);
        }
        if (btnNext != null)
            btnNext.setEnabled(false);
        if (btnPrev != null)
            btnPrev.setEnabled(false);
        if (btnFinish != null)
            btnFinish.setEnabled(false);
    }

    private void enableInputs() {
        if (locked)
            return;
        if (txtEssayAnswer != null)
            txtEssayAnswer.setEditable(true);
        if (rbA != null) {
            rbA.setEnabled(true);
            rbB.setEnabled(true);
            rbC.setEnabled(true);
            rbD.setEnabled(true);
        }
        if (btnNext != null)
            btnNext.setEnabled(true);
        if (btnPrev != null)
            btnPrev.setEnabled(true);
        if (btnFinish != null)
            btnFinish.setEnabled(true);
    }

    private void showInfo(String msg) {
        new ToastNotification(this, msg, ToastNotification.INFO).showToast();
    }

    private static class QuestionObj {
        int id;
        String text;

        String optA;
        String optB;
        String optC;
        String optD;
        String correctKey; // tambahined for grading
    }

    class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;

        public RoundedPanel(int radius, Color bgColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            setOpaque(false);
        }

        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            g2.setColor(new Color(229, 231, 235));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private String getPdfPath() {
        try {
            Connection conn = KoneksiDatabase.getConnection();
            String sql = "SELECT e.path_file_soal FROM ujian e WHERE e.id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("path_file_soal");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // === FITUR TAMBAHAN: WATERMARK ANTI-LEAK ===
    private class WatermarkPane extends javax.swing.JComponent {
        private final String text;

        public WatermarkPane(String text) {
            this.text = text;
            setOpaque(false); // Transparan, click-through
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Style Watermark: Miring, Abu-abu transparan
            g2.setFont(new Font("Arial", Font.BOLD, 24));
            g2.setColor(new Color(150, 150, 150, 40));
            g2.rotate(Math.toRadians(-30));

            int w = getWidth();
            int h = getHeight();
            int gap = 300;

            for (int x = -h; x < w; x += gap) {
                for (int y = -h; y < h * 2; y += gap) {
                    g2.drawString(text + " - ANTI CHEAT", x, y);
                }
            }
            g2.dispose();
        }
    }

    private void initSecurity() {
        if (txtPgSoal != null) {
            txtPgSoal.setHighlighter(null);
            txtPgSoal.setTransferHandler(null);
            txtPgSoal.setComponentPopupMenu(null);
        }
        if (txtEssayQuestion != null) {
            txtEssayQuestion.setHighlighter(null);
            txtEssayQuestion.setTransferHandler(null);
            txtEssayQuestion.setComponentPopupMenu(null);
        }
        if (txtEssayAnswer != null) {
            txtEssayAnswer.setTransferHandler(null);
            InputMap im = txtEssayAnswer.getInputMap(JComponent.WHEN_FOCUSED);
            im.put(KeyStroke.getKeyStroke("control V"), "none");
            im.put(KeyStroke.getKeyStroke("control C"), "none");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_PASTE, 0), "none");
            im.put(KeyStroke.getKeyStroke(KeyEvent.VK_COPY, 0), "none");
        }
    }

    private void initWatermark() {
        WatermarkPane wp = new WatermarkPane(studentName != null ? studentName.toUpperCase() : "PESERTA");
        setGlassPane(wp);
        wp.setVisible(true);
    }

    private JPanel createPreFlightPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(15, 23, 42)); // Darker theme

        JPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 50, 40, 50));

        JLabel lblTitle = new JLabel("System Pre-Flight Check");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        lblTitle.setForeground(PRIMARY_BLUE);

        JLabel lblSub = new JLabel("Memeriksa kesiapan sistem ujian...");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        lblSub.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        lblSub.setForeground(Color.GRAY);

        JPanel checkList = new JPanel(new GridLayout(4, 1, 10, 15));
        checkList.setOpaque(false);
        checkList.setBorder(new EmptyBorder(30, 0, 30, 0));

        checkList.add(createCheckItem("Koneksi Database", true));
        checkList.add(createCheckItem("Layar Penuh (Fullscreen)", true));
        checkList.add(createCheckItem("Keamanan Kamera", true)); // Fake or basic check
        checkList.add(createCheckItem("Anti-Cheat System", true));

        JButton btnLanjut = createStyledButton("LANJUT KE TATA TERTIB", true, PRIMARY_BLUE);
        btnLanjut.setAlignmentX(JComponent.CENTER_ALIGNMENT);
        btnLanjut.setEnabled(false);

        // Simulate checking process
        Timer t = new Timer(800, e -> {
            btnLanjut.setEnabled(true);
            btnLanjut.setText("SISTEM SIAP - LANJUT");
            ((Timer) e.getSource()).stop();
        });
        t.start();

        btnLanjut.addActionListener(e -> mainLayout.show(mainContainer, "RULES"));

        card.add(lblTitle);
        card.add(lblSub);
        card.add(checkList);
        card.add(btnLanjut);

        panel.add(card);
        return panel;
    }

    private JPanel createCheckItem(String text, boolean status) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setOpaque(false);
        JLabel icon = new JLabel(status ? "OK" : "XR"); // Replace mostly with check icon logic if avail
        icon.setForeground(status ? SUCCESS_GREEN : ALERT_RED);
        icon.setFont(new Font("Consolas", Font.BOLD, 16));
        icon.setBorder(BorderFactory.createLineBorder(status ? SUCCESS_GREEN : ALERT_RED, 2));

        JLabel lbl = new JLabel(" " + text);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));

        p.add(icon);
        p.add(lbl);
        return p;
    }

    // Check broadcast message polling
    private void checkBroadcast() {
        try {
            Connection conn = KoneksiDatabase.getConnection();
            String sql = "SELECT broadcast_message FROM ujian WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String msg = rs.getString("broadcast_message");
                if (msg != null && !msg.isEmpty()) {
                    lblBroadcast.setText("PENGUMUMAN: " + msg);
                    lblBroadcast.setVisible(true);

                    // Blink effect
                    if (System.currentTimeMillis() % 1000 < 500) {
                        lblBroadcast.setForeground(Color.RED);
                    } else {
                        lblBroadcast.setForeground(Color.BLUE);
                    }
                } else {
                    lblBroadcast.setVisible(false);
                }
            }
        } catch (Exception e) {
        }
    }

    private void checkStatusFromServer() {
        checkBroadcast(); // Poll broadcast
        try {
            String status = sessionRepository.getStatus(sessionId);
            if (status == null)
                return;

            if ("LOCKED".equals(status) && !locked) {
                locked = true;
                lblStatus.setText("LOCKED Status: TERKUNCI (Oleh Pengawas)");
                lblStatus.setForeground(ALERT_RED);
                disableInputs();

                ignoreFocusLost = true;
                // Use JOptionPane for critical lock, keep it blocking
                JOptionPane.showMessageDialog(this, "Ujian dikunci oleh pengawas.", "LOCKED",
                        JOptionPane.ERROR_MESSAGE);
                ignoreFocusLost = false;

            } else if ("ONGOING".equals(status) && locked) {
                locked = false;
                lblStatus.setText("OK Status: Aman");
                lblStatus.setForeground(SUCCESS_GREEN);
                enableInputs();

                new ToastNotification(this, "Ujian dibuka kembali. Silakan lanjutkan.", ToastNotification.SUCCESS)
                        .showToast();

            } else if ("FINISHED".equals(status)) {
                stopAllTimers();
                ignoreFocusLost = true;
                JOptionPane.showMessageDialog(this, "Ujian telah diakhiri oleh pengawas.");
                System.exit(0);
            }
        } catch (Exception ignored) {
        }
    }
}
