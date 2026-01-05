package id.ac.campus.antiexam.ui.ux.mahasiswa;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.awt.Image;
import javax.swing.ImageIcon;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class BerandaMahasiswaFrame extends JFrame {

    // Neo-Brutalism Palette
    private final Color SIDEBAR_COLOR = new Color(88, 101, 242); // Neo Blue
    private final Color BG_COLOR = new Color(248, 250, 252);
    private final Color TEXT_COLOR = Color.BLACK;
    private final Color PRIMARY_BLUE = new Color(88, 101, 242);
    private final Color MUTED_TEXT = Color.DARK_GRAY;

    private final Color STATUS_READY = new Color(16, 185, 129);
    private final Color STATUS_ONGOING = new Color(59, 130, 246);
    private final Color STATUS_FINISHED = new Color(239, 68, 68);
    private final String LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    private int studentId;
    private String studentName;
    private String studentNumber;
    private String studentClass;
    private final id.ac.campus.antiexam.data.SesiUjianData sessionRepo = new id.ac.campus.antiexam.data.SesiUjianData();
    private final List<CourseItem> courseItems = new ArrayList<>();

    public BerandaMahasiswaFrame() {
        this(1);
    }

    public BerandaMahasiswaFrame(int studentId) {
        this.studentId = studentId;
        setTitle("SiUjian - Student Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setUndecorated(true);
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            setIconImage(icon.getImage());
        } catch (Exception ignored) {
        }
        initData();
        initUI();
    }

    private void initData() {
        loadStudentProfile();
        loadStudentExams();
    }

    private void loadStudentProfile() {
        String sql = "SELECT name, nim, nama_kelas FROM mahasiswa WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    studentName = rs.getString("name");
                    studentNumber = rs.getString("nim");
                    studentClass = rs.getString("nama_kelas");
                } else {
                    studentName = "Mahasiswa";
                    studentNumber = "";
                    studentClass = "";
                }
            }
        } catch (Exception e) {
            studentName = "Mahasiswa";
            studentNumber = "";
            studentClass = "";
        }
    }

    private void loadStudentExams() {
        courseItems.clear();
        String sql = "SELECT e.id AS exam_original_id, se.id AS student_exam_id, e.title AS exam_title, e.token AS exam_code, e.target_kelas AS target_class, e.ruangan AS room, e.jadwal_waktu AS scheduled_at, e.status, s.name AS subject_name "
                + "FROM ujian e "
                + "LEFT JOIN ujian_mahasiswa se ON e.id = se.id_ujian AND se.id_mahasiswa = ? "
                + "LEFT JOIN mata_kuliah s ON e.kode_matkul = s.kode_matkul "
                + "WHERE (e.target_kelas = ? OR e.status = 'ONGOING') "
                + "ORDER BY e.status, e.jadwal_waktu DESC";

        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, studentId);
            ps.setString(2, studentClass);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    CourseItem item = new CourseItem();
                    item.studentExamId = rs.getInt("student_exam_id");
                    item.examOriginalId = rs.getInt("exam_original_id");
                    item.examTitle = rs.getString("exam_title");
                    item.examCode = rs.getString("exam_code");
                    item.subjectName = rs.getString("subject_name");
                    item.targetClass = rs.getString("target_class");
                    item.room = rs.getString("room");
                    item.schedule = rs.getString("scheduled_at");
                    item.status = rs.getString("status");
                    courseItems.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel sidebar = createSidebar();
        mainPanel.add(sidebar, BorderLayout.WEST);

        // Dot Pattern background buat content
        JPanel content = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(BG_COLOR);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(new Color(226, 232, 240));
                for (int x = 0; x < getWidth(); x += 30) {
                    for (int y = 0; y < getHeight(); y += 30) {
                        g2.fillOval(x, y, 3, 3);
                    }
                }
            }
        };
        // Populate content
        JPanel innerContent = createContentPanel();
        innerContent.setOpaque(false);
        content.add(innerContent, BorderLayout.CENTER);

        mainPanel.add(content, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BorderLayout());
        sidebar.setBackground(SIDEBAR_COLOR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Color.BLACK)); // border tebel

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        logoPanel.setOpaque(false);
        logoPanel.setBorder(new EmptyBorder(0, 10, 40, 10));

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            Image img = icon.getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            lblLogo.setText("[LOGO]");
            lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 32));
        }

        JLabel lblBrand = new JLabel("SiUjian");
        lblBrand.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblBrand.setForeground(Color.WHITE);
        lblBrand.setBorder(new EmptyBorder(0, 15, 0, 0));

        logoPanel.add(lblLogo);
        logoPanel.add(lblBrand);

        JPanel profilePanel = new JPanel(new GridBagLayout());
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(20, 0, 40, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 20, 0);

        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int size = Math.min(getWidth(), getHeight());
                // Avatar dengan hard border
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, size - 2, size - 2);
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawOval(0, 0, size - 2, size - 2);

                g2.setColor(Color.BLACK);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 32));
                String name = studentName != null && !studentName.isEmpty() ? studentName.trim() : "M";
                String initial = name.substring(0, 1).toUpperCase();
                int textWidth = g2.getFontMetrics().stringWidth(initial);
                int textHeight = g2.getFontMetrics().getHeight();
                g2.drawString(initial, (size - textWidth) / 2, (size + textHeight / 2) / 2 - 4);
            }
        };
        avatar.setPreferredSize(new Dimension(100, 100));
        avatar.setOpaque(false);
        profilePanel.add(avatar, gbc);

        JLabel lblName = new JLabel(studentName != null ? studentName : "Mahasiswa");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblName.setForeground(Color.WHITE);

        gbc.gridy = 1;
        gbc.insets = new Insets(10, 0, 5, 0);
        profilePanel.add(lblName, gbc);

        String roleText = "Mahasiswa Aktif";
        if (studentClass != null && !studentClass.isEmpty()) {
            roleText = "Kelas " + studentClass;
        }
        JLabel lblRole = new JLabel(roleText);
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(224, 231, 255));

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 5, 0);
        profilePanel.add(lblRole, gbc);

        if (studentNumber != null && !studentNumber.isEmpty()) {
            JLabel lblNim = new JLabel("NIM " + studentNumber);
            lblNim.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            lblNim.setForeground(new Color(203, 213, 225));
            gbc.gridy = 3;
            gbc.insets = new Insets(0, 0, 15, 0);
            profilePanel.add(lblNim, gbc);
        }

        JButton btnLogout = new NeoButton("Keluar Sesi", Color.WHITE, Color.BLACK);
        btnLogout.addActionListener(e -> {
            new PilihPeranFrame().setVisible(true);
            dispose();
        });

        gbc.gridy = 4;
        gbc.insets = new Insets(10, 0, 0, 0);
        profilePanel.add(btnLogout, gbc);

        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(profilePanel, BorderLayout.CENTER);

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        footerPanel.setOpaque(false);
        footerPanel.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel lblFooter = new JLabel("(c) 2025 SiUjian v2.0");
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(203, 213, 225));
        footerPanel.add(lblFooter);

        sidebar.add(footerPanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createContentPanel() {
        JPanel content = new JPanel(new BorderLayout());
        content.setOpaque(false);
        content.setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 30, 0));

        JLabel lblTitle = new JLabel("Dashboard Mahasiswa");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblTitle.setForeground(TEXT_COLOR);

        String subtitleText = "Pilih mata kuliah untuk mengikuti ujian";
        if (studentClass != null && !studentClass.isEmpty()) {
            subtitleText = "Daftar ujian untuk kelas " + studentClass;
        }
        JLabel lblSubtitle = new JLabel(subtitleText);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSubtitle.setForeground(MUTED_TEXT);

        JPanel titlePanel = new JPanel(new GridLayout(2, 1, 0, 5));
        titlePanel.setOpaque(false);
        titlePanel.add(lblTitle);
        titlePanel.add(lblSubtitle);

        JLabel lblWelcome = new JLabel(
                "Selamat Datang, " + (studentName != null ? studentName.split(" ")[0] : "Mahasiswa") + "!");
        lblWelcome.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblWelcome.setForeground(PRIMARY_BLUE);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        headerRight.setOpaque(false);

        JButton btnRefresh = new NeoButton("Refresh", Color.WHITE, PRIMARY_BLUE);
        btnRefresh.setPreferredSize(new Dimension(120, 40));
        btnRefresh.addActionListener(e -> {
            loadStudentExams();
            content.remove(1); // ilangin old grid/empty panel (index 1)
            // Re-add content based on new data
            if (courseItems.isEmpty()) {
                JPanel emptyPanel = new JPanel(new BorderLayout());
                emptyPanel.setOpaque(false);
                JLabel lblEmpty = new JLabel("Belum ada jadwal ujian untuk Anda.", JLabel.CENTER);
                lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                lblEmpty.setForeground(MUTED_TEXT);
                emptyPanel.add(lblEmpty, BorderLayout.CENTER);
                content.add(emptyPanel, BorderLayout.CENTER);
            } else {
                int cols = courseItems.size() >= 3 ? 3 : courseItems.size();
                if (cols <= 0)
                    cols = 1;
                int rows = (int) Math.ceil(courseItems.size() / (double) cols);
                JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 30, 30));
                gridPanel.setOpaque(false);
                int index = 0;
                for (CourseItem item : courseItems) {
                    String icon = pickIcon(index);
                    gridPanel.add(createCourseCard(item, icon));
                    index++;
                }
                content.add(gridPanel, BorderLayout.CENTER);
            }
            content.revalidate();
            content.repaint();
        });

        headerRight.add(btnRefresh);
        headerRight.add(lblWelcome);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(headerRight, BorderLayout.EAST);

        content.add(header, BorderLayout.NORTH);

        if (courseItems.isEmpty()) {
            JPanel emptyPanel = new JPanel(new BorderLayout());
            emptyPanel.setOpaque(false);
            JLabel lblEmpty = new JLabel("Belum ada jadwal ujian untuk Anda.", JLabel.CENTER);
            lblEmpty.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lblEmpty.setForeground(MUTED_TEXT);
            emptyPanel.add(lblEmpty, BorderLayout.CENTER);
            content.add(emptyPanel, BorderLayout.CENTER);
        } else {
            int cols = courseItems.size() >= 3 ? 3 : courseItems.size();
            if (cols <= 0)
                cols = 1;
            int rows = (int) Math.ceil(courseItems.size() / (double) cols);
            JPanel gridPanel = new JPanel(new GridLayout(rows, cols, 30, 30));
            gridPanel.setOpaque(false);
            int index = 0;
            for (CourseItem item : courseItems) {
                String icon = pickIcon(index);
                gridPanel.add(createCourseCard(item, icon));
                index++;
            }
            content.add(gridPanel, BorderLayout.CENTER);
        }

        return content;
    }

    private String pickIcon(int index) {
        String[] icons = new String[] { "[EXAM]", "[QUIZ]", "[TEST]", "[LAB]", "[MID]", "[FINAL]", "[CODE]" };
        return icons[index % icons.length];
    }

    private JPanel createCourseCard(CourseItem item, String icon) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // tepi tajam

                // shadow
                g2.setColor(Color.BLACK);
                g2.fillRect(6, 6, getWidth() - 6, getHeight() - 6);

                // White Card Body
                g2.setColor(Color.WHITE);
                g2.fillRect(0, 0, getWidth() - 6, getHeight() - 6);

                // border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(3));
                g2.drawRect(1, 1, getWidth() - 8, getHeight() - 8);
            }
        };
        card.setLayout(new BorderLayout());
        card.setOpaque(false);
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        card.setBorder(new EmptyBorder(25, 25, 25, 25));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 36));

        JLabel lblCode = new JLabel(""); // Token hidden as per request
        lblCode.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblCode.setForeground(PRIMARY_BLUE);
        lblCode.setHorizontalAlignment(JLabel.RIGHT);

        topPanel.add(lblIcon, BorderLayout.WEST);
        topPanel.add(lblCode, BorderLayout.EAST);

        String mainTitle = item.subjectName != null && !item.subjectName.isEmpty() ? item.subjectName : item.examTitle;
        JLabel lblTitle = new JLabel(mainTitle, JLabel.LEFT);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(TEXT_COLOR);
        lblTitle.setBorder(new EmptyBorder(15, 0, 5, 0));

        String subLine = item.examTitle != null ? item.examTitle : "Ujian";
        JLabel lblUjian = new JLabel(subLine);
        lblUjian.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblUjian.setForeground(MUTED_TEXT);

        JLabel lblMeta = new JLabel();
        StringBuilder meta = new StringBuilder();
        if (item.targetClass != null && !item.targetClass.isEmpty()) {
            meta.append("Kelas ").append(item.targetClass);
        }
        if (item.room != null && !item.room.isEmpty()) {
            if (meta.length() > 0)
                meta.append(" â€¢ ");
            meta.append("Ruangan ").append(item.room);
        }
        if (item.schedule != null && !item.schedule.isEmpty()) {
            if (meta.length() > 0)
                meta.append(" â€¢ ");
            meta.append(item.schedule);
        }
        lblMeta.setText(meta.toString());
        lblMeta.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblMeta.setForeground(MUTED_TEXT);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 4, 0));
        infoPanel.setOpaque(false);
        infoPanel.add(lblTitle);
        infoPanel.add(lblUjian);
        infoPanel.add(lblMeta);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        statusPanel.setOpaque(false);
        JLabel lblStatus = new JLabel(item.status != null ? item.status : "-");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblStatus.setBorder(new EmptyBorder(4, 10, 4, 10));
        Color bg = STATUS_READY;
        Color fg = new Color(6, 95, 70);
        if ("ONGOING".equalsIgnoreCase(item.status)) {
            bg = STATUS_ONGOING;
            fg = Color.WHITE;
        } else if ("FINISHED".equalsIgnoreCase(item.status)) {
            bg = STATUS_FINISHED;
            fg = Color.WHITE;
        }
        lblStatus.setOpaque(true);
        lblStatus.setBackground(bg);
        lblStatus.setForeground(fg);
        // tambahin minimal border to status badge
        lblStatus.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 1),
                new EmptyBorder(4, 10, 4, 10)));

        statusPanel.add(lblStatus);

        JPanel centerWrap = new JPanel(new BorderLayout());
        centerWrap.setOpaque(false);
        centerWrap.add(infoPanel, BorderLayout.CENTER);
        centerWrap.add(statusPanel, BorderLayout.SOUTH);

        card.add(topPanel, BorderLayout.NORTH);
        card.add(centerWrap, BorderLayout.CENTER);

        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showTokenDialog(item);
            }
        });

        return card;
    }

    private void showTokenDialog(CourseItem course) {
        JDialog dialog = new JDialog(this, "Masukkan Token Ujian", true);
        dialog.setUndecorated(true);
        dialog.setSize(450, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 25, 5, 25);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;

        JLabel lblHeader = new JLabel("Masukkan Token");
        lblHeader.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblHeader.setForeground(TEXT_COLOR);

        gbc.gridy = 0;
        gbc.insets = new Insets(25, 25, 15, 25);
        panel.add(lblHeader, gbc);

        String courseName = course.subjectName != null && !course.subjectName.isEmpty() ? course.subjectName
                : course.examTitle;
        JLabel lblCourse = new JLabel("Mata Kuliah: " + courseName);
        lblCourse.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblCourse.setForeground(Color.BLACK);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 25, 20, 25);
        panel.add(lblCourse, gbc);

        JLabel lblLabel = new JLabel("Token Akses Ujian");
        lblLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblLabel.setForeground(Color.BLACK);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 25, 8, 25);
        panel.add(lblLabel, gbc);

        JPasswordField txtToken = new JPasswordField();
        txtToken.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtToken.setPreferredSize(new Dimension(350, 45));
        txtToken.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(Color.BLACK, 2),
                new EmptyBorder(10, 15, 10, 15)));
        txtToken.setBackground(Color.WHITE);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 25, 25, 25);
        panel.add(txtToken, gbc);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setOpaque(false);

        JButton btnCancel = new NeoButton("Batal", Color.WHITE, Color.BLACK);
        btnCancel.setPreferredSize(new Dimension(100, 40));
        btnCancel.addActionListener(e -> dialog.dispose());

        JButton btnSubmit = new NeoButton("Masuk Ujian", PRIMARY_BLUE, Color.WHITE);
        btnSubmit.setPreferredSize(new Dimension(150, 40));

        btnSubmit.addActionListener(e -> {
            String token = new String(txtToken.getPassword()).trim();

            if (token.isEmpty()) {
                javax.swing.JOptionPane.showMessageDialog(dialog, "Token tidak boleh kosong!", "Peringatan",
                        javax.swing.JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Fetch real-time token from DB to avoid stale data
            String serverToken = null;
            try (Connection conn = KoneksiDatabase.getConnection()) {
                PreparedStatement ps = conn.prepareStatement("SELECT token FROM ujian WHERE id = ?");
                ps.setInt(1, course.examOriginalId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    serverToken = rs.getString("token");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            // Validasi Token against Real-Time Database Token
            if (serverToken == null || !token.equalsIgnoreCase(serverToken.trim())) {
                javax.swing.JOptionPane.showMessageDialog(dialog,
                        "Token Salah! Harap minta token valid dari Dosen/Pengawas.",
                        "Akses Ditolak", javax.swing.JOptionPane.ERROR_MESSAGE);
                return;
            }

            dialog.dispose();

            // Auto-Join / bikin Session if first time
            if (course.studentExamId == 0) {
                try {
                    // Use spesifik exam creation
                    int newSessionId = sessionRepo.createSessionForUjian(course.examOriginalId, studentId);
                    course.studentExamId = newSessionId;

                    openExamFrame(course, token);
                } catch (Exception ex) {
                    javax.swing.JOptionPane.showMessageDialog(this, "Gagal memulai sesi: " + ex.getMessage());
                }
            } else {
                openExamFrame(course, token);
            }
        });

        btnPanel.add(btnCancel);
        btnPanel.add(btnSubmit);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 25, 25, 25);
        panel.add(btnPanel, gbc);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private void openExamFrame(CourseItem course, String token) {
        SwingUtilities.invokeLater(() -> {
            String name = studentName != null ? studentName : "Mahasiswa";
            UjianMahasiswaFrame examFrame = new UjianMahasiswaFrame(course.studentExamId, name, token);
            examFrame.setVisible(true);
            this.dispose();
        });
    }

    private static class CourseItem {

        int studentExamId;
        int examOriginalId;
        String examTitle;
        String examCode;
        String subjectName;
        String targetClass;
        String room;
        String schedule;
        String status;
    }

    // tombol neo-brutalism Class
    private class NeoButton extends JButton {
        private Color bgColor;
        private Color fgColor;

        public NeoButton(String text, Color bg, Color fg) {
            super(text);
            this.bgColor = bg;
            this.fgColor = fg;
            setFont(new Font("Segoe UI", Font.BOLD, 14));
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // tepi tajam

            // shadow
            if (!getModel().isPressed()) {
                g2.setColor(Color.BLACK);
                g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
            }

            // utama body
            int offset = getModel().isPressed() ? 4 : 0;
            g2.setColor(bgColor);
            g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

            // border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(offset, offset, getWidth() - 6, getHeight() - 6); // sesuaiin buat stroke

            // teks
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> {
            new BerandaMahasiswaFrame(1).setVisible(true);
        });
    }
}
