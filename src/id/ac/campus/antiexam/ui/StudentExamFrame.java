package id.ac.campus.antiexam.ui;

import id.ac.campus.antiexam.config.DBConnection;
import id.ac.campus.antiexam.repository.SessionRepository;

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

public class StudentExamFrame extends JFrame {

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

    private JLabel lblStatus;
    private JLabel lblTimer;

    private JButton btnNext;
    private JButton btnPrev;
    private JButton btnFinish;

    private JTextArea txtEssayAnswer;
    private JTextArea txtPgQuestion;
    private JRadioButton rbA;
    private JRadioButton rbB;
    private JRadioButton rbC;
    private JRadioButton rbD;
    private ButtonGroup pgGroup;
    private JLabel lblQuestionNumber;

    private final SessionRepository sessionRepository = new SessionRepository();
    private Timer statusTimer;
    private Timer countdownTimer;
    private Timer autoSaveTimer; // Auto-save every 30s
    private int remainingSeconds;
    private boolean locked = false;
    private boolean ignoreFocusLost = false;
    private boolean isExamActive = false;
    private long lastViolationTime = 0;

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
    private JPanel questionPalettePanel; // Question overview sidebar
    private JLabel lblProgress; // Progress label

    public StudentExamFrame(int sessionId, String studentName, String token) {
        this.sessionId = sessionId;
        this.studentName = studentName;

        setTitle("SiUjian - Browser Ujian Mahasiswa");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setUndecorated(true);
        setExtendedState(MAXIMIZED_BOTH);
        setIconImage(new ImageIcon(LOGO_PATH).getImage());

        initExamData();
        initUI();
        initFocusListener();
    }

    private void initExamData() {
        try {
            Connection conn = DBConnection.getConnection();

            String sqlExam = "SELECT s.exam_id, e.title, e.duration_min, e.type " +
                    "FROM student_exams s JOIN exams e ON s.exam_id = e.id WHERE s.id = ?";
            PreparedStatement ps = conn.prepareStatement(sqlExam);
            ps.setInt(1, sessionId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                examId = rs.getInt("exam_id");
                examTitle = rs.getString("title");
                durationMinutes = rs.getInt("duration_min");
                examMode = rs.getString("type");
            } else {
                examId = 0;
                examTitle = "Ujian";
                durationMinutes = 60;
                examMode = "ESSAY";
            }

            remainingSeconds = durationMinutes * 60;

            // Force MCQ Mode as requested
            examMode = "PG";

            loadQuestionsFromDb();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal memuat data ujian: " + ex.getMessage());
            ex.printStackTrace(); // Keep for debugging
            System.exit(0);
        }
    }

    private void loadQuestionsFromDb() {
        questions.clear();
        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM exam_questions WHERE exam_id = ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QuestionObj q = new QuestionObj();
                q.id = rs.getInt("id");
                q.text = rs.getString("question_text");
                // Explicitly set type to PG as demanded by context, or read from DB
                // q.type = rs.getString("question_type"); // Field removed in previous step, so
                // irrelevant
                q.optA = rs.getString("option_a");
                q.optB = rs.getString("option_b");
                q.optC = rs.getString("option_c");
                q.optD = rs.getString("option_d");
                q.correctKey = rs.getString("correct_answer");
                questions.add(q);
            }

            if (questions.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Belum ada soal untuk ujian ini. Hubungi Dosen.");
            } else {
                // Pre-fill answers map if resuming (optional, logic needed)
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal memuat soal: " + e.getMessage());
        }
    }

    private void initUI() {
        mainLayout = new CardLayout();
        mainContainer = new JPanel(mainLayout);

        rulesPanel = createRulesPanel();
        examPanel = createExamPanel();

        mainContainer.add(rulesPanel, "RULES");
        mainContainer.add(examPanel, "EXAM");

        add(mainContainer);

        mainLayout.show(mainContainer, "RULES");
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
            lblLogo.setText("🛡️");
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
                "<h2 style='color: #1e40af; margin-bottom: 25px;'>📋 Petunjuk Penting:</h2>" +
                "<div style='text-align: left; margin: 0 auto; width: 90%;'>" +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>1. 🕌 Awali dengan berdoa menurut agama dan kepercayaan masing-masing.</p>"
                +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>2. 🚫 Dilarang keras melakukan kecurangan (mencontek, kerja sama).</p>"
                +
                "<p style='color: #dc2626; font-size: 16px; font-weight: bold; margin-bottom: 12px; background: #fef2f2; padding: 10px; border-radius: 8px;'>3. ⚠️ JANGAN berpindah window atau menekan Alt+Tab. Sistem akan mendeteksi dan mengunci ujian Anda.</p>"
                +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>4. ⏱️ Waktu berjalan mundur otomatis saat Anda menekan tombol mulai.</p>"
                +
                "<p style='color: #4b5563; font-size: 16px; margin-bottom: 12px;'>5. 💾 Jawaban tersimpan otomatis setiap 30 detik.</p>"
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

        JButton btnStart = createStyledButton("🚀 SAYA MENGERTI & MULAI UJIAN", true, PRIMARY_BLUE);
        btnStart.setPreferredSize(new Dimension(300, 55));
        btnStart.addActionListener(e -> startExamSession());

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

    private void startExamSession() {
        mainLayout.show(mainContainer, "EXAM");
        startStatusTimer();
        startCountdown();
        startAutoSave(); // Start auto-save
        if ("PG".equalsIgnoreCase(examMode) && !questions.isEmpty()) {
            loadPgQuestion(0);
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
            lblLogo.setText("🛡️");
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
        bottomBar.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        statusPanel.setBackground(Color.WHITE);

        lblStatus = new JLabel("Status: Aman");
        lblStatus.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblStatus.setForeground(SUCCESS_GREEN);
        lblStatus.setIconTextGap(10);

        JLabel lblInfo = new JLabel("🟢 Sistem aktif | ⚠️ Jangan pindah window");
        lblInfo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblInfo.setForeground(new Color(107, 114, 128));

        statusPanel.add(lblStatus);
        statusPanel.add(lblInfo);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        btnPanel.setBackground(Color.WHITE);

        if ("PG".equalsIgnoreCase(examMode)) {
            btnPrev = createStyledButton("⬅ Sebelumnya", false, new Color(107, 114, 128));
            btnNext = createStyledButton("Selanjutnya ➡", true, PRIMARY_BLUE);

            btnPrev.addActionListener(e -> navigatePg(-1));
            btnNext.addActionListener(e -> navigatePg(1));

            btnPanel.add(btnPrev);
            btnPanel.add(btnNext);
        }

        btnFinish = createStyledButton("✅ Kirim Jawaban", true, SUCCESS_GREEN);
        btnFinish.addActionListener(e -> showReviewPage());

        // Show finish button only on last question for PG
        if ("PG".equalsIgnoreCase(examMode)) {
            btnFinish.setVisible(false); // Will be shown on last question
        }

        btnPanel.add(btnFinish);

        bottomBar.add(statusPanel, BorderLayout.WEST);
        bottomBar.add(btnPanel, BorderLayout.EAST);

        root.add(topBar, BorderLayout.NORTH);
        root.add(content, BorderLayout.CENTER);
        root.add(bottomBar, BorderLayout.SOUTH);

        return root;
    }

    private void buildPgLayout(JPanel container) {
        // Main split: Left sidebar (question palette) + Right content
        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplit.setResizeWeight(0.2); // 20% for sidebar
        mainSplit.setDividerSize(2);
        mainSplit.setBorder(null);

        // === LEFT: Question Palette ===
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(Color.WHITE);
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(229, 231, 235)),
                new EmptyBorder(20, 15, 20, 15)));

        JLabel lblPaletteTitle = new JLabel("📋 Daftar Soal");
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

        // === RIGHT: Question Content ===
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

        txtPgQuestion = new JTextArea();
        txtPgQuestion.setEditable(false);
        txtPgQuestion.setLineWrap(true);
        txtPgQuestion.setWrapStyleWord(true);
        txtPgQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        txtPgQuestion.setBorder(new EmptyBorder(15, 20, 25, 20));
        txtPgQuestion.setBackground(new Color(249, 250, 251));
        txtPgQuestion.setForeground(TEXT_DARK);

        JScrollPane scrollQuestion = new JScrollPane(txtPgQuestion);
        scrollQuestion.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        scrollQuestion.getViewport().setBackground(new Color(249, 250, 251));

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
        centerPanel.add(scrollQuestion, BorderLayout.CENTER);
        centerPanel.add(optionsPanel, BorderLayout.SOUTH);

        card.add(centerPanel, BorderLayout.CENTER);

        mainSplit.setLeftComponent(sidebar);
        mainSplit.setRightComponent(card);

        container.add(mainSplit, BorderLayout.CENTER);
    }

    private void buildEssayLayout(JPanel container) {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        splitPane.setDividerSize(12);
        splitPane.setBorder(null);
        splitPane.setDividerLocation(0.5);

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 2, new Color(229, 231, 235)),
                new EmptyBorder(0, 0, 0, 0)));

        JLabel lblLeftTitle = new JLabel("  📄 Naskah Soal");
        lblLeftTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblLeftTitle.setForeground(TEXT_DARK);
        lblLeftTitle.setPreferredSize(new Dimension(0, 60));
        lblLeftTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)),
                new EmptyBorder(0, 25, 0, 0)));

        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setBackground(Color.WHITE);
        leftHeader.add(lblLeftTitle, BorderLayout.WEST);

        leftPanel.add(leftHeader, BorderLayout.NORTH);

        // Check for PDF file and integrate PDF Viewer if exists
        String pdfPath = getPdfPath();
        if (pdfPath != null && !pdfPath.isEmpty() && new java.io.File(pdfPath).exists()) {
            PdfPanel pdfPanel = new PdfPanel(pdfPath);
            leftPanel.add(pdfPanel, BorderLayout.CENTER);
            lblLeftTitle.setText("  📄 Naskah Soal (PDF Viewer)");
        } else {
            JTextArea txtQuestions = new JTextArea();
            txtQuestions.setEditable(false);
            txtQuestions.setLineWrap(true);
            txtQuestions.setWrapStyleWord(true);
            txtQuestions.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            txtQuestions.setBorder(new EmptyBorder(25, 25, 25, 25));
            txtQuestions.setBackground(new Color(235, 235, 235));
            txtQuestions.setForeground(TEXT_DARK);

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < questions.size(); i++) {
                sb.append("Soal ").append(i + 1).append(":\n");
                sb.append(questions.get(i).text == null ? "" : questions.get(i).text).append("\n\n");
            }
            txtQuestions.setText(sb.toString());
            txtQuestions.setCaretPosition(0);

            leftPanel.add(new JScrollPane(txtQuestions), BorderLayout.CENTER);
        }

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);

        JLabel lblRightTitle = new JLabel("  ✍️ Lembar Jawaban");
        lblRightTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblRightTitle.setForeground(PRIMARY_BLUE);
        lblRightTitle.setPreferredSize(new Dimension(0, 60));
        lblRightTitle.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)),
                new EmptyBorder(0, 25, 0, 0)));

        txtEssayAnswer = new JTextArea();
        txtEssayAnswer.setLineWrap(true);
        txtEssayAnswer.setWrapStyleWord(true);
        txtEssayAnswer.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtEssayAnswer.setBorder(new EmptyBorder(25, 25, 25, 25));
        txtEssayAnswer.setBackground(new Color(235, 235, 235));
        txtEssayAnswer.setForeground(TEXT_DARK);

        StringBuilder sbAns = new StringBuilder();
        for (int i = 0; i < questions.size(); i++) {
            sbAns.append("Jawaban Soal ").append(i + 1).append(":\n");
            sbAns.append("\n\n\n\n\n");
            sbAns.append("\n");
        }
        txtEssayAnswer.setText(sbAns.toString());

        rightPanel.add(lblRightTitle, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(txtEssayAnswer), BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);
        splitPane.setRightComponent(rightPanel);

        container.add(splitPane, BorderLayout.CENTER);
    }

    private void loadPgQuestion(int index) {
        if (questions.isEmpty() || index < 0 || index >= questions.size())
            return;

        QuestionObj q = questions.get(index);
        currentQuestionIndex = index;

        lblQuestionNumber.setText("Soal No. " + (index + 1) + " dari " + questions.size());
        txtPgQuestion.setText(q.text == null ? "" : q.text);

        rbA.setText("A. " + safe(q.optA));
        rbB.setText("B. " + safe(q.optB));
        rbC.setText("C. " + safe(q.optC));
        rbD.setText("D. " + safe(q.optD));

        pgGroup.clearSelection();

        if (pgAnswers.containsKey(q.id)) {
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

        // Update button visibility based on position
        if (btnPrev != null) {
            btnPrev.setEnabled(index > 0); // Enable if not first question
        }

        if (index == questions.size() - 1) {
            // Last question: hide next, show finish
            if (btnNext != null)
                btnNext.setVisible(false);
            if (btnFinish != null)
                btnFinish.setVisible(true);
        } else {
            // Not last: show next, hide finish
            if (btnNext != null)
                btnNext.setVisible(true);
            if (btnFinish != null)
                btnFinish.setVisible(false);
        }

        // Update question palette and progress
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
        int target = currentQuestionIndex + step;
        loadPgQuestion(target);
    }

    private void saveCurrentPgAnswer(String answer) {
        if (questions.isEmpty() || currentQuestionIndex < 0 || currentQuestionIndex >= questions.size())
            return;

        QuestionObj q = questions.get(currentQuestionIndex);
        pgAnswers.put(q.id, answer);
        answeredQuestions.put(q.id, true);

        // Update UI immediately
        updateQuestionPalette();
        updateProgressLabel();
    }

    // Update progress label
    private void updateProgressLabel() {
        if (lblProgress != null) {
            int answered = answeredQuestions.size();
            lblProgress.setText("Terjawab: " + answered + "/" + questions.size());

            // Change color based on completion
            if (answered == questions.size()) {
                lblProgress.setForeground(SUCCESS_GREEN);
            } else if (answered > questions.size() / 2) {
                lblProgress.setForeground(WARNING_YELLOW);
            } else {
                lblProgress.setForeground(new Color(107, 114, 128));
            }
        }
    }

    // Create question palette with clickable buttons
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

            // Color based on status
            if (index == currentQuestionIndex) {
                // Current question: Blue
                btnQ.setBackground(PRIMARY_BLUE);
                btnQ.setForeground(Color.WHITE);
            } else if (answeredQuestions.containsKey(q.id)) {
                // Answered: Green
                btnQ.setBackground(SUCCESS_GREEN);
                btnQ.setForeground(Color.WHITE);
            } else {
                // Unanswered: Light gray
                btnQ.setBackground(new Color(243, 244, 246));
                btnQ.setForeground(TEXT_DARK);
            }

            btnQ.addActionListener(e -> {
                if (!locked) {
                    loadPgQuestion(index);
                }
            });

            questionPalettePanel.add(btnQ);
        }

        questionPalettePanel.revalidate();
        questionPalettePanel.repaint();
    }

    // Show review page before final submit
    private void showReviewPage() {
        if (locked)
            return;

        ignoreFocusLost = true;

        // Check unanswered questions
        int unanswered = questions.size() - answeredQuestions.size();

        String message;
        if (unanswered > 0) {
            message = String.format(
                    "<html><div style='text-align:center; padding:20px;'>" +
                            "<h2>⚠️ Perhatian!</h2>" +
                            "<p style='font-size:14px;'>Anda masih memiliki <b style='color:red;'>%d soal</b> yang belum dijawab.</p>"
                            +
                            "<p style='font-size:14px;'>Apakah Anda yakin ingin mengumpulkan sekarang?</p>" +
                            "</div></html>",
                    unanswered);
        } else {
            message = "<html><div style='text-align:center; padding:20px;'>" +
                    "<h2>✅ Semua Soal Terjawab!</h2>" +
                    "<p style='font-size:14px;'>Yakin ingin mengumpulkan jawaban?</p>" +
                    "</div></html>";
        }

        int opt = JOptionPane.showConfirmDialog(
                this,
                message,
                "📤 Konfirmasi Pengumpulan",
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
                if (now - lastViolationTime < 2000) {
                    return;
                }
                lastViolationTime = now;
                handleViolation("FOCUS_LOST_DETECTED");
            }
        });
    }

    private void handleViolation(String code) {
        try {
            int count = sessionRepository.incrementViolation(sessionId);

            lblStatus.setText("⚠️ PERINGATAN: Fokus Hilang! Total: " + count);
            lblStatus.setForeground(ALERT_RED);

            ignoreFocusLost = true;

            if (count >= 4) {
                locked = true;
                disableInputs();
                JOptionPane.showMessageDialog(this,
                        "ANDA TELAH DIKUNCI KARENA TERDETEKSI CURANG (Alt+Tab/Pindah Window) SEBANYAK 4 KALI.\nHubungi pengawas untuk membuka kunci.",
                        "⛔ SISTEM TERKUNCI", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "PERINGATAN KE-" + count
                                + ": Jangan pindah window/tab!\nAktivitas ini tercatat di sistem pengawas.\nJika mencapai 4 kali, ujian akan terkunci.",
                        "⚠️ PELANGGARAN TERDETEKSI", JOptionPane.WARNING_MESSAGE);
            }

            ignoreFocusLost = false;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startStatusTimer() {
        statusTimer = new Timer(3000, e -> checkStatusFromServer());
        statusTimer.start();
    }

    private void checkStatusFromServer() {
        try {
            String status = sessionRepository.getStatus(sessionId);
            if (status == null)
                return;

            if ("LOCKED".equals(status) && !locked) {
                locked = true;
                lblStatus.setText("⛔ Status: TERKUNCI (Oleh Pengawas)");
                lblStatus.setForeground(ALERT_RED);
                disableInputs();

                ignoreFocusLost = true;
                showInfo("Ujian dikunci oleh pengawas.");
                ignoreFocusLost = false;

            } else if ("ONGOING".equals(status) && locked) {
                locked = false;
                lblStatus.setText("✅ Status: Aman");
                lblStatus.setForeground(SUCCESS_GREEN);
                enableInputs();

                ignoreFocusLost = true;
                showInfo("Ujian dibuka kembali. Silakan lanjutkan.");
                ignoreFocusLost = false;

            } else if ("FINISHED".equals(status)) {
                stopAllTimers();
                ignoreFocusLost = true;
                showInfo("Ujian telah diakhiri oleh pengawas.");
                System.exit(0);
            }
        } catch (Exception ignored) {
        }
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
                        lblTimer.setText("⏰ " + formatTime(remainingSeconds));
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

    // Auto-save answers every 30 seconds
    private void startAutoSave() {
        autoSaveTimer = new Timer(30000, e -> autoSaveAnswers()); // 30 seconds
        autoSaveTimer.start();
    }

    private void autoSaveAnswers() {
        if (locked || "ESSAY".equalsIgnoreCase(examMode))
            return;

        try {
            Connection conn = DBConnection.getConnection();

            // Delete existing answers
            String delSql = "DELETE FROM answers WHERE student_exam_id = ?";
            PreparedStatement psDel = conn.prepareStatement(delSql);
            psDel.setInt(1, sessionId);
            psDel.executeUpdate();

            // Insert current answers
            String insSql = "INSERT INTO answers (student_exam_id, question_id, answer_text) VALUES (?, ?, ?)";
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

            // Silent save - no notification to avoid disturbing student

        } catch (Exception ex) {
            // Silent fail - don't disturb student
            ex.printStackTrace();
        }
    }

    private String formatTime(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;
        return String.format("%02d:%02d", m, s);
    }

    private void finishExam() {
        if (locked)
            return;
        ignoreFocusLost = true;
        int opt = JOptionPane.showConfirmDialog(this, "Yakin ingin mengumpulkan jawaban?", "📤 Konfirmasi Pengumpulan",
                JOptionPane.YES_NO_OPTION);
        ignoreFocusLost = false;
        if (opt == JOptionPane.YES_OPTION)
            submitToDb(false);
    }

    private void submitToDb(boolean auto) {
        disableInputs();
        stopAllTimers();

        try {
            Connection conn = DBConnection.getConnection();
            String delSql = "DELETE FROM answers WHERE student_exam_id = ?";
            PreparedStatement psDel = conn.prepareStatement(delSql);
            psDel.setInt(1, sessionId);
            psDel.executeUpdate();

            String insSql = "INSERT INTO answers (student_exam_id, question_id, answer_text) VALUES (?, ?, ?)";
            PreparedStatement ps = conn.prepareStatement(insSql);

            // Calculate Score Locally
            int correctCount = 0;
            if ("PG".equalsIgnoreCase(examMode)) {
                for (QuestionObj q : questions) {
                    String studentAns = pgAnswers.getOrDefault(q.id, "");
                    if (studentAns != null && studentAns.equalsIgnoreCase(q.correctKey)) {
                        correctCount++;
                    }

                    // Save to DB (optional, but good for record)
                    ps.setInt(1, sessionId);
                    ps.setInt(2, q.id);
                    ps.setString(3, studentAns);
                    ps.addBatch();
                }
                ps.executeBatch();
            } else {
                // Essay logic omitted as per request
            }

            int finalScore = 0;
            if (!questions.isEmpty()) {
                finalScore = (int) Math.round(((double) correctCount / questions.size()) * 100);
            }

            // Update student_exams with FINISHED status and score
            String updSql = "UPDATE student_exams SET status = 'FINISHED', finished_at = CURRENT_TIMESTAMP, score = ? WHERE id = ?";
            PreparedStatement psUpd = conn.prepareStatement(updSql);
            psUpd.setInt(1, finalScore);
            psUpd.setInt(2, sessionId);
            psUpd.executeUpdate();

            // Show Score Immediately
            double score = ((double) correctCount / questions.size()) * 100;
            String msg = String.format("<html><div style='text-align:center;'>"
                    + "<h1>Ujian Selesai!</h1>"
                    + "<h2>Nilai Anda: <span style='color:blue'>%.0f</span> / 100</h2>"
                    + "</div></html>", score);

            sessionRepository.updateStatus(sessionId, "FINISHED");

            // Show result dialog then exit
            JOptionPane.showMessageDialog(this, msg, "Hasil Ujian", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);

        } catch (Exception ex) {
            enableInputs();
            showInfo("❌ Error submit: " + ex.getMessage());
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
        ignoreFocusLost = true;
        JOptionPane.showMessageDialog(this, msg);
        ignoreFocusLost = false;
    }

    private static class QuestionObj {
        int id;
        String text;

        String optA;
        String optB;
        String optC;
        String optD;
        String correctKey; // Added for grading
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
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT e.question_file_path FROM exams e WHERE e.id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getString("question_file_path");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}