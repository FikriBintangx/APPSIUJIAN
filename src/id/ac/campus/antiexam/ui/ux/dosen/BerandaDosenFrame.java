package id.ac.campus.antiexam.ui.ux.dosen;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.data.UjianData;
import id.ac.campus.antiexam.data.SesiUjianData;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import id.ac.campus.antiexam.ui.icon.*;

import javax.swing.table.DefaultTableModel;

import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;
import id.ac.campus.antiexam.ui.ux.ManualQuestionEditorDialog;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.List;

public class BerandaDosenFrame extends JFrame {

    private static final String APP_LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    // === INI BASE BUAT DOSEN, JANGAN DISENGGOL NGAB ===
    private final String lecturerUsername;
    private final UjianData examRepository = new UjianData();
    private final SesiUjianData sessionRepository = new SesiUjianData();

    private CardLayout contentCardLayout;
    // panel utama nih boss, tempat gonta-ganti view
    private JPanel mainContentPanel;
    private JButton btnMenuOverview;
    private JButton btnMenuSettings;
    private JButton btnMenuReport;
    private JButton btnMenuAccount;

    private JTable examTable;
    private DefaultTableModel examModel;
    // Tabel sesi buat mantau bocil-bocil ujian
    private JTable sessionTable;
    private DefaultTableModel sessionModel;

    // === INI BUAT LAPORAN UJIAN YE KAN ===
    private JTable reportTable;
    private DefaultTableModel reportModel;

    private JLabel lblStatTotal;
    private JLabel lblStatOngoing;
    private JLabel lblStatPelanggarans;

    private JComboBox<String> cmbSettingCourse;

    private JComboBox<String> cmbExamType;
    private JComboBox<String> cmbJenisSoal;
    private JSpinner spSettingDuration;

    private int selectedExamId = -1;

    private String selectedSubjectCode;
    private String selectedType;
    private int selectedDuration;

    // Neo-Brutalism Palette
    private final Color COL_PRIMARY = new Color(88, 101, 242); // neo blue
    private final Color COL_SECONDARY = new Color(16, 185, 129);
    private final Color COL_BG_MAIN = new Color(248, 250, 252);
    private final Color COL_BG_SIDEBAR = new Color(88, 101, 242); // sidebar biru
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

    public BerandaDosenFrame(String lecturerUsername) {
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
        initComponents();
        loadExams();
        switchView("VIEW_OVERVIEW", btnMenuOverview);

        // Start auto-refresh timer for realtime monitoring
        Timer dashboardTimer = new Timer(5000, e -> loadDashboardStats());
        dashboardTimer.start();
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

        btnMenuOverview = createSidebarButton("Dashboard Utama", new IconDashboard(20, Color.BLACK));
        btnMenuSettings = createSidebarButton("Atur Ujian", new IconEdit(20, Color.BLACK));
        btnMenuReport = createSidebarButton("Laporan Hasil", new IconUjian(20, Color.BLACK));
        btnMenuAccount = createSidebarButton("Profil Saya", new IconInfo(20, Color.BLACK));

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

    // helper buat bikin tombol sidebar
    private JButton createSidebarButton(String text, Icon icon) {
        NeoButton btn = new NeoButton(text, Color.WHITE, Color.BLACK);
        btn.setIcon(icon);
        // pake style beda buat sidebar... mungkin cuma flat teks atau NeoButton?
        // kita pake NeoButton aja biar konsisten.
        // atau sebenernya, tombol flat simple di sidebar biru keliatan lebih bagus kalo
        // teks putih?
        // yaudah pake NeoButton aja buat konsistensi tapi WHITE BG dengan BLACK teks
        // buat yang inactive.
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

        // tambahin padding inside badge
        lblBadge.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.BLACK, 2),
                new EmptyBorder(5, 10, 5, 10)));

        NeoButton btnLogout = new NeoButton("Keluar", COL_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> {
            new PilihPeranFrame().setVisible(true);
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
        lblStatPelanggarans = new JLabel("0");

        statsPanel.add(createStatCard("Total Peserta", lblStatTotal, COL_INFO));
        statsPanel.add(createStatCard("Sedang Ujian", lblStatOngoing, COL_SECONDARY));
        statsPanel.add(createStatCard("Mencurigakan", lblStatPelanggarans, COL_DANGER));

        // cuma Ujian List buat Lecturer, no monitoring
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
        // card.setPreferredSize(new Dimension(450, 0)); // full width sekarang
        card.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        JLabel lblTitle = new JLabel("Daftar Ujian Saya");
        lblTitle.setFont(FONT_H2);
        lblTitle.setForeground(COL_TEXT_DARK);

        NeoButton btnRefresh = new NeoButton("Refresh", Color.WHITE, Color.BLACK);
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

        // ga ada tombol aksi buat dosen di sini, mereka cuma liat
        // NeoButton btnSelect = new NeoButton("Mulai Monitoring", COL_PRIMARY,
        // warna.WHITE);
        // btnSelect.addActionListener(e -> selectUjian());

        card.add(headerPanel, BorderLayout.NORTH);
        card.add(new JScrollPane(examTable), BorderLayout.CENTER);

        // === TOMBOL BUAT NGATUR SOAL ===
        NeoButton btnManageQuestions = new NeoButton("Atur Soal Ujian Ini", COL_PRIMARY, Color.WHITE);
        btnManageQuestions.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih ujian dulu ngab! Jangan ghosting");
                return;
            }
            int examId = (Integer) examModel.getValueAt(row, 0);
            // buka editor soal
            new ManualQuestionEditorDialog(this, examId).setVisible(true);
        });

        NeoButton btnAssignProctor = new NeoButton("Kirim ke Pengawas", COL_SECONDARY, Color.WHITE);
        btnAssignProctor.addActionListener(e -> {
            int row = examTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Pilih ujian dulu!");
                return;
            }
            int examId = (Integer) examModel.getValueAt(row, 0);
            assignToProctor(examId);
        });

        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        footerPanel.add(btnAssignProctor);
        footerPanel.add(Box.createHorizontalStrut(10));
        footerPanel.add(btnManageQuestions);

        card.add(footerPanel, BorderLayout.SOUTH);

        return card;
    }

    // ... logika methods ...

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

        // form buat ngisi data ujian, jangan sampe kosong ntar diomelin mahasiswa
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

        cmbJenisSoal = new JComboBox<>(new String[] { "PG", "ESSAY" });
        styleComboBox(cmbJenisSoal);

        spSettingDuration = new JSpinner(new SpinnerNumberModel(90, 10, 300, 5));
        spSettingDuration.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        // baru: Proctor Selection buat Lecturer
        JComboBox<String> cmbSettingProctor = new JComboBox<>();
        styleComboBox(cmbSettingProctor);
        populateProctorCombo(cmbSettingProctor);

        addFormRow(form, gbc, "Mata Kuliah:", cmbSettingCourse);
        addFormRow(form, gbc, "Tipe Ujian:", cmbExamType);
        addFormRow(form, gbc, "Tipe Soal:", cmbJenisSoal);
        addFormRow(form, gbc, "Durasi (Menit):", spSettingDuration);
        addFormRow(form, gbc, "Pilih Pengawas:", cmbSettingProctor);

        // simpen referensi buat dipake di saveSettings
        form.putClientProperty("cmbSettingProctor", cmbSettingProctor);

        // Spacer
        gbc.gridy++;
        form.add(new JLabel(" "), gbc);

        gbc.gridy++;
        NeoButton btnManual = new NeoButton("\u270F\uFE0F Atur Soal (Editor)", new Color(245, 158, 11), Color.BLACK);
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
        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT username, name FROM pengawas";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = rs.getString("username"); // cuma username buat simplicity in saving
                cmb.addItem(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateSubjectCombo() {
        cmbSettingCourse.removeAllItems();
        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT kode_matkul, name FROM mata_kuliah";
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String item = rs.getString("kode_matkul") + " - " + rs.getString("name");
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
                    id.ac.campus.antiexam.layanan.EksporPdfService.exportTableToPdf(reportModel, "Laporan Hasil Ujian",
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

    private void loadDashboardStats() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() throws Exception {
                return sessionRepository.listAllSessionsForLecturer(lecturerUsername);
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    int total = list.size();
                    int ongoing = 0;
                    int violations = 0;

                    for (Object[] row : list) {
                        String status = (String) row[3];
                        int vCount = (Integer) row[5];

                        if ("ONGOING".equals(status) || "LOCKED".equals(status)) {
                            ongoing++;
                        }
                        if (vCount > 0) {
                            violations++;
                        }
                    }

                    if (lblStatTotal != null)
                        lblStatTotal.setText(String.valueOf(total));
                    if (lblStatOngoing != null)
                        lblStatOngoing.setText(String.valueOf(ongoing));
                    if (lblStatPelanggarans != null)
                        lblStatPelanggarans.setText(String.valueOf(violations));

                } catch (Exception e) {
                    // silent fail
                }
            }
        }.execute();
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
                        // Monitoring freeze logika removed

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
                    lblStatPelanggarans.setText(String.valueOf(violations));
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
                cmbJenisSoal.setSelectedItem(selectedExamMode);
            spSettingDuration.setValue(selectedDuration);
        }
    }

    private void saveSettings(JPanel form) {
        String courseItem = (String) cmbSettingCourse.getSelectedItem();
        String subjectCode = courseItem.split(" - ")[0];

        String type = (String) cmbExamType.getSelectedItem();
        String qType = (String) cmbJenisSoal.getSelectedItem();
        int duration = (Integer) spSettingDuration.getValue();
        JComboBox<String> cmbProc = (JComboBox<String>) form.getClientProperty("cmbSettingProctor");

        if (selectedExamId != -1) {
            // update
            try {
                // kita cuma update field basic buat sekarang atau panggil update spesifik
                examRepository.updateExamWithFile(selectedExamId, type, qType, duration, subjectCode, "");
                // sebenernya kita mau updateUjian full params tapi ga nampil semua
                // field di sini.
                // anggap aja update minimal ini cukup buat view settings.
                // idealnya kita harusnya pake updateUjian tapi ada beberapa field yang kurang
                // di
                // ini
                // view context.
                // However, UjianData.updateExamWithFile updates type, examMode, duration,
                // subjectCode.
                // kurang update proctor.
                // kita andalin examRepository.updateUjian yang ga bisa dipanggil gampang tanpa
                // loading all data first.
                // jadi buat sekarang, kita pake apa yang udah ada atau yang cocok.
                // user asked buat "bahasa indo", ga logika refactor.
                JOptionPane.showMessageDialog(this, "Pengaturan ujian disimpan!");
                loadExams();
                switchView("VIEW_OVERVIEW", btnMenuOverview);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih ujian untuk diedit dari daftar!");
        }
    }

    private void switchView(String viewName, JButton activeBtn) {
        contentCardLayout.show(mainContentPanel, viewName);
        resetSidebarButtons();
        ((NeoButton) activeBtn).setColors(Color.WHITE, COL_PRIMARY);
    }

    private void resetSidebarButtons() {
        ((NeoButton) btnMenuOverview).setColors(COL_BG_SIDEBAR, Color.WHITE);
        ((NeoButton) btnMenuSettings).setColors(COL_BG_SIDEBAR, Color.WHITE);
        ((NeoButton) btnMenuReport).setColors(COL_BG_SIDEBAR, Color.WHITE);
        ((NeoButton) btnMenuAccount).setColors(COL_BG_SIDEBAR, Color.WHITE);
    }

    private void openAccountDialog() {
        JOptionPane.showMessageDialog(this, "Login sebagai: " + lecturerUsername + "\nRole: Dosen");
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(true);
        table.setGridColor(Color.BLACK);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(Color.WHITE);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return table;
    }

    private void exportReportToExcel() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan Laporan Excel");
        fileChooser.setSelectedFile(new java.io.File("Laporan_Hasil_Ujian_" + System.currentTimeMillis() + ".xls"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (java.io.PrintWriter writer = new java.io.PrintWriter(fileChooser.getSelectedFile())) {
                StringBuilder html = new StringBuilder();
                html.append("<html xmlns:x=\"urn:schemas-microsoft-com:office:excel\">");
                html.append("<head><meta http-equiv=\"content-type\" content=\"text/plain; charset=UTF-8\"/>");
                html.append("<style>");
                html.append("table { border-collapse: collapse; width: 100%; font-family: sans-serif; }");
                html.append(
                        "th { background-color: #4F46E5; color: white; border: 1px solid #000; padding: 10px; text-align: left; }");
                html.append("td { border: 1px solid #000; padding: 8px; vertical-align: top; }");
                html.append(".title { font-size: 24px; font-weight: bold; margin-bottom: 20px; }");
                html.append("</style>");
                html.append("</head><body>");
                html.append("<div class='title'>Laporan Hasil Ujian - Dosen</div>");
                html.append("<table>");

                // headers
                html.append("<thead><tr>");
                for (int i = 0; i < reportModel.getColumnCount(); i++) {
                    html.append("<th>").append(reportModel.getColumnName(i)).append("</th>");
                }
                html.append("</tr></thead>");

                // Rows
                html.append("<tbody>");
                for (int i = 0; i < reportModel.getRowCount(); i++) {
                    html.append("<tr>");
                    for (int j = 0; j < reportModel.getColumnCount(); j++) {
                        Object val = reportModel.getValueAt(i, j);
                        html.append("<td>").append(val == null ? "-" : val.toString()).append("</td>");
                    }
                    html.append("</tr>");
                }
                html.append("</tbody></table>");
                html.append("</body></html>");

                writer.write(html.toString());
                JOptionPane.showMessageDialog(this, "\u2705 Berhasil ekspor Excel!");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "\u274C Gagal ekspor: " + e.getMessage());
            }
        }
    }

    // custom tombol class same as Proctor
    private class NeoButton extends JButton {
        private Color bgColor;
        private Color fgColor;

        public NeoButton(String t, Color bg, Color fg) {
            super(t);
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
            int off = getModel().isPressed() ? 4 : 0;
            g2.setColor(bgColor);
            g2.fillRect(off, off, getWidth() - 4, getHeight() - 4);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(off, off, getWidth() - 6, getHeight() - 6);

            // ikon & Text Drawing
            Icon icon = getIcon();
            String text = getText();
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            int textW = fm.stringWidth(text);
            int iconW = (icon != null) ? icon.getIconWidth() : 0;
            int gap = (icon != null && !text.isEmpty()) ? 10 : 0;
            int totalW = textW + iconW + gap;
            int startX = (getWidth() - totalW) / 2 + off / 2;
            int centerY = (getHeight() - 4) / 2 + off / 2;
            if (icon != null) {
                icon.paintIcon(this, g2, startX, centerY - icon.getIconHeight() / 2);
            }
            g2.setColor(fgColor);
            g2.drawString(text, startX + iconW + gap,
                    centerY + (fm.getAscent() - fm.getDescent()) / 2 + fm.getDescent());
            g2.dispose();
        }
    }

    private void assignToProctor(int examId) {
        try {
            java.util.List<String> proctors = new java.util.ArrayList<>();
            java.sql.Connection conn = id.ac.campus.antiexam.konfigurasi.KoneksiDatabase.getConnection();
            String sql = "SELECT username FROM pengawas";
            java.sql.PreparedStatement ps = conn.prepareStatement(sql);
            java.sql.ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                proctors.add(rs.getString("username"));
            }

            if (proctors.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Tidak ada akun Pengawas yang tersedia!");
                return;
            }

            String[] proctorArray = proctors.toArray(new String[0]);
            String selected = (String) JOptionPane.showInputDialog(
                    this,
                    "Pilih Pengawas untuk ujian ini:",
                    "Kirim Data ke Pengawas",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    proctorArray,
                    proctorArray[0]);

            if (selected != null) {
                String upd = "UPDATE ujian SET username_pengawas = ? WHERE id = ?";
                java.sql.PreparedStatement psUpd = conn.prepareStatement(upd);
                psUpd.setString(1, selected);
                psUpd.setInt(2, examId);
                psUpd.executeUpdate();
                JOptionPane.showMessageDialog(this, "Sukses! Data ujian dikirim ke pengawas: " + selected);
                loadExams();
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal assign pengawas: " + e.getMessage());
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
            g2.setColor(Color.BLACK);
            g2.fillRect(6, 6, getWidth() - 6, getHeight() - 6);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth() - 6, getHeight() - 6);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRect(0, 0, getWidth() - 7, getHeight() - 7);
            g2.dispose();
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            new BerandaDosenFrame("11009010").setVisible(true);
        });
    }
}
