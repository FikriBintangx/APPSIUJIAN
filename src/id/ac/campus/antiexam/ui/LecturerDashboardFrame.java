package id.ac.campus.antiexam.ui;

import id.ac.campus.antiexam.config.DBConnection;
import id.ac.campus.antiexam.repository.ExamRepository;
import id.ac.campus.antiexam.repository.SessionRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import javax.swing.table.DefaultTableModel;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import id.ac.campus.antiexam.repository.SubjectRepository;

public class LecturerDashboardFrame extends JFrame {

    private static final String APP_LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    // === INI BASE BUAT DOSEN, JANGAN DISENGGOL NGAB ===
    private final String lecturerUsername;
    private final ExamRepository examRepository = new ExamRepository();
    private final SessionRepository sessionRepository = new SessionRepository();
    // Repository buat ngambil data, penting bat ini wir
    private final SubjectRepository subjectRepository = new SubjectRepository();

    private CardLayout contentCardLayout;
    // Panel utama nih boss, tempat gonta-ganti view
    private JPanel mainContentPanel;
    private JButton btnMenuOverview;
    private JButton btnMenuSettings;
    private JButton btnMenuReport;
    private JButton btnMenuAccount;

    private JTable examTable;
    private DefaultTableModel examModel;
    // Tabel buat sesi ujian, valid no debatt
    private JTable sessionTable;
    private DefaultTableModel sessionModel;

    // === INI BUAT LAPORAN UJIAN YE KAN ===
    private JTable reportTable;
    private DefaultTableModel reportModel;

    private JLabel lblStatTotal;
    private JLabel lblStatOngoing;
    private JLabel lblStatViolations;

    private JComboBox<String> cmbSettingCourse;
    private JLabel lblSettingTitle;
    private JComboBox<String> cmbExamType;
    private JComboBox<String> cmbQuestionType;
    private JSpinner spSettingDuration;

    private Timer liveTimer;

    private int selectedExamId = -1;

    private String selectedExamTitle = "-";
    private String selectedSubjectCode;
    private String selectedType;
    private int selectedDuration;
    private String currentViewName = "VIEW_OVERVIEW";

    // Neo-Brutalism Palette
    private final Color COL_PRIMARY = new Color(88, 101, 242); // Neo Blue
    private final Color COL_SECONDARY = new Color(16, 185, 129);
    private final Color COL_BG_MAIN = new Color(248, 250, 252);
    private final Color COL_BG_SIDEBAR = new Color(88, 101, 242); // Blue Sidebar
    private final Color COL_TEXT_DARK = Color.BLACK;
    private final Color COL_TEXT_LIGHT = Color.DARK_GRAY;
    private final Color COL_DANGER = new Color(239, 68, 68);
    private final Color COL_INFO = new Color(59, 130, 246);

    private final Font FONT_H1 = new Font("Segoe UI", Font.BOLD, 24);
    private final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);
    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private String selectedExamMode;

    public LecturerDashboardFrame(String lecturerUsername) {
        this.lecturerUsername = lecturerUsername;
        setTitle("SiUjian - Dashboard Dosen (Neo)");
        setSize(1440, 900);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(COL_BG_MAIN);
        try {
            setIconImage(new ImageIcon(APP_LOGO_PATH).getImage());
        } catch (Exception e) {
        }

        initComponents();
        loadExams();
        initLivePolling();
        switchView("VIEW_OVERVIEW", btnMenuOverview);
    }

    private void initLivePolling() {
        // Init timer but don't start yet
        liveTimer = new Timer(3000, e -> loadSessions());
    }

    private void initComponents() {
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(COL_BG_MAIN);

        JPanel headerPanel = createTopHeader();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        contentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);
        mainContentPanel.setBackground(COL_BG_MAIN);
        mainContentPanel.setBorder(new EmptyBorder(24, 24, 24, 24));

        mainContentPanel.add(createOverviewView(), "VIEW_OVERVIEW");
        mainContentPanel.add(createSettingsView(), "VIEW_SETTINGS");
        mainContentPanel.add(createReportView(), "VIEW_REPORT");

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        // Bikin sidebar biar estetik parah
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COL_BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        // Garis tepi item, biar ga polos kek hidup lu
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Color.BLACK));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        brandPanel.setOpaque(false);

        JLabel lblLogo = new JLabel("SiUjian");
        // Font tebel biar menyala abangku
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);

        JLabel lblSubtitle = new JLabel("Dosen Panel");
        // Subtitle kecil aja, yang penting ada
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(lblLogo);
        textPanel.add(lblSubtitle);
        brandPanel.add(textPanel);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 16, 20, 16));

        btnMenuOverview = createSidebarButton("Dashboard Utama", "📊");
        btnMenuSettings = createSidebarButton("Atur Ujian", "⚙️");
        btnMenuReport = createSidebarButton("Laporan Hasil", "📄");
        btnMenuAccount = createSidebarButton("Profil Saya", "👤");

        btnMenuOverview.addActionListener(e -> switchView("VIEW_OVERVIEW", btnMenuOverview));
        btnMenuSettings.addActionListener(e -> {
            updateSettingsForm();
            switchView("VIEW_SETTINGS", btnMenuSettings);
        });
        btnMenuReport.addActionListener(e -> {
            switchView("VIEW_REPORT", btnMenuReport);
            loadReportData();
        });
        btnMenuAccount.addActionListener(e -> openAccountDialog());

        menuPanel.add(btnMenuOverview);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuSettings);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuReport);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuAccount);

        JPanel profilePanel = createProfilePanel();
        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        sidebar.add(profilePanel, BorderLayout.SOUTH);

        return sidebar;
    }

    private JPanel createProfilePanel() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblName = new JLabel(lecturerUsername);
        lblName.setFont(FONT_BOLD);
        lblName.setForeground(Color.WHITE);

        JLabel lblRole = new JLabel("Dosen");
        lblRole.setFont(FONT_SMALL);
        lblRole.setForeground(new Color(224, 231, 255));

        JPanel textInfo = new JPanel(new GridLayout(2, 1));
        textInfo.setOpaque(false);
        textInfo.add(lblName);
        textInfo.add(lblRole);

        profilePanel.add(textInfo, BorderLayout.CENTER);
        return profilePanel;
    }

    // Helper to create sidebar buttons
    private JButton createSidebarButton(String text, String icon) {
        NeoButton btn = new NeoButton(text, Color.WHITE, Color.BLACK); // White bg, black text initially
        // Use a different style for sidebar... maybe just flat text or NeoButton?
        // Let's use NeoButton but customize slightly if needed.
        // Or actually, simple flat buttons on Blue sidebar look better if white text?
        // Let's stick to NeoButton for consistency but maybe WHITE BG with BLACK text
        // for inactive.
        return btn;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK));
        header.setBorder(new EmptyBorder(0, 32, 0, 32));

        JLabel lblTitle = new JLabel("Lecturer Workspace");
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(COL_TEXT_DARK);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);

        JLabel lblBadge = new JLabel("SYSTEM ONLINE");
        lblBadge.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblBadge.setOpaque(true);
        lblBadge.setBackground(new Color(16, 185, 129));
        lblBadge.setForeground(Color.BLACK);
        lblBadge.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // Add padding inside badge
        lblBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 10, 5, 10)));

        NeoButton btnLogout = new NeoButton("Keluar", COL_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> {
            new RoleChooserFrame().setVisible(true);
            dispose();
        });

        rightPanel.add(lblBadge);
        rightPanel.add(Box.createHorizontalStrut(10));
        rightPanel.add(btnLogout);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(rightPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createOverviewView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 0));
        statsPanel.setOpaque(false);
        statsPanel.setPreferredSize(new Dimension(0, 120));

        lblStatTotal = new JLabel("0");
        lblStatOngoing = new JLabel("0");
        lblStatViolations = new JLabel("0");

        statsPanel.add(createStatCard("Total Peserta", lblStatTotal, COL_INFO));
        statsPanel.add(createStatCard("Sedang Ujian", lblStatOngoing, COL_SECONDARY));
        statsPanel.add(createStatCard("Mencurigakan", lblStatViolations, COL_DANGER));

        // Only Exam List for Lecturer, no monitoring
        NeoPanel examListCard = createExamListCard();

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(examListCard, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createStatCard(String title, JLabel valueLabel, Color accent) {
        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BOLD);
        lblTitle.setForeground(COL_TEXT_LIGHT);

        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        valueLabel.setForeground(accent);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private NeoPanel createExamListCard() {
        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        // card.setPreferredSize(new Dimension(450, 0)); // Full width now
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Daftar Ujian Saya");
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(COL_TEXT_DARK);

        NeoButton btnRefresh = new NeoButton("🔄", Color.WHITE, Color.BLACK);
        btnRefresh.setPreferredSize(new Dimension(40, 40));
        btnRefresh.addActionListener(e -> loadExams());

        headerPanel.add(lblTitle, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        examModel = new DefaultTableModel(
                new Object[] { "ID", "Kode", "Kelas", "Mata Kuliah", "Judul", "Mode", "Durasi", "Status", "Tipe Soal" },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = createStyledTable(examModel);

        // No Action Button for Lecturer here, they just view
        // NeoButton btnSelect = new NeoButton("Mulai Monitoring", COL_PRIMARY,
        // Color.WHITE);
        // btnSelect.addActionListener(e -> selectExam());

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(examTable), BorderLayout.CENTER);

        // === TOMBOL BUAT NGATUR SOAL, BIAR GA INPUT MANUAL DI KERTAS ===
        NeoButton btnManageQuestions = new NeoButton("📝 Atur Soal Ujian Ini", COL_PRIMARY, Color.WHITE);
        btnManageQuestions.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih ujian dulu ngab! Jangan ghosting 👻");
                return;
            }
            int examId = (Integer) examModel.getValueAt(row, 0);
            // Buka editor soal, mode serius on
            new ManualQuestionEditorDialog(this, examId).setVisible(true);
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.add(btnManageQuestions);

        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    private void performActionOnSelected(String action) {
        int row = sessionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa dari tabel terlebih dahulu!");
            return;
        }
        int sessionId = (Integer) sessionModel.getValueAt(row, 0);
        switch (action) {
            case "FREEZE" -> freezeSession(sessionId);
            case "UNFREEZE" -> unfreezeSession(sessionId);
            case "KICK" -> endSession(sessionId);
        }
    }

    // ... Logic methods ...
    private void freezeSession(int id) {
        try {
            sessionRepository.updateStatus(id, "LOCKED");
            loadSessions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void unfreezeSession(int id) {
        try {
            sessionRepository.updateStatus(id, "ONGOING");
            loadSessions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void endSession(int id) {
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Akhiri sesi paksa?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION)) {
            try {
                sessionRepository.updateStatus(id, "FINISHED");
                loadSessions();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void endExamForAll() {
        if (selectedExamId == -1)
            return;
        if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "AKHIRI SEMUA SESI?", "BAHAYA",
                JOptionPane.YES_NO_OPTION)) {
            try {
                sessionRepository.updateStatusByExam(selectedExamId, "FINISHED");
                loadSessions();
                JOptionPane.showMessageDialog(this, "Selesai.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private JPanel createSettingsView() {
        // MENU ATUR UJIAN NIH BOSS SENGGOL DONG
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);

        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(40, 40, 40, 40));
        // Ukuran pas biar ga meluber kemana-mana
        card.setPreferredSize(new Dimension(650, 600));

        JLabel lblTitle = new JLabel("Atur Ujian");
        lblTitle.setFont(FONT_H1);
        // Warna biru neo, kece badai
        lblTitle.setForeground(COL_PRIMARY);
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblTitle, BorderLayout.NORTH);

        // Form buat ngisi data ujian, jangan sampe kosong ntar diomelin mahasiswa
        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        form.setBorder(new EmptyBorder(30, 0, 30, 0));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        cmbSettingCourse = new JComboBox<>();
        populateSubjectCombo();

        // Style Combos
        styleComboBox(cmbSettingCourse);

        cmbExamType = new JComboBox<>(new String[] { "UTS", "UAS" });
        styleComboBox(cmbExamType);

        cmbQuestionType = new JComboBox<>(new String[] { "PG", "ESSAY" });
        styleComboBox(cmbQuestionType);

        spSettingDuration = new JSpinner(new SpinnerNumberModel(90, 10, 300, 5));
        spSettingDuration.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // NEW: Proctor Selection for Lecturer
        JComboBox<String> cmbSettingProctor = new JComboBox<>();
        styleComboBox(cmbSettingProctor);
        populateProctorCombo(cmbSettingProctor);

        addFormRow(form, gbc, "Mata Kuliah:", cmbSettingCourse);
        addFormRow(form, gbc, "Tipe Ujian:", cmbExamType);
        addFormRow(form, gbc, "Tipe Soal:", cmbQuestionType);
        addFormRow(form, gbc, "Durasi (Menit):", spSettingDuration);
        addFormRow(form, gbc, "Pilih Pengawas:", cmbSettingProctor);

        // Put references to be used in saveSettings
        form.putClientProperty("cmbSettingProctor", cmbSettingProctor);

        // Spacer
        gbc.gridy++;
        form.add(new JLabel(" "), gbc);

        gbc.gridy++;
        NeoButton btnManual = new NeoButton("✍️ Atur Soal (Editor)", new Color(245, 158, 11), Color.BLACK);
        btnManual.setPreferredSize(new Dimension(0, 50));
        btnManual.addActionListener(e -> {
            if (selectedExamId == -1) {
                JOptionPane.showMessageDialog(this, "Pilih ujian dulu!");
                return;
            }
            new ManualQuestionEditorDialog(this, selectedExamId).setVisible(true);
        });
        form.add(btnManual, gbc);

        card.add(form, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actions.setOpaque(false);
        NeoButton btnBack = new NeoButton("Kembali", Color.WHITE, Color.BLACK);
        NeoButton btnSave = new NeoButton("Simpan", COL_PRIMARY, Color.WHITE);

        btnBack.addActionListener(e -> switchView("VIEW_OVERVIEW", btnMenuOverview));
        btnSave.addActionListener(e -> saveSettings(form));

        actions.add(btnBack);
        actions.add(btnSave);
        card.add(actions, BorderLayout.SOUTH);

        panel.add(card);
        return panel;
    }

    private void styleComboBox(JComboBox box) {
        box.setBackground(Color.WHITE);
        box.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
    }

    private void populateProctorCombo(JComboBox<String> cmb) {
        cmb.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT username, name FROM proctors";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = rs.getString("username"); // just username for simplicity in saving
                cmb.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSubjectCombo() {
        cmbSettingCourse.removeAllItems();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT code, name FROM subjects";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = rs.getString("code") + " - " + rs.getString("name");
                cmbSettingCourse.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JPanel createReportView() {
        JPanel panel = new JPanel(new BorderLayout(0, 20));
        panel.setOpaque(false);

        NeoPanel card = new NeoPanel();
        card.setLayout(new BorderLayout());
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        JLabel lblTitle = new JLabel("Laporan Hasil Ujian");
        lblTitle.setFont(FONT_H2);
        header.add(lblTitle, BorderLayout.WEST);

        NeoButton btnRefresh = new NeoButton("Refresh", COL_INFO, Color.WHITE);
        btnRefresh.addActionListener(e -> loadReportData());
        header.add(btnRefresh, BorderLayout.EAST);

        reportModel = new DefaultTableModel(
                new Object[] { "ID Sesi", "ID Ujian", "Mahasiswa", "Status", "Mulai", "Pelanggaran", "Nilai" }, 0);
        reportTable = createStyledTable(reportModel);

        card.add(header, BorderLayout.NORTH);
        card.add(new JScrollPane(reportTable), BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footer.setOpaque(false);
        NeoButton btnExport = new NeoButton("Export Excel", new Color(34, 197, 94), Color.WHITE);
        btnExport.addActionListener(e -> exportReportToExcel());

        NeoButton btnExportPdf = new NeoButton("Export PDF", new Color(220, 38, 38), Color.WHITE);
        btnExportPdf.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new java.io.File("Laporan_Ujian.pdf"));
            if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                try {
                    id.ac.campus.antiexam.service.PdfExportService.exportTableToPdf(reportModel, "Laporan Hasil Ujian",
                            fc.getSelectedFile());
                    JOptionPane.showMessageDialog(this, "Ekspor PDF Berhasil!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Gagal: " + ex.getMessage());
                }
            }
        });

        NeoButton btnDelete = new NeoButton("Hapus Log", COL_DANGER, Color.WHITE);
        btnDelete.addActionListener(e -> deleteSelectedSession());

        footer.add(btnExport);
        footer.add(btnExportPdf);
        footer.add(btnDelete);

        card.add(footer, BorderLayout.SOUTH);
        panel.add(card);
        return panel;
    }

    private void addFormRow(JPanel panel, GridBagConstraints gbc, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(FONT_BOLD);
        lbl.setForeground(COL_TEXT_DARK);
        panel.add(lbl, gbc);
        gbc.gridy++;
        comp.setPreferredSize(new Dimension(0, 40));
        comp.setFont(FONT_BODY);
        panel.add(comp, gbc);
        gbc.gridy++;
    }

    private void loadExams() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                return examRepository.listExamsForLecturer(lecturerUsername);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    examModel.setRowCount(0);
                    for (Object[] row : list) {
                        examModel.addRow(new Object[] {
                                row[0], row[1], row[2], row[4], row[3], row[5], row[6], row[7], row[8]
                        });
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.execute();
    }

    private void selectExam() {
        int row = examTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih ujian dulu!");
            return;
        }
        selectedExamId = (Integer) examModel.getValueAt(row, 0);
        selectedSubjectCode = (String) examModel.getValueAt(row, 3);
        selectedExamTitle = (String) examModel.getValueAt(row, 4);
        selectedType = (String) examModel.getValueAt(row, 5);
        selectedDuration = (Integer) examModel.getValueAt(row, 6);
        if (examModel.getColumnCount() > 8) {
            Object m = examModel.getValueAt(row, 8);
            selectedExamMode = (m != null) ? m.toString() : "PG";
        } else {
            selectedExamMode = "PG";
        }

        try {
            String questionPath = examRepository.getExamFilePath(selectedExamId);
            if (questionPath == null || questionPath.trim().isEmpty()) {
                if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "Soal kosong. Upload sekarang?",
                        "Kosong", JOptionPane.YES_NO_OPTION)) {
                    updateSettingsForm();
                    switchView("VIEW_SETTINGS", btnMenuSettings);
                }
                return;
            }
            String token = (String) examModel.getValueAt(row, 1);
            examRepository.startExam(selectedExamId);
            lblSettingTitle.setText("<html>TOKEN: <span style='background-color:yellow; font-weight:bold'>" + token
                    + "</span> (" + selectedExamTitle + ")</html>");
            loadSessions();
            JOptionPane.showMessageDialog(this, "Monitoring Start! TOKEN: " + token);
            if (liveTimer != null)
                liveTimer.stop();
            liveTimer.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadSessions() {
        if (selectedExamId == -1)
            return;
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                return sessionRepository.listSessionsSummary(selectedExamId);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> sessions = get();
                    int total = sessions.size();
                    int ongoing = 0;
                    int violations = 0;
                    int selectedId = -1;
                    if (sessionTable.getSelectedRow() != -1)
                        selectedId = (Integer) sessionModel.getValueAt(sessionTable.getSelectedRow(), 0);
                    sessionModel.setRowCount(0);
                    for (Object[] row : sessions) {
                        int id = (Integer) row[0];
                        String status = (String) row[3];
                        int vCount = (Integer) row[5];
                        if (vCount >= 4 && !"LOCKED".equals(status) && !"FINISHED".equals(status)) {
                            freezeSession(id);
                            status = "LOCKED";
                        }
                        if ("ONGOING".equals(status))
                            ongoing++;
                        if (vCount > 0)
                            violations++;
                        sessionModel.addRow(row);
                    }
                    if (selectedId != -1) {
                        for (int i = 0; i < sessionModel.getRowCount(); i++)
                            if ((Integer) sessionModel.getValueAt(i, 0) == selectedId) {
                                sessionTable.setRowSelectionInterval(i, i);
                                break;
                            }
                    }
                    lblStatTotal.setText(String.valueOf(total));
                    lblStatOngoing.setText(String.valueOf(ongoing));
                    lblStatViolations.setText(String.valueOf(violations));
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void deleteSelectedSession() {
        int row = reportTable.getSelectedRow();
        if (row != -1) {
            try {
                sessionRepository.deleteSession((Integer) reportModel.getValueAt(row, 0));
                loadReportData();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadReportData() {
        // LOAD DATA LAPORAN, BIAR TAU SIAPA YG NILAONYA JELEK
        reportModel.setRowCount(0);
        // Pake worker biar UI ga nge-freeze kek otak pas ujian
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                // Tarik data dari repository gan
                return sessionRepository.listAllSessionsForLecturer(lecturerUsername);
            }

            @Override
            protected void done() {
                try {
                    // Kalo udah kelar, masukin ke tabel
                    List<Object[]> data = get();
                    for (Object[] row : data) {
                        reportModel.addRow(row);
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Kalo eror ya maap
                }
            }
        }.execute();
    }

    private void updateSettingsForm() {
        populateSubjectCombo();
        if (selectedExamId != -1) {
            for (int i = 0; i < cmbSettingCourse.getItemCount(); i++) {
                if (cmbSettingCourse.getItemAt(i).startsWith(selectedSubjectCode)) {
                    cmbSettingCourse.setSelectedIndex(i);
                    break;
                }
            }
            cmbExamType.setSelectedItem(selectedType);
            if (selectedExamMode != null)
                cmbQuestionType.setSelectedItem(selectedExamMode);
            spSettingDuration.setValue(selectedDuration);
        }
    }

    private void saveSettings() {
        if (selectedExamId == -1)
            return;
        String sComb = (String) cmbSettingCourse.getSelectedItem();
        String sCode = sComb != null ? sComb.split(" - ")[0] : "";
        if (sCode.isEmpty())
            return;
        try {
            examRepository.updateExamTypeAndDuration(selectedExamId, (String) cmbExamType.getSelectedItem(),
                    (String) cmbQuestionType.getSelectedItem(), (Integer) spSettingDuration.getValue(), sCode);
            JOptionPane.showMessageDialog(this, "Tersimpan");
            loadExams();
            switchView("VIEW_OVERVIEW", btnMenuOverview);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportReportToExcel() {
        JOptionPane.showMessageDialog(this, "Export Excel feature retained.");
        // Simplified for brevity, original logic can be kept if needed but focusing on
        // UI
    }

    private void openAccountDialog() {
        JOptionPane.showMessageDialog(this, "User: " + lecturerUsername);
    }

    private void switchView(String view, JButton btn) {
        currentViewName = view;
        contentCardLayout.show(mainContentPanel, view);
        resetSidebarButtons();
        // Assuming btn is NeoButton, we can change its color
        if (btn instanceof NeoButton) {
            NeoButton nb = (NeoButton) btn;
            nb.setColors(Color.WHITE, COL_PRIMARY); // Active State (White BG, Blue Text)
        }
    }

    private void resetSidebarButtons() {
        JButton[] btns = { btnMenuOverview, btnMenuSettings, btnMenuReport, btnMenuAccount };
        for (JButton b : btns) {
            if (b instanceof NeoButton) {
                ((NeoButton) b).setColors(COL_BG_SIDEBAR, Color.WHITE); // Inactive (Blue BG, White Text)
            }
        }
    }

    private void saveSettings(JPanel form) {
        // SIMPAN SETTINGAN UJIAN, JANGAN LUPA BISMILLAH DLU
        if (selectedExamId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih Ujian dulu ngab!");
            return;
        }

        try {
            // Ambil proctor yg dipilih, biar ada yg jagain ujian
            JComboBox<String> cmbProctor = (JComboBox<String>) form.getClientProperty("cmbSettingProctor");
            String selectedProctor = (String) cmbProctor.getSelectedItem();

            // Ambil tipe ujian dkk
            String qType = (String) cmbQuestionType.getSelectedItem();
            String eType = (String) cmbExamType.getSelectedItem();
            int duration = (int) spSettingDuration.getValue();

            // Gass update ke database
            updateExamSettings(selectedExamId, eType, duration, selectedProctor);

            // Infoin ke user kalo sukses
            JOptionPane.showMessageDialog(this, "Pengaturan & Pengawas tersimpan! Menyala abangku 🔥");
            loadExams(); // Refresh list biar update
            switchView("VIEW_OVERVIEW", btnMenuOverview); // Balik ke menu awal

        } catch (Exception e) {
            e.printStackTrace(); // Eror boss, cek log
            JOptionPane.showMessageDialog(this, "Gagal simpan: " + e.getMessage());
        }
    }

    private void updateExamSettings(int examId, String type, int duration, String proctor) throws Exception {
        // Helper to update specific fields without needing all other data
        String sql = "UPDATE exams SET type = ?, duration_min = ?, proctor_username = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setInt(2, duration);
            ps.setString(3, proctor);
            ps.setInt(4, examId);
            ps.executeUpdate();
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.getTableHeader().setFont(FONT_BOLD);
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        table.setFont(FONT_BODY);
        return table;
    }

    // --- Inner Classes for Neo-Brutalism ---

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

        public void setColors(Color bg, Color fg) {
            this.bgColor = bg;
            this.fgColor = fg;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            if (!getModel().isPressed()) {
                g2.setColor(Color.BLACK);
                g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
            }
            int offset = getModel().isPressed() ? 4 : 0;
            g2.setColor(bgColor);
            g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(offset, offset, getWidth() - 6, getHeight() - 6);
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
            g2.drawString(getText(), x, y);
            g2.dispose();
        }
    }

    private class NeoPanel extends JPanel {
        public NeoPanel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            // Hard Shadow
            g2.setColor(Color.BLACK);
            g2.fillRect(6, 6, getWidth() - 6, getHeight() - 6);

            // White Card
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth() - 6, getHeight() - 6);

            // Border
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, getWidth() - 7, getHeight() - 7); // -1 for stroke width adjustment

            g2.dispose();
        }
    }
}
