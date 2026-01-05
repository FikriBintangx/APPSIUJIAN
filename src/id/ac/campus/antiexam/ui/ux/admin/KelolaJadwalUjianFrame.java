package id.ac.campus.antiexam.ui.ux.admin;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.data.UjianData;
import id.ac.campus.antiexam.data.DosenData;
import id.ac.campus.antiexam.data.MataKuliahData;
import id.ac.campus.antiexam.data.MahasiswaData;
import id.ac.campus.antiexam.data.RuangUjianData;
import id.ac.campus.antiexam.layanan.ImportSoalService;
import id.ac.campus.antiexam.ui.icon.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.List;
import java.io.File;

import java.util.Set;
import java.awt.Image;
import javax.swing.RowFilter;

public class KelolaJadwalUjianFrame extends JFrame {

    private final UjianData examRepository = new UjianData();
    private final DosenData lecturerRepository = new DosenData();
    private final MataKuliahData subjectRepository = new MataKuliahData();
    private final MahasiswaData studentRepository = new MahasiswaData();
    private final RuangUjianData roomRepository = new RuangUjianData();

    private JLabel lblStatTotal;
    private JLabel lblStatOngoing;
    private JLabel lblStatScheduled;
    private JLabel lblStatAlert;

    // Stats Cards Labels
    private JLabel lblCardTotal;
    private JLabel lblCardOngoing;
    private JLabel lblCardScheduled;
    private JLabel lblCardAlert;

    private CardLayout contentCardLayout;
    private JPanel mainContentPanel;

    private JButton btnMenuDashboard;
    private JButton btnMenuPenjadwalan;
    private JButton btnMenuDirectory;
    private JButton btnMenuAbout;

    private JTable dashboardTable;
    private DefaultTableModel dashboardModel;

    private JTable tableScheduled;
    private DefaultTableModel modelScheduled;

    private JTable examTableList;
    private DefaultTableModel examModelList;

    private JTable examTableEdit;
    private DefaultTableModel examModelEdit;

    private JTable lecturerTable;
    private DefaultTableModel lecturerModel;

    private JTable subjectTable;

    private DefaultTableModel subjectModel;

    private JTable studentTable;
    private DefaultTableModel studentModel;

    private JTextField txtAddCode;
    private JTextField txtAddTitle;
    private JComboBox<String> cbAddRoom;
    private JTextField txtAddDuration;
    private JComboBox<String> cbAddClass;
    private JComboBox<String> cbAddCourse;
    private JComboBox<String> cbAddType;
    private JComboBox<String> cbAddDay;
    private JComboBox<String> cbAddMonth;
    private JComboBox<String> cbAddYear;
    private JComboBox<String> cbAddHour;
    private JComboBox<String> cbAddMinute;
    private JComboBox<String> cbAddLecturer;

    private JTextField txtEditCode;
    private JTextField txtEditClass;
    private JTextField txtEditTitle;
    private JComboBox<String> cbEditCourse;
    private JComboBox<String> cbEditRoom;
    private JTextField txtEditDuration;
    private JComboBox<String> cbEditType;
    private JComboBox<String> cbEditDay;
    private JComboBox<String> cbEditMonth;
    private JComboBox<String> cbEditYear;
    private JComboBox<String> cbEditHour;
    private JComboBox<String> cbEditMinute;
    private JComboBox<String> cbEditLecturer;
    private JComboBox<String> cbEditProctor;
    private int selectedEditId = -1;

    private JTextField txtNewClassName;
    private JTextField txtStudentCount;
    private JPanel pnlStudentInputs;
    private List<JTextField> listStudentNames = new ArrayList<>();
    private List<JTextField> listStudentNims = new ArrayList<>();

    private JTextField txtNewRoomName;

    private final String adminUsername;

    private final Color COLOR_PRIMARY = new Color(88, 101, 242);
    private final Color COLOR_PRIMARY_LIGHT = new Color(224, 231, 255);
    private final Color COLOR_PRIMARY_DARK = new Color(67, 56, 202);
    private final Color COLOR_BG_OFF_WHITE = new Color(248, 250, 252);
    private final Color COLOR_BG_SIDEBAR = new Color(88, 101, 242); // Neo Blue Sidebar
    private final Color COLOR_TEXT_HEADER = Color.BLACK;
    private final Color COLOR_TEXT_BODY = Color.DARK_GRAY;
    private final Color COLOR_SUCCESS = new Color(16, 185, 129);
    private final Color COLOR_SUCCESS_LIGHT = new Color(209, 250, 229);
    private final Color COLOR_WARNING = new Color(245, 158, 11);

    private final Color COLOR_ACCENT_RED = new Color(239, 68, 68);
    private final Color COLOR_ACCENT_RED_LIGHT = new Color(254, 226, 226);
    private final Color COLOR_BORDER = Color.BLACK; // tebel Black border
    private final Color COLOR_TABLE_HEADER = Color.WHITE;
    private final Color COLOR_TABLE_HOVER = new Color(241, 245, 249);
    private final String LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    private JPanel sidebarPanel;
    private JLabel lblLogoText;
    private JLabel lblLogoSubtitle;
    private JLabel quickActionsLabel;

    private List<JButton> sidebarButtons = new ArrayList<>();

    private final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font FONT_BODY_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_ICON = new Font("Segoe UI Emoji", Font.PLAIN, 14); // benerin buat square icons

    public KelolaJadwalUjianFrame(String adminUsername) {
        this.adminUsername = adminUsername;
        setTitle("SiUjian - Administrator Dashboard");
        setSize(1440, 900);
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Fullscreen window
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setBackground(COLOR_BG_OFF_WHITE);
        initComponents();
        loadAllData();
        switchView("VIEW_DASHBOARD", btnMenuDashboard);
    }

    private void initComponents() {
        JPanel sidebarPanel = createSidebar();
        add(sidebarPanel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(COLOR_BG_OFF_WHITE);
        JPanel headerPanel = createTopHeader();
        rightPanel.add(headerPanel, BorderLayout.NORTH);

        contentCardLayout = new CardLayout();
        mainContentPanel = new JPanel(contentCardLayout);
        mainContentPanel.setBackground(COLOR_BG_OFF_WHITE);
        mainContentPanel.setBorder(new EmptyBorder(25, 30, 25, 30));

        mainContentPanel.add(createDashboardView(), "VIEW_DASHBOARD");
        mainContentPanel.add(createPenjadwalanView(), "VIEW_PENJADWALAN");
        mainContentPanel.add(createDirectoryView(), "VIEW_DIRECTORY");
        mainContentPanel.add(createAboutView(), "VIEW_ABOUT");

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        sidebarPanel = new JPanel(new BorderLayout());
        sidebarPanel.setBackground(COLOR_BG_SIDEBAR);
        sidebarPanel.setPreferredSize(new Dimension(300, getHeight())); // Reduce width slightly
        sidebarPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Color.BLACK));

        // Brand Panel
        JPanel brandPanel = new JPanel(new BorderLayout());
        brandPanel.setOpaque(false);
        brandPanel.setBorder(new EmptyBorder(30, 25, 30, 25));

        JPanel logoContainer = new JPanel(new BorderLayout(15, 0));
        logoContainer.setOpaque(false);

        JLabel iconLabel = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            Image img = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
            iconLabel.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            iconLabel.setText("\uD83D\uDEE1");
            iconLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 40));
        }

        JPanel textContainer = new JPanel(new GridLayout(2, 1));
        textContainer.setOpaque(false);
        lblLogoText = new JLabel("SiUjian");
        lblLogoText.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblLogoText.setForeground(Color.WHITE);
        lblLogoSubtitle = new JLabel("Admin Panel");
        lblLogoSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblLogoSubtitle.setForeground(new Color(224, 231, 255));
        textContainer.add(lblLogoText);
        textContainer.add(lblLogoSubtitle);

        logoContainer.add(iconLabel, BorderLayout.WEST);
        logoContainer.add(textContainer, BorderLayout.CENTER);

        brandPanel.add(logoContainer, BorderLayout.CENTER);

        // Menu Panel
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel menuTitle = new JLabel("MENU UTAMA");
        menuTitle.setFont(new Font("Segoe UI", Font.BOLD, 12));
        menuTitle.setForeground(new Color(224, 231, 255)); // Light Blue teks
        menuTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        menuTitle.setBorder(new EmptyBorder(0, 10, 10, 0));
        menuPanel.add(menuTitle);

        sidebarButtons.clear();

        btnMenuDashboard = createModernSidebarButton("Dashboard", new IconDashboard(20, Color.WHITE),
                new IconDashboard(20, COLOR_PRIMARY));
        btnMenuPenjadwalan = createModernSidebarButton("Penjadwalan", new IconKalender(20, Color.WHITE),
                new IconKalender(20, COLOR_PRIMARY));
        btnMenuDirectory = createModernSidebarButton("Direktori Data", new IconDirektori(20, Color.WHITE),
                new IconDirektori(20, COLOR_PRIMARY));
        btnMenuAbout = createModernSidebarButton("Tentang Aplikasi", new IconInfo(20, Color.WHITE),
                new IconInfo(20, COLOR_PRIMARY));

        sidebarButtons.add(btnMenuDashboard);
        sidebarButtons.add(btnMenuPenjadwalan);
        sidebarButtons.add(btnMenuDirectory);
        sidebarButtons.add(btnMenuAbout);

        btnMenuDashboard.addActionListener(e -> {
            switchView("VIEW_DASHBOARD", btnMenuDashboard);
            loadAllData();
        });
        btnMenuPenjadwalan.addActionListener(e -> {
            switchView("VIEW_PENJADWALAN", btnMenuPenjadwalan);
            loadAllData();
        });
        btnMenuDirectory.addActionListener(e -> {
            switchView("VIEW_DIRECTORY", btnMenuDirectory);
            loadAllData();
        });
        btnMenuAbout.addActionListener(e -> switchView("VIEW_ABOUT", btnMenuAbout));

        for (JButton btn : sidebarButtons) {
            menuPanel.add(btn);
            menuPanel.add(Box.createVerticalStrut(15)); // More spacing
        }

        // Quick Actions
        JPanel quickActionsPanel = new JPanel();
        quickActionsPanel.setLayout(new BoxLayout(quickActionsPanel, BoxLayout.Y_AXIS));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setBorder(new EmptyBorder(20, 20, 0, 20));

        quickActionsLabel = new JLabel("AKSI CEPAT");
        quickActionsLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        quickActionsLabel.setForeground(new Color(224, 231, 255));
        quickActionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickActionsLabel.setBorder(new EmptyBorder(0, 10, 10, 0));

        JButton btnQuickAdd = createModernActionButton("Tambah Jadwal", new IconTambah(16, Color.WHITE),
                new Color(16, 185, 129));
        JButton btnQuickExport = createModernActionButton("Ekspor Data", new IconEkspor(16, Color.WHITE),
                new Color(245, 158, 11));

        btnQuickAdd.addActionListener(e -> {
            switchView("VIEW_PENJADWALAN", btnMenuPenjadwalan);
            try {
                JTabbedPane tabs = (JTabbedPane) ((JPanel) mainContentPanel.getComponent(1)).getComponent(1);
                tabs.setSelectedIndex(1);
            } catch (Exception ex) {
            }
        });
        btnQuickExport.addActionListener(e -> showExportDialog());

        quickActionsPanel.add(quickActionsLabel);
        quickActionsPanel.add(btnQuickAdd);
        quickActionsPanel.add(Box.createVerticalStrut(10));
        quickActionsPanel.add(btnQuickExport);

        // Profile Panel
        JPanel profilePanel = new JPanel(new BorderLayout(15, 0));
        profilePanel.setOpaque(false);
        profilePanel.setBorder(new EmptyBorder(20, 20, 30, 20));

        JLabel lblUser = new JLabel(adminUsername);
        lblUser.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lblUser.setForeground(Color.WHITE);
        JLabel lblRole = new JLabel("Administrator");
        lblRole.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblRole.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel(new GridLayout(2, 1));
        textPanel.setOpaque(false);
        textPanel.add(lblUser);
        textPanel.add(lblRole);

        profilePanel.add(textPanel, BorderLayout.CENTER);

        // Assembly
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setOpaque(false);
        contentWrapper.add(menuPanel, BorderLayout.NORTH);
        contentWrapper.add(quickActionsPanel, BorderLayout.CENTER);

        sidebarPanel.add(brandPanel, BorderLayout.NORTH);
        sidebarPanel.add(contentWrapper, BorderLayout.CENTER);
        sidebarPanel.add(profilePanel, BorderLayout.SOUTH);

        return sidebarPanel;
    }

    // baru: Modern sidebar tombol Neo-Brutalism dengan state Active
    private JButton createModernSidebarButton(String text, Icon iconInactive, Icon iconActive) {
        JButton btn = new JButton("  " + text) {
            private boolean isActive = false;

            @Override
            public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
                if ("activeState".equals(propertyName)) {
                    this.isActive = newValue;
                    setIcon(newValue ? iconActive : iconInactive);
                    setForeground(newValue ? COLOR_PRIMARY : Color.WHITE);
                    repaint();
                }
                super.firePropertyChange(propertyName, oldValue, newValue);
            }

            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                boolean isPressed = getModel().isPressed();
                boolean isRollover = getModel().isRollover();

                if (isActive || isRollover || isPressed) { // Active State (White bg, Blue Text)
                    // Shadow
                    if (!isPressed) {
                        g2.setColor(Color.BLACK);
                        g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
                    }

                    int offset = isPressed ? 4 : 0;
                    g2.setColor(Color.WHITE);
                    g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(2));
                    g2.drawRect(offset, offset, getWidth() - 5, getHeight() - 5);

                    // Foreground color is set by firePropertyChange or rollover listener
                } else { // Inactive (Transparent)
                    // Foreground color is set by firePropertyChange
                }

                super.paintComponent(g2);
                g2.dispose();
            }
        };
        btn.setIcon(iconInactive);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 50));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setBorder(new EmptyBorder(12, 15, 12, 15));
        btn.setForeground(Color.WHITE);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        return btn;
    }

    // baru: Modern action tombol
    private JButton createModernActionButton(String text, Icon icon, Color color) {
        JButton btn = createStyledButton(" " + text, true, color);
        btn.setIcon(icon);
        btn.setPreferredSize(new Dimension(200, 42)); // Fixed width buat actions
        return btn;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 80));
        header.setBorder(new EmptyBorder(0, 30, 0, 30));

        // tebel border bawah
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK),
                new EmptyBorder(0, 30, 0, 30)));

        JPanel titlePanel = new JPanel(new BorderLayout(0, 4));
        titlePanel.setOpaque(false);

        JLabel lblTitle = new JLabel("Dashboard Administrator");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(COLOR_TEXT_HEADER);

        JLabel lblDesc = new JLabel("Ringkasan jadwal ujian yang terdaftar");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(COLOR_TEXT_BODY);

        titlePanel.add(lblTitle, BorderLayout.NORTH);
        titlePanel.add(lblDesc, BorderLayout.SOUTH);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        statsPanel.setOpaque(false);

        // System Health Widget (Simple)
        JLabel lblServerStatus = new JLabel("System Online \u2022");
        lblServerStatus.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblServerStatus.setForeground(COLOR_SUCCESS);
        statsPanel.add(lblServerStatus);

        JButton btnHeaderAdd = createStyledButton(" + Tambah Jadwal", true, COLOR_PRIMARY);
        btnHeaderAdd.setPreferredSize(new Dimension(160, 40));
        btnHeaderAdd.addActionListener(e -> {
            switchView("VIEW_PENJADWALAN", btnMenuPenjadwalan);
            try {
                JTabbedPane tabs = (JTabbedPane) ((JPanel) mainContentPanel.getComponent(1)).getComponent(1);
                tabs.setSelectedIndex(1);
            } catch (Exception ex) {
            }
        });

        statsPanel.add(btnHeaderAdd);

        header.add(titlePanel, BorderLayout.WEST);
        header.add(statsPanel, BorderLayout.EAST);

        return header;
    }

    private JPanel createDashboardView() {
        JPanel panel = new JPanel(new BorderLayout(0, 25));
        panel.setBackground(COLOR_BG_OFF_WHITE);
        panel.setBorder(new EmptyBorder(0, 0, 20, 0)); // Slight bottom padding

        JPanel statsCardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsCardsPanel.setBackground(COLOR_BG_OFF_WHITE);
        statsCardsPanel.setBorder(new EmptyBorder(0, 0, 15, 0));

        lblCardTotal = new JLabel("0");
        lblCardOngoing = new JLabel("0");
        lblCardScheduled = new JLabel("0");
        lblCardAlert = new JLabel("0");

        statsCardsPanel.add(
                createStatCard("Total Ujian", lblCardTotal, new IconDashboard(32, COLOR_PRIMARY), COLOR_PRIMARY,
                        "+12% dari bulan lalu"));
        statsCardsPanel
                .add(createStatCard("Sedang Berlangsung", lblCardOngoing, new IconStatus(24, COLOR_SUCCESS),
                        COLOR_SUCCESS, "Hari ini"));
        statsCardsPanel
                .add(createStatCard("Terjadwal", lblCardScheduled, new IconKalender(32, new Color(139, 92, 246)),
                        new Color(139, 92, 246),
                        "7 hari ke depan"));
        statsCardsPanel
                .add(createStatCard("Peringatan", lblCardAlert, new IconPeringatan(32, COLOR_ACCENT_RED),
                        COLOR_ACCENT_RED, "Perlu perhatian"));

        // --- Container for Tables (Vertical Layout) ---
        JPanel tablesContainer = new JPanel();
        tablesContainer.setLayout(new BoxLayout(tablesContainer, BoxLayout.Y_AXIS));
        tablesContainer.setBackground(COLOR_BG_OFF_WHITE);

        // --- Table 1: ONGOING ---
        RoundedPanel cardOngoing = new RoundedPanel(16, Color.WHITE);
        cardOngoing.setLayout(new BorderLayout());
        cardOngoing.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardOngoing.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400)); // Limit height

        JPanel headerOngoing = new JPanel(new BorderLayout());
        headerOngoing.setBackground(Color.WHITE);
        headerOngoing.setBorder(new EmptyBorder(0, 0, 15, 0));
        JLabel titleOngoing = new JLabel("Jadwal Ujian Aktif (Berlangsung)");
        titleOngoing.setFont(FONT_ICON.deriveFont(Font.BOLD, 18f));
        titleOngoing.setForeground(COLOR_TEXT_HEADER);
        headerOngoing.add(titleOngoing, BorderLayout.WEST);

        // Actions for Ongoing
        JPanel actionsOngoing = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionsOngoing.setOpaque(false);
        JButton btnExportOn = createTableActionButton("Ekspor", new IconEkspor(16, Color.WHITE), Color.BLACK,
                Color.WHITE);
        btnExportOn.addActionListener(e -> showExportDialog());
        actionsOngoing.add(btnExportOn);
        headerOngoing.add(actionsOngoing, BorderLayout.EAST);

        dashboardModel = new DefaultTableModel(new Object[] { "ID", "Kode", "Kelas", "Judul", "Matkul", "Jenis",
                "Durasi", "Pengajar", "Status", "Waktu", "Aksi" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        dashboardTable = createStyledTable(dashboardModel);
        setupDashboardTableRenderers(dashboardTable); // Refactored renderer setup

        JScrollPane scrollOngoing = new JScrollPane(dashboardTable);
        scrollOngoing.setBorder(BorderFactory.createEmptyBorder());
        scrollOngoing.getViewport().setBackground(Color.WHITE);
        scrollOngoing.setPreferredSize(new Dimension(0, 250)); // Fixed height preference

        cardOngoing.add(headerOngoing, BorderLayout.NORTH);
        cardOngoing.add(scrollOngoing, BorderLayout.CENTER);

        // --- Table 2: SCHEDULED ---
        RoundedPanel cardScheduled = new RoundedPanel(16, Color.WHITE);
        cardScheduled.setLayout(new BorderLayout());
        cardScheduled.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardScheduled.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        JPanel headerScheduled = new JPanel(new BorderLayout());
        headerScheduled.setBackground(Color.WHITE);
        headerScheduled.setBorder(new EmptyBorder(0, 0, 15, 0));
        JLabel titleScheduled = new JLabel("Jadwal Ujian Terjadwal");
        titleScheduled.setFont(FONT_ICON.deriveFont(Font.BOLD, 18f));
        titleScheduled.setForeground(COLOR_TEXT_HEADER);
        headerScheduled.add(titleScheduled, BorderLayout.WEST);

        modelScheduled = new DefaultTableModel(new Object[] { "ID", "Kode", "Kelas", "Judul", "Matkul", "Jenis",
                "Durasi", "Pengajar", "Status", "Waktu", "Aksi" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tableScheduled = createStyledTable(modelScheduled);
        setupDashboardTableRenderers(tableScheduled);

        JScrollPane scrollScheduled = new JScrollPane(tableScheduled);
        scrollScheduled.setBorder(BorderFactory.createEmptyBorder());
        scrollScheduled.getViewport().setBackground(Color.WHITE);
        scrollScheduled.setPreferredSize(new Dimension(0, 250));

        cardScheduled.add(headerScheduled, BorderLayout.NORTH);
        cardScheduled.add(scrollScheduled, BorderLayout.CENTER);

        // Add to container with spacing
        tablesContainer.add(cardOngoing);
        tablesContainer.add(Box.createVerticalStrut(25));
        tablesContainer.add(cardScheduled);

        // Main Refresh Layout
        JScrollPane mainScroll = new JScrollPane(tablesContainer);
        mainScroll.setBorder(null);
        mainScroll.getVerticalScrollBar().setUnitIncrement(16);
        mainScroll.setBackground(COLOR_BG_OFF_WHITE);

        panel.add(statsCardsPanel, BorderLayout.NORTH);
        panel.add(mainScroll, BorderLayout.CENTER);
        return panel;
    }

    // Helper to setup renderers for any dashboard-like table
    private void setupDashboardTableRenderers(JTable table) {
        // Custom Renderer for Status (Index 8)
        table.getColumnModel().getColumn(8).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel();
                label.setOpaque(true);
                label.setFont(new Font("Segoe UI", Font.BOLD, 12));
                label.setHorizontalAlignment(SwingConstants.CENTER);

                if (value != null) {
                    String status = value.toString();
                    label.setText(status);

                    if ("ONGOING".equalsIgnoreCase(status)) {
                        label.setBackground(new Color(209, 250, 229)); // Green Light
                        label.setForeground(new Color(6, 95, 70)); // Green Dark
                        label.setText("BERLANGSUNG");
                    } else if ("SCHEDULED".equalsIgnoreCase(status)) {
                        label.setBackground(new Color(219, 234, 254)); // Blue Light
                        label.setForeground(new Color(30, 64, 175)); // Blue Dark
                        label.setText("TERJADWAL");
                    } else if ("FINISHED".equalsIgnoreCase(status)) {
                        label.setBackground(new Color(243, 244, 246)); // Gray Light
                        label.setForeground(new Color(31, 41, 55)); // Gray Dark
                        label.setText("SELESAI");
                    } else {
                        label.setBackground(Color.WHITE);
                        label.setForeground(Color.BLACK);
                    }
                    label.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));
                }
                if (isSelected) {
                    label.setBackground(table.getSelectionBackground());
                    label.setForeground(table.getSelectionForeground());
                }
                return label;
            }
        });

        // Add Mouse Listener for Actions (Edit/Delete) - Copied logic
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());
                if (row >= 0 && col == 10) { // Aksi Column
                    handleTableAction(table, row);
                }
            }
        });
    }

    private void handleTableAction(JTable table, int row) {
        // Generic action handler, simplify for now (just copy logic if complex, or
        // leave Action column empty/basic)
        // For simplicity in this refactor, we skip the complex coordinate click
        // detection for dual buttons unless requested.
        // We will make the row selection trigger the Edit tab switch for now as a
        // simple fallback, or implement simple dialog.
        // Or better: Just switch to Edit tab and select the row.
        try {
            int id = Integer.parseInt(table.getValueAt(row, 0).toString());
            switchView("VIEW_PENJADWALAN", btnMenuPenjadwalan);
            JTabbedPane tabs = (JTabbedPane) ((JPanel) mainContentPanel.getComponent(1)).getComponent(1);
            tabs.setSelectedIndex(2); // Edit & Hapus tab

            // Find row in examTableEdit
            for (int i = 0; i < examTableEdit.getRowCount(); i++) {
                int editId = Integer.parseInt(examTableEdit.getValueAt(i, 0).toString());
                if (editId == id) {
                    examTableEdit.setRowSelectionInterval(i, i);
                    examTableEdit.scrollRectToVisible(examTableEdit.getCellRect(i, 0, true));
                    populateEditForm();
                    break;
                }
            }
        } catch (Exception ex) {
        }
    }

    private JPanel createPenjadwalanView() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_BG_OFF_WHITE);

        RoundedPanel headerCard = new RoundedPanel(16, Color.WHITE);
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel lblTitle = new JLabel("Manajemen Penjadwalan");
        lblTitle.setFont(FONT_ICON.deriveFont(Font.BOLD, 24f));
        lblTitle.setForeground(COLOR_TEXT_HEADER);
        JLabel lblDesc = new JLabel("Kelola data jadwal ujian, ruangan, dan kelas secara terpusat");
        lblDesc.setFont(FONT_BODY);
        lblDesc.setForeground(COLOR_TEXT_BODY);
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 5));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblDesc);
        headerCard.add(titleBox, BorderLayout.WEST);
        panel.add(headerCard, BorderLayout.NORTH);

        RoundedPanel contentCard = new RoundedPanel(16, Color.WHITE);
        contentCard.setLayout(new BorderLayout());
        contentCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabs.setFont(FONT_ICON.deriveFont(14f));

        tabs.setBackground(Color.WHITE);
        tabs.setForeground(COLOR_TEXT_HEADER);
        tabs.setBorder(BorderFactory.createEmptyBorder());

        tabs.addTab("Daftar Jadwal", createTabDaftar());
        tabs.addTab("Tambah Jadwal", createTabTambah());
        tabs.addTab("Edit & Hapus", createTabEdit());
        tabs.addTab("Tambah Kelas", createTabTambahKelas());
        tabs.addTab("Tambah Ruangan", createTabTambahRuangan());

        contentCard.add(tabs, BorderLayout.CENTER);
        panel.add(contentCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabDaftar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(10, 0, 0, 0));

        examModelList = new DefaultTableModel(new Object[] { "ID", "Kode", "Kelas", "Judul", "Matkul (Kode)", "Jenis",
                "Tahun", "Durasi", "Pengajar", "Pengawas", "Ruangan", "Status", "Waktu" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTableList = createStyledTable(examModelList);

        examTableList.getColumnModel().getColumn(11).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (value != null) {
                    String status = value.toString();
                    String displayStatus = status.equals("ONGOING") ? "BERLANGSUNG"
                            : (status.equals("SCHEDULED") ? "TERJADWAL" : status);
                    JLabel label = new JLabel(displayStatus);
                    label.setHorizontalAlignment(SwingConstants.CENTER);
                    label.setOpaque(true);
                    label.setFont(new Font("Segoe UI", Font.BOLD, 12));

                    if (status.equals("ONGOING")) {
                        label.setBackground(COLOR_SUCCESS_LIGHT);
                        label.setForeground(COLOR_SUCCESS);
                        label.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(COLOR_SUCCESS_LIGHT.darker()),
                                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                    } else if (status.equals("SCHEDULED")) {
                        label.setBackground(new Color(219, 234, 254));
                        label.setForeground(COLOR_PRIMARY);
                        label.setBorder(BorderFactory.createCompoundBorder(
                                BorderFactory.createLineBorder(new Color(191, 219, 254)),
                                BorderFactory.createEmptyBorder(6, 12, 6, 12)));
                    }

                    return label;
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(examTableList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getViewport().setBackground(Color.WHITE);

        panel.add(scroll, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabTambah() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(15, 20, 15, 20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtAddCode = new JTextField();
        txtAddCode.setEditable(false);
        txtAddCode.setText(generateExamCode());
        txtAddTitle = new JTextField();
        cbAddRoom = new JComboBox<>();
        txtAddDuration = new JTextField("90");

        cbAddClass = new JComboBox<>();
        cbAddCourse = new JComboBox<>();
        cbAddType = new JComboBox<>(new String[] { "UTS", "UAS", "HARIAN", "REMEDIAL" });

        cbAddDay = new JComboBox<>();
        cbAddMonth = new JComboBox<>();
        cbAddYear = new JComboBox<>();
        cbAddHour = new JComboBox<>();
        cbAddMinute = new JComboBox<>();
        cbAddLecturer = new JComboBox<>();
        // cbAddProctor removed

        for (int i = 1; i <= 31; i++)
            cbAddDay.addItem(String.format("%02d", i));
        for (int i = 1; i <= 12; i++)
            cbAddMonth.addItem(String.format("%02d", i));
        int curYear = Year.now().getValue();
        for (int i = curYear - 1; i <= curYear + 2; i++)
            cbAddYear.addItem(String.valueOf(i));
        for (int i = 0; i < 24; i++)
            cbAddHour.addItem(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5)
            cbAddMinute.addItem(String.format("%02d", i));

        styleFormField(txtAddCode);
        styleFormField(txtAddTitle);
        styleComboBox(cbAddRoom);
        styleFormField(txtAddDuration);
        styleComboBox(cbAddClass);
        styleComboBox(cbAddCourse);
        styleComboBox(cbAddType);
        styleComboBox(cbAddDay);
        styleComboBox(cbAddMonth);
        styleComboBox(cbAddYear);
        styleComboBox(cbAddHour);
        styleComboBox(cbAddMinute);
        styleComboBox(cbAddLecturer);

        gbc.gridx = 0;
        gbc.gridy = 0;
        addFormRowSimple(form, gbc, "Kode Ujian", txtAddCode);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addFormRowSimple(form, gbc, "Kelas Target", cbAddClass);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addFormRowSimple(form, gbc, "Judul Ujian", txtAddTitle);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addFormRowSimple(form, gbc, "Mata Kuliah (Kode)", cbAddCourse);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addFormRowSimple(form, gbc, "Ruangan", cbAddRoom);
        gbc.gridx = 1;
        gbc.gridy = 4;
        addFormRowSimple(form, gbc, "Jenis Ujian", cbAddType);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JLabel lblTanggal = new JLabel("Tanggal (DD-MM-YYYY)");
        lblTanggal.setFont(FONT_BODY_BOLD);
        lblTanggal.setForeground(COLOR_TEXT_HEADER);
        form.add(lblTanggal, gbc);
        gbc.gridy++;
        JPanel datePanel = new JPanel(new GridLayout(1, 3, 6, 0));
        datePanel.setBackground(Color.WHITE);
        datePanel.add(cbAddDay);
        datePanel.add(cbAddMonth);
        datePanel.add(cbAddYear);
        form.add(datePanel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JLabel lblJam = new JLabel("Jam (HH:mm)");
        lblJam.setFont(FONT_BODY_BOLD);
        lblJam.setForeground(COLOR_TEXT_HEADER);
        form.add(lblJam, gbc);
        gbc.gridy++;
        JPanel timePanel = new JPanel(new GridLayout(1, 2, 6, 0));
        timePanel.setBackground(Color.WHITE);
        timePanel.add(cbAddHour);
        timePanel.add(cbAddMinute);
        form.add(timePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 8;
        addFormRowSimple(form, gbc, "Durasi (Menit)", txtAddDuration);
        gbc.gridx = 1;
        gbc.gridy = 8;
        addFormRowSimple(form, gbc, "Pengajar (Username)", cbAddLecturer);

        gbc.gridx = 0;
        gbc.gridy = 10;
        // ilangind Proctor and PDF selection

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        panel.add(scrollForm, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        JButton btnGenerateCode = createStyledButton("Reset Kode", false, COLOR_PRIMARY);
        btnGenerateCode.setIcon(new IconInfo(14, COLOR_PRIMARY));
        btnGenerateCode.addActionListener(e -> txtAddCode.setText(generateExamCode()));
        JButton btnSave = createStyledButton("Simpan Jadwal Baru", true, COLOR_PRIMARY);
        // btnSave.setIcon(new IconSimpan(14, Color.WHITE)); // Icon Removed
        btnSave.addActionListener(e -> saveNewSchedule());
        btnPanel.add(btnGenerateCode);
        btnPanel.add(btnSave);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTabEdit() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        examModelEdit = new DefaultTableModel(new Object[] { "ID", "Kode", "Judul", "Matkul", "Ruang", "Waktu" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        examTableEdit = createStyledTable(examModelEdit);
        examTableEdit.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                populateEditForm();
            }
        });

        JScrollPane scrollTable = new JScrollPane(examTableEdit);
        scrollTable.setPreferredSize(new Dimension(0, 220));
        scrollTable.setBorder(BorderFactory.createTitledBorder("ðŸ“‹ Pilih Jadwal untuk Diedit/Hapus"));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        form.setBorder(BorderFactory.createTitledBorder("âœï¸ Form Edit Data"));

        txtEditCode = new JTextField();
        txtEditCode.setEditable(false);
        txtEditClass = new JTextField();
        txtEditTitle = new JTextField();
        cbEditCourse = new JComboBox<>();
        cbEditRoom = new JComboBox<>();
        txtEditDuration = new JTextField();
        cbEditType = new JComboBox<>(new String[] { "UTS", "UAS", "HARIAN", "REMEDIAL" });
        cbEditDay = new JComboBox<>();
        cbEditMonth = new JComboBox<>();
        cbEditYear = new JComboBox<>();
        cbEditHour = new JComboBox<>();
        cbEditMinute = new JComboBox<>();
        cbEditLecturer = new JComboBox<>();
        cbEditProctor = new JComboBox<>();

        for (int i = 1; i <= 31; i++)
            cbEditDay.addItem(String.format("%02d", i));
        for (int i = 1; i <= 12; i++)
            cbEditMonth.addItem(String.format("%02d", i));
        int curYear = Year.now().getValue();
        for (int i = curYear - 1; i <= curYear + 2; i++)
            cbEditYear.addItem(String.valueOf(i));
        for (int i = 0; i < 24; i++)
            cbEditHour.addItem(String.format("%02d", i));
        for (int i = 0; i < 60; i += 5)
            cbEditMinute.addItem(String.format("%02d", i));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        gbc.gridx = 0;
        gbc.gridy = 0;
        addFormRowSimple(form, gbc, "Kode", txtEditCode);
        gbc.gridx = 1;
        gbc.gridy = 0;
        addFormRowSimple(form, gbc, "Kelas Target", txtEditClass);
        gbc.gridx = 2;
        gbc.gridy = 0;
        addFormRowSimple(form, gbc, "Judul", txtEditTitle);

        gbc.gridx = 0;
        gbc.gridy = 2;
        addFormRowSimple(form, gbc, "Matkul (Kode)", cbEditCourse);
        gbc.gridx = 1;
        gbc.gridy = 2;
        addFormRowSimple(form, gbc, "Ruang", cbEditRoom);
        gbc.gridx = 2;
        gbc.gridy = 2;
        addFormRowSimple(form, gbc, "Jenis", cbEditType);

        gbc.gridx = 0;
        gbc.gridy = 4;
        addFormRowSimple(form, gbc, "Durasi", txtEditDuration);
        gbc.gridx = 1;
        gbc.gridy = 4;
        addFormRowSimple(form, gbc, "Pengajar", cbEditLecturer);
        gbc.gridx = 2;
        gbc.gridy = 4;
        addFormRowSimple(form, gbc, "Pengawas", cbEditProctor);

        gbc.gridx = 0;
        gbc.gridy = 6;
        JPanel dateP = new JPanel(new GridLayout(1, 3, 4, 0));
        dateP.setBackground(Color.WHITE);
        dateP.add(cbEditDay);
        dateP.add(cbEditMonth);
        dateP.add(cbEditYear);
        addFormRowSimple(form, gbc, "Tanggal", dateP);

        gbc.gridx = 1;
        gbc.gridy = 6;
        JPanel timeP = new JPanel(new GridLayout(1, 2, 4, 0));
        timeP.setBackground(Color.WHITE);
        timeP.add(cbEditHour);
        timeP.add(cbEditMinute);
        addFormRowSimple(form, gbc, "Jam", timeP);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(new EmptyBorder(15, 0, 0, 0));
        JButton btnUpdate = createStyledButton("ðŸ’¾ Simpan Perubahan", true, COLOR_PRIMARY);
        JButton btnDelete = createStyledButton("ðŸ—‘ï¸ Hapus Jadwal", false, COLOR_ACCENT_RED);
        JButton btnImport = createStyledButton("ðŸ“¥ Import Soal (Auto-Grade)", false, new Color(16, 185, 129));

        btnUpdate.addActionListener(e -> updateSchedule());
        btnDelete.addActionListener(e -> deleteScheduleFromEdit());
        btnImport.addActionListener(e -> importQuestionsForUjian());

        btnPanel.add(btnDelete);
        btnPanel.add(btnImport);
        btnPanel.add(btnUpdate);

        JPanel bottomContainer = new JPanel(new BorderLayout());
        bottomContainer.setBackground(Color.WHITE);
        bottomContainer.add(form, BorderLayout.CENTER);
        bottomContainer.add(btnPanel, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scrollTable, bottomContainer);
        split.setDividerLocation(220);
        panel.add(split, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTabTambahKelas() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel topControl = new JPanel(new GridBagLayout());
        topControl.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtNewClassName = new JTextField(25);
        txtStudentCount = new JTextField(8);
        styleFormField(txtNewClassName);
        styleFormField(txtStudentCount);

        JButton btnGenerate = createStyledButton("Generate Input", true, COLOR_PRIMARY);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblClassName = new JLabel("Nama Kelas:");
        lblClassName.setFont(FONT_BODY_BOLD);
        topControl.add(lblClassName, gbc);
        gbc.gridx = 1;
        topControl.add(txtNewClassName, gbc);
        gbc.gridx = 2;
        JLabel lblStudentCount = new JLabel("Jumlah Siswa:");
        lblStudentCount.setFont(FONT_BODY_BOLD);
        topControl.add(lblStudentCount, gbc);
        gbc.gridx = 3;
        topControl.add(txtStudentCount, gbc);
        gbc.gridx = 4;
        topControl.add(btnGenerate, gbc);

        JButton btnImportCsv = createStyledButton("Import CSV", false, new Color(16, 185, 129));
        gbc.gridx = 5;
        topControl.add(btnImportCsv, gbc);

        btnImportCsv.addActionListener(e -> importStudentCsv());

        pnlStudentInputs = new JPanel();
        pnlStudentInputs.setLayout(new BoxLayout(pnlStudentInputs, BoxLayout.Y_AXIS));
        pnlStudentInputs.setBackground(Color.WHITE);
        JScrollPane scrollPane = new JScrollPane(pnlStudentInputs);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Input Data Siswa"));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        btnGenerate.addActionListener(e -> {
            try {
                int count = Integer.parseInt(txtStudentCount.getText());
                pnlStudentInputs.removeAll();
                listStudentNames.clear();
                listStudentNims.clear();

                for (int i = 0; i < count; i++) {
                    JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                    row.setBackground(Color.WHITE);

                    JLabel lblStudent = new JLabel("Siswa " + (i + 1) + " - Nama: ");
                    lblStudent.setFont(FONT_BODY);
                    row.add(lblStudent);

                    JTextField txtName = new JTextField(25);
                    styleFormField(txtName);
                    listStudentNames.add(txtName);
                    row.add(txtName);

                    row.add(new JLabel(" NIM: "));
                    JTextField txtNim = new JTextField(20);
                    styleFormField(txtNim);
                    listStudentNims.add(txtNim);
                    row.add(txtNim);

                    pnlStudentInputs.add(row);
                }
                pnlStudentInputs.revalidate();
                pnlStudentInputs.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Masukkan angka jumlah siswa yang valid!");
            }
        });

        JButton btnSaveClass = createStyledButton("Simpan Data Kelas & Siswa", true, COLOR_PRIMARY);
        btnSaveClass.addActionListener(e -> saveClassData());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.add(btnSaveClass);

        panel.add(topControl, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createTabTambahRuangan() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        txtNewRoomName = new JTextField();
        styleFormField(txtNewRoomName);
        gbc.gridx = 0;
        gbc.gridy = 0;
        addFormRowSimple(form, gbc, "Nama Ruangan Baru", txtNewRoomName);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(Color.WHITE);
        JButton btnSaveRoom = createStyledButton("Simpan Ruangan", true, COLOR_PRIMARY);
        btnSaveRoom.addActionListener(e -> saveRoomData());
        btnPanel.add(btnSaveRoom);

        panel.add(form, BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDirectoryView() {
        JPanel panel = new JPanel(new BorderLayout(0, 15));
        panel.setBackground(COLOR_BG_OFF_WHITE);

        RoundedPanel headerCard = new RoundedPanel(16, Color.WHITE);
        headerCard.setLayout(new BorderLayout());
        headerCard.setBorder(new EmptyBorder(25, 30, 25, 30));
        JLabel lblTitle = new JLabel("ðŸ‘¥ Direktori Dosen & Matkul");
        lblTitle.setFont(FONT_ICON.deriveFont(Font.BOLD, 24f));
        lblTitle.setForeground(COLOR_TEXT_HEADER);
        JLabel lblDesc = new JLabel("Tambahkan dan kelola data dosen (Users) dan mata kuliah (Subjects).");
        lblDesc.setFont(FONT_BODY);
        lblDesc.setForeground(COLOR_TEXT_BODY);
        JPanel titleBox = new JPanel(new GridLayout(2, 1, 0, 5));
        titleBox.setOpaque(false);
        titleBox.add(lblTitle);
        titleBox.add(lblDesc);
        headerCard.add(titleBox, BorderLayout.WEST);
        panel.add(headerCard, BorderLayout.NORTH);

        RoundedPanel contentCard = new RoundedPanel(16, Color.WHITE);
        contentCard.setLayout(new BorderLayout());
        contentCard.setBorder(new EmptyBorder(10, 10, 10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_ICON.deriveFont(14f));

        lecturerModel = new DefaultTableModel(new Object[] { "ID", "Username", "Nama Lengkap" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        lecturerTable = createStyledTable(lecturerModel);
        JScrollPane spLect = new JScrollPane(lecturerTable);
        spLect.setBorder(BorderFactory.createEmptyBorder());
        spLect.getViewport().setBackground(Color.WHITE);

        subjectModel = new DefaultTableModel(
                new Object[] { "ID", "Kode Matkul", "Nama Matkul", "Kelas", "Dosen Pengampu (User)" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        subjectTable = createStyledTable(subjectModel);
        JScrollPane spSub = new JScrollPane(subjectTable);
        spSub.setBorder(BorderFactory.createEmptyBorder());
        spSub.getViewport().setBackground(Color.WHITE);

        studentModel = new DefaultTableModel(
                new Object[] { "ID", "NIM", "Nama Lengkap", "Kelas" }, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = createStyledTable(studentModel);
        JScrollPane spStu = new JScrollPane(studentTable);
        spStu.setBorder(BorderFactory.createEmptyBorder());
        spStu.getViewport().setBackground(Color.WHITE);

        tabs.addTab("Daftar Dosen", spLect);
        tabs.addTab("Daftar Mata Kuliah", spSub);
        tabs.addTab("Daftar Mahasiswa", spStu);
        contentCard.add(tabs, BorderLayout.CENTER);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        actions.setBackground(Color.WHITE);

        JButton btnAddLect = createStyledButton("+ Tambah Dosen", true, COLOR_PRIMARY);
        JButton btnEditLect = createStyledButton("Edit Dosen", false, COLOR_PRIMARY);
        JButton btnDelLect = createStyledButton("Hapus Dosen", false, COLOR_ACCENT_RED);

        JButton btnAddSub = createStyledButton("+ Tambah Matkul", true, COLOR_PRIMARY);
        JButton btnEditSub = createStyledButton("Edit Matkul", false, COLOR_PRIMARY);
        JButton btnDelSub = createStyledButton("Hapus Matkul", false, COLOR_ACCENT_RED);

        JButton btnAddStu = createStyledButton("+ Tambah Mhs", true, COLOR_PRIMARY);
        JButton btnEditStu = createStyledButton("Edit Mhs", false, COLOR_PRIMARY);
        JButton btnDelStu = createStyledButton("Hapus Mhs", false, COLOR_ACCENT_RED);

        btnAddLect.addActionListener(e -> openAddLecturerDialog());
        btnEditLect.addActionListener(e -> openEditLecturerDialog());
        btnDelLect.addActionListener(e -> deleteSelectedLecturer());

        btnAddSub.addActionListener(e -> openAddSubjectDialog());
        btnEditSub.addActionListener(e -> openEditSubjectDialog());
        btnDelSub.addActionListener(e -> deleteSelectedSubject());

        btnAddStu.addActionListener(e -> openAddStudentDialog());
        btnEditStu.addActionListener(e -> openEditStudentDialog());
        btnDelStu.addActionListener(e -> deleteSelectedStudent());

        JPanel pnlLect = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlLect.setBackground(Color.WHITE);
        pnlLect.add(new JLabel("Dosen:"));
        pnlLect.add(btnAddLect);
        pnlLect.add(btnEditLect);
        pnlLect.add(btnDelLect);

        JPanel pnlSub = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlSub.setBackground(Color.WHITE);
        pnlSub.add(new JLabel("Matkul:"));
        pnlSub.add(btnAddSub);
        pnlSub.add(btnEditSub);
        pnlSub.add(btnDelSub);

        JPanel pnlStu = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        pnlStu.setBackground(Color.WHITE);
        pnlStu.add(new JLabel("Mhs:"));
        pnlStu.add(btnAddStu);
        pnlStu.add(btnEditStu);
        pnlStu.add(btnDelStu);

        // dinamis Action tombol Switching berdasarkan dipilih tab
        JPanel cardActionPanel = new JPanel(new CardLayout());
        cardActionPanel.add(pnlLect, "0");
        cardActionPanel.add(pnlSub, "1");
        cardActionPanel.add(pnlStu, "2");

        tabs.addChangeListener(e -> {
            CardLayout cl = (CardLayout) cardActionPanel.getLayout();
            cl.show(cardActionPanel, String.valueOf(tabs.getSelectedIndex()));
        });

        contentCard.add(cardActionPanel, BorderLayout.SOUTH);
        panel.add(contentCard, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAboutView() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(COLOR_BG_OFF_WHITE);
        JPanel contentWrapper = new JPanel(new GridBagLayout());
        contentWrapper.setBackground(COLOR_BG_OFF_WHITE);

        RoundedPanel card = new RoundedPanel(20, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(50, 70, 50, 70));
        card.setPreferredSize(new Dimension(650, 450));

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            Image img = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            lblLogo.setText("ðŸ›¡ï¸");
            lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 72));
            lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        JLabel lblTitle = new JLabel("SiUjian");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36));
        lblTitle.setForeground(COLOR_PRIMARY);
        lblTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblVer = new JLabel("Versi 2.0.1 (Stable Release)");
        lblVer.setFont(FONT_BODY);
        lblVer.setForeground(COLOR_TEXT_BODY);
        lblVer.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDesc = new JLabel(
                "<html><center>SiUjian adalah solusi manajemen ujian digital yang aman dan efisien.<br>Database Integrated: examguard (4).sql</center></html>");
        lblDesc.setFont(FONT_BODY);
        lblDesc.setForeground(COLOR_TEXT_HEADER);
        lblDesc.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblCredit = new JLabel("Â© 2025 Fikri Bintang Purnomo & Team");
        lblCredit.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblCredit.setForeground(COLOR_TEXT_BODY);
        lblCredit.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(lblLogo);
        card.add(Box.createVerticalStrut(15));
        card.add(lblTitle);
        card.add(Box.createVerticalStrut(10));
        card.add(lblVer);
        card.add(Box.createVerticalStrut(35));
        card.add(lblDesc);
        card.add(Box.createVerticalGlue());
        card.add(lblCredit);

        contentWrapper.add(card);
        panel.add(contentWrapper, BorderLayout.CENTER);
        return panel;
    }

    private JButton createTableActionButton(String text, Icon icon, Color color, Color textColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                // Shadow
                if (!getModel().isPressed()) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
                }

                // Body
                int offset = getModel().isPressed() ? 4 : 0;
                g2.setColor(color);
                g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

                // Border
                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(offset, offset, getWidth() - 5, getHeight() - 5);

                super.paintComponent(g2);

                // Draw Text manually to ensure it's on top and centered relative to the pressed
                // state
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
                // Adjust x if icon exists
                if (getIcon() != null) {
                    x += getIcon().getIconWidth() / 2 + 2;
                }

                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
                g2.setColor(textColor);
                // We rely on standard painting for text usually, but for custom buttons like
                // this super.paintComponent often does job if contentAreaFilled is false.
                // But let's just let super handle text/icon by setting opaque false.
                g2.dispose();
            }
        };
        btn.setIcon(icon);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setForeground(textColor);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 15, 8, 15));
        return btn;
    }

    private JButton createPaginationButton(String text, boolean active) {
        JButton btn = new JButton(text);
        btn.setFont(text.equals("â—€") || text.equals("â–¶") ? FONT_ICON.deriveFont(14f) : FONT_BODY);
        btn.setPreferredSize(new Dimension(40, 40));
        btn.setBackground(active ? COLOR_PRIMARY : Color.WHITE);
        btn.setForeground(active ? Color.WHITE : COLOR_TEXT_HEADER);
        btn.setBorder(new RoundedBorder(active ? COLOR_PRIMARY : COLOR_BORDER, 10));
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (!active) {
            btn.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    btn.setBackground(COLOR_BG_OFF_WHITE);
                }

                public void mouseExited(MouseEvent e) {
                    btn.setBackground(Color.WHITE);
                }
            });
        }

        return btn;
    }

    private JPanel createStatCard(String title, JLabel lblValue, Icon icon, Color color, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 2),
                BorderFactory.createEmptyBorder(25, 25, 25, 25)));

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblValue.setForeground(COLOR_TEXT_HEADER);

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(FONT_BODY);
        lblTitle.setForeground(COLOR_TEXT_BODY);

        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSubtitle.setForeground(color);

        JPanel textPanel = new JPanel(new GridLayout(3, 1, 0, 5));
        textPanel.setOpaque(false);
        textPanel.add(lblValue);
        textPanel.add(lblTitle);
        textPanel.add(lblSubtitle);

        JLabel lblIcon = new JLabel();
        if (icon != null)
            lblIcon.setIcon(icon);

        card.add(textPanel, BorderLayout.WEST);
        card.add(lblIcon, BorderLayout.EAST);

        return card;
    }

    private JPanel createStatItem(String label, JLabel lblValue, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);

        lblValue.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblValue.setForeground(color);

        JLabel lblLabel = new JLabel(label);
        lblLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblLabel.setForeground(COLOR_TEXT_BODY);

        JPanel textPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textPanel.setOpaque(false);
        textPanel.add(lblValue);
        textPanel.add(lblLabel);

        panel.add(textPanel, BorderLayout.CENTER);
        return panel;
    }

    private void styleFormField(JTextField field) {
        field.setFont(FONT_BODY);
        field.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDER, 10),
                BorderFactory.createEmptyBorder(10, 15, 10, 15)));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, 45));
        field.setBackground(new Color(235, 235, 235));
    }

    private void styleComboBox(JComboBox<String> combo) {
        combo.setFont(FONT_BODY);
        combo.setBackground(Color.WHITE);
        combo.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(COLOR_BORDER, 10),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        combo.setPreferredSize(new Dimension(combo.getPreferredSize().width, 45));
    }

    private void loadAllData() {
        if (dashboardModel != null)
            dashboardModel.setRowCount(0);
        if (modelScheduled != null)
            modelScheduled.setRowCount(0);
        examModelList.setRowCount(0);
        examModelEdit.setRowCount(0);
        lecturerModel.setRowCount(0);
        subjectModel.setRowCount(0);
        if (studentModel != null)
            studentModel.setRowCount(0);

        cbAddClass.removeAllItems();
        cbAddCourse.removeAllItems();
        cbEditCourse.removeAllItems();
        cbAddLecturer.removeAllItems();
        cbEditLecturer.removeAllItems();
        cbEditProctor.removeAllItems();
        cbAddRoom.removeAllItems();
        cbEditRoom.removeAllItems();

        // Use TreeSet buat keep classes sorted
        Set<String> classSet = new java.util.TreeSet<>();

        try {
            List<String> rooms = roomRepository.listRooms();
            for (String r : rooms) {
                cbAddRoom.addItem(r);
                cbEditRoom.addItem(r);
            }
        } catch (Exception e) {
            System.err.println("Gagal load ruangan: " + e.getMessage());
        }

        try {
            List<Object[]> lecturers = lecturerRepository.listLecturers();
            for (Object[] lect : lecturers) {
                lecturerModel.addRow(lect);
                String username = (String) lect[1];
                cbAddLecturer.addItem(username);
                cbEditLecturer.addItem(username);
                cbEditProctor.addItem(username);
            }
        } catch (Exception e) {
            System.err.println("Gagal load dosen: " + e.getMessage());
        }

        try {
            List<Object[]> subjects = subjectRepository.listSubjects();
            subjectModel = new DefaultTableModel(new Object[] { "ID", "Kode", "Nama", "Kelas", "Dosen", "Pengawas" },
                    0); // Re-init with new col
            subjectTable.setModel(subjectModel); // set new model
            // sembunyiin ID column
            subjectTable.getColumnModel().getColumn(0).setMinWidth(0);
            subjectTable.getColumnModel().getColumn(0).setMaxWidth(0);
            subjectTable.getColumnModel().getColumn(0).setWidth(0);

            for (Object[] sub : subjects) {
                subjectModel.addRow(sub);
                String code = (String) sub[1];
                String className = (String) sub[3];

                cbAddCourse.addItem(code);
                cbEditCourse.addItem(code);

                if (className != null && !className.isEmpty())
                    classSet.add(className);
            }
        } catch (Exception e) {
            System.err.println("Gagal load matkul: " + e.getMessage());
        }

        try {
            if (studentModel != null) {
                List<Object[]> students = studentRepository.listStudents();
                for (Object[] stu : students) {
                    studentModel.addRow(stu);
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal load mahasiswa: " + e.getMessage());
        }

        // NEW: Load from students table as requested
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(
                        "SELECT DISTINCT nama_kelas FROM mahasiswa WHERE nama_kelas IS NOT NULL AND nama_kelas != '' ORDER BY nama_kelas ASC")) {
            try (java.sql.ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    classSet.add(rs.getString("nama_kelas"));
                }
            }
        } catch (Exception e) {
            System.err.println("Gagal load kelas students: " + e.getMessage());
        }

        try {
            List<Object[]> exams = examRepository.listAllExamsForAdmin();

            int total = 0;
            int ongoing = 0;
            int scheduled = 0;
            int alert = 0;

            for (Object[] row : exams) {
                total++;
                String status = (String) row[11];
                if (status != null)
                    status = status.trim();
                System.out.println("DEBUG: Exam ID " + row[0] + " has status: '" + status + "'");

                if ("ONGOING".equalsIgnoreCase(status)) {
                    ongoing++;
                } else if ("SCHEDULED".equalsIgnoreCase(status)) {
                    scheduled++;
                } else {
                    alert++;
                }

                // Map raw row buat dashboard columns:
                Object[] rowObj = new Object[] {
                        row[0], // ID
                        row[1], // Kode
                        row[2], // Kelas
                        row[3], // Judul
                        (row.length > 13 && row[13] != null ? row[13] : row[4]), // Matkul
                        row[5], // Jenis
                        row[7] + " menit", // Durasi
                        row[8], // Pengajar
                        row[11], // Status
                        row[12], // Waktu
                        "Buka"
                };

                // Split Logic
                if ("ONGOING".equalsIgnoreCase(status)) {
                    if (dashboardModel != null)
                        dashboardModel.addRow(rowObj);
                } else {
                    // Fallback: Add to Scheduled table if not ONGOING (covers SCHEDULED, FINISHED,
                    // etc.)
                    // This ensures data is visible.
                    if (modelScheduled != null)
                        modelScheduled.addRow(rowObj);
                }

                examModelList.addRow(new Object[] {
                        row[0], // ID
                        row[1], // Kode
                        row[2], // Kelas
                        row[3], // Judul
                        (row.length > 13 && row[13] != null ? row[13] : row[4]), // Matkul (Display)
                        row[5], // Jenis
                        row[6], // Tahun/Date part
                        row[7] + " mnt", // Durasi
                        row[8], // Pengajar
                        row[9], // Pengawas
                        row[10], // Ruangan
                        row[11], // Status
                        row[12] // Waktu
                });

                examModelEdit.addRow(new Object[] {
                        row[0],
                        row[1],
                        row[3],
                        (row.length > 13 && row[13] != null ? row[13] : row[4]),
                        row[10],
                        row.length > 12 ? row[12] : row[11]
                });

                if (row[2] != null)
                    classSet.add(row[2].toString());
            }

            // update header Stats
            if (lblStatTotal != null)
                lblStatTotal.setText(String.valueOf(total));
            if (lblStatOngoing != null)
                lblStatOngoing.setText(String.valueOf(ongoing));
            if (lblStatScheduled != null)
                lblStatScheduled.setText(String.valueOf(scheduled));
            if (lblStatAlert != null)
                lblStatAlert.setText(String.valueOf(alert));

            // update Card Stats (sama logika)
            if (lblCardTotal != null)
                lblCardTotal.setText(String.valueOf(total));
            if (lblCardOngoing != null)
                lblCardOngoing.setText(String.valueOf(ongoing));
            if (lblCardScheduled != null)
                lblCardScheduled.setText(String.valueOf(scheduled));
            if (lblCardAlert != null)
                lblCardAlert.setText(String.valueOf(alert));

        } catch (

        Exception e) {
            System.err.println("Gagal load exam: " + e.getMessage());
        }

        // akhir consolidated population dari class Combo
        for (String c : classSet) {
            cbAddClass.addItem(c);
        }
    }

    private void saveClassData() {
        String className = txtNewClassName.getText().trim();
        if (className.isEmpty() || listStudentNames.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama kelas dan data siswa tidak boleh kosong.");
            return;
        }

        String sql = "INSERT INTO mahasiswa (nim, name, nama_kelas) VALUES (?, ?, ?)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {

            conn.setAutoCommit(false);
            for (int i = 0; i < listStudentNames.size(); i++) {
                String name = listStudentNames.get(i).getText();
                String nim = listStudentNims.get(i).getText();

                if (!name.isEmpty() && !nim.isEmpty()) {
                    ps.setString(1, nim);
                    ps.setString(2, name);
                    ps.setString(3, className);
                    ps.addBatch();
                }
            }
            ps.executeBatch();
            conn.commit();
            conn.setAutoCommit(true);

            JOptionPane.showMessageDialog(this, "âœ… Data kelas dan siswa berhasil disimpan!");
            loadAllData();
            txtNewClassName.setText("");
            txtStudentCount.setText("");
            pnlStudentInputs.removeAll();
            pnlStudentInputs.repaint();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "âŒ Gagal simpan: " + e.getMessage());
        }
    }

    private void saveRoomData() {
        String roomName = txtNewRoomName.getText().trim();
        if (roomName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nama ruangan tidak boleh kosong.");
            return;
        }

        try {
            roomRepository.createRoom(roomName);
            JOptionPane.showMessageDialog(this, "âœ… Ruangan berhasil disimpan!");
            loadAllData(); // refresh dropdowns
            txtNewRoomName.setText("");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "âŒ Gagal simpan: " + e.getMessage());
        }
    }

    private void saveNewSchedule() {
        try {
            String code = txtAddCode.getText();
            String cls = cbAddClass.getSelectedItem() != null ? cbAddClass.getSelectedItem().toString() : "";
            String title = txtAddTitle.getText();
            String subjCode = (String) cbAddCourse.getSelectedItem();
            String room = cbAddRoom.getSelectedItem() != null ? cbAddRoom.getSelectedItem().toString() : "";
            String type = (String) cbAddType.getSelectedItem();

            String date = cbAddYear.getSelectedItem() + "-" + cbAddMonth.getSelectedItem() + "-"
                    + String.format("%02d", Integer.parseInt((String) cbAddDay.getSelectedItem()));
            String time = cbAddHour.getSelectedItem() + ":" + cbAddMinute.getSelectedItem() + ":00";
            String fullTime = date + " " + time;

            int year = Integer.parseInt((String) cbAddYear.getSelectedItem());
            int dur = Integer.parseInt(txtAddDuration.getText());
            String lectUser = (String) cbAddLecturer.getSelectedItem();

            // Proctor adalah explicitly null/empty sekarang, buat be assigned oleh Lecturer
            String procUser = "";

            // IMPORTANT: Ensure ga ada buka connections block ini.
            // Although examRepository.createUjian handles it, we harusnya be careful.
            examRepository.createUjian(code, cls, title, subjCode, type, fullTime, year, dur, lectUser, procUser, room);
            JOptionPane.showMessageDialog(this, "âœ… Berhasil tambah jadwal! Tugas telah diberikan ke Dosen.");
            loadAllData();
            txtAddCode.setText(generateExamCode());
            txtAddTitle.setText("");
            txtAddDuration.setText("90");
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "âŒ Gagal simpan: " + ex.getMessage());
        }
    }

    private void populateEditForm() {
        int row = examTableEdit.getSelectedRow();
        if (row == -1)
            return;
        selectedEditId = Integer.parseInt(examModelEdit.getValueAt(row, 0).toString());

        try {
            List<Object[]> all = examRepository.listAllExamsForAdmin();
            Object[] data = null;
            for (Object[] obj : all) {
                if ((int) obj[0] == selectedEditId) {
                    data = obj;
                    break;
                }
            }

            if (data == null)
                return;

            txtEditCode.setText((String) data[1]);
            txtEditClass.setText((String) data[2]);
            txtEditTitle.setText((String) data[3]);
            cbEditCourse.setSelectedItem(data[4]);
            cbEditType.setSelectedItem(data[5]);
            txtEditDuration.setText(String.valueOf(data[7]));
            cbEditLecturer.setSelectedItem(data[8]);
            cbEditProctor.setSelectedItem(data[9]);
            cbEditRoom.setSelectedItem(data[10] != null ? data[10].toString() : "");

            Object timeObj = data.length > 12 ? data[12] : data[11];
            if (timeObj instanceof Timestamp) {
                LocalDateTime ldt = ((Timestamp) timeObj).toLocalDateTime();
                cbEditDay.setSelectedItem(String.format("%02d", ldt.getDayOfMonth()));
                cbEditMonth.setSelectedItem(String.format("%02d", ldt.getMonthValue()));
                cbEditYear.setSelectedItem(String.valueOf(ldt.getYear()));
                cbEditHour.setSelectedItem(String.format("%02d", ldt.getHour()));
                cbEditMinute.setSelectedItem(String.format("%02d", ldt.getMinute()));
            } else if (timeObj instanceof String) {
                String s = (String) timeObj;
                if (s.contains(" ") && s.contains("-") && s.contains(":")) {
                    LocalDateTime ldt = LocalDateTime.parse(s, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    cbEditDay.setSelectedItem(String.format("%02d", ldt.getDayOfMonth()));
                    cbEditMonth.setSelectedItem(String.format("%02d", ldt.getMonthValue()));
                    cbEditYear.setSelectedItem(String.valueOf(ldt.getYear()));
                    cbEditHour.setSelectedItem(String.format("%02d", ldt.getHour()));
                    cbEditMinute.setSelectedItem(String.format("%02d", ldt.getMinute()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateSchedule() {
        if (selectedEditId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal dari tabel di atas!");
            return;
        }
        try {
            String code = txtEditCode.getText();
            String cls = txtEditClass.getText();
            String title = txtEditTitle.getText();
            String subjCode = (String) cbEditCourse.getSelectedItem();
            String room = cbEditRoom.getSelectedItem() != null ? cbEditRoom.getSelectedItem().toString() : "";
            String type = (String) cbEditType.getSelectedItem();

            String date = cbEditYear.getSelectedItem() + "-" + cbEditMonth.getSelectedItem() + "-"
                    + String.format("%02d", Integer.parseInt((String) cbEditDay.getSelectedItem()));
            String time = cbEditHour.getSelectedItem() + ":" + cbEditMinute.getSelectedItem() + ":00";
            String fullTime = date + " " + time;

            int year = Integer.parseInt((String) cbEditYear.getSelectedItem());
            int dur = Integer.parseInt(txtEditDuration.getText());
            String lectUser = (String) cbEditLecturer.getSelectedItem();
            String procUser = (String) cbEditProctor.getSelectedItem();

            examRepository.updateUjian(selectedEditId, code, cls, title, subjCode, type, fullTime, year, dur, lectUser,
                    procUser, room);
            JOptionPane.showMessageDialog(this, "âœ… Jadwal berhasil diperbarui!");
            loadAllData();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "âŒ Gagal update: " + ex.getMessage());
        }
    }

    private void importQuestionsForUjian() {
        if (selectedEditId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal dari tabel di atas terlebih dahulu!");
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Pilih File Soal (PDF/TXT)");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("PDF & TXT Files", "pdf", "txt"));

        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try {
                // Determine file type
                String name = file.getName().toLowerCase();
                if (!name.endsWith(".pdf") && !name.endsWith(".txt")) {
                    JOptionPane.showMessageDialog(this, "Format file harus PDF atau TXT!");
                    return;
                }

                // tampilin loading
                setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

                // proses Import
                // proses Import
                ImportSoalService importer = new ImportSoalService();
                importer.importFromFile(file, selectedEditId);

                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "âœ… Berhasil Mengimpor Soal ke Database!\n\n"
                        + "Sekarang ujian ini mendukung Auto-Grading (Penilaian Otomatis).");

            } catch (Exception e) {
                setCursor(Cursor.getDefaultCursor());
                JOptionPane.showMessageDialog(this, "âŒ Gagal Import: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void deleteScheduleFromEdit() {
        if (selectedEditId == -1) {
            JOptionPane.showMessageDialog(this, "Pilih jadwal dari tabel di atas!");
            return;
        }
        int opt = JOptionPane.showConfirmDialog(this, "Hapus jadwal ID " + selectedEditId + "?", "ðŸ—‘ï¸ Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            try {
                examRepository.deleteUjian(selectedEditId);
                loadAllData();
                selectedEditId = -1;
                JOptionPane.showMessageDialog(this, "âœ… Jadwal dihapus.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal hapus: " + ex.getMessage());
            }
        }
    }

    private void openAddLecturerDialog() {
        JTextField txtUser = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtPass = new JPasswordField();
        Object[] message = {
                "Username:", txtUser,
                "Nama Lengkap:", txtName,
                "Password Default:", txtPass
        };

        int option = JOptionPane.showConfirmDialog(this, message, "âž• Tambah Dosen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                lecturerRepository.createLecturer(txtUser.getText(), txtPass.getText(), txtName.getText());
                loadAllData();
                JOptionPane.showMessageDialog(this, "âœ… Dosen berhasil ditambahkan.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal: " + e.getMessage());
            }
        }
    }

    private void openEditLecturerDialog() {
        int row = lecturerTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih dosen di tabel.");
            return;
        }
        int id = Integer.parseInt(lecturerModel.getValueAt(row, 0).toString());
        String currentName = lecturerModel.getValueAt(row, 2).toString();
        String currentUser = lecturerModel.getValueAt(row, 1).toString();

        JTextField txtName = new JTextField(currentName);
        JTextField txtUser = new JTextField(currentUser);
        Object[] message = { "Username:", txtUser, "Nama Lengkap:", txtName };

        int option = JOptionPane.showConfirmDialog(this, message, "âœï¸ Edit Dosen", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                lecturerRepository.updateLecturer(id, txtUser.getText(), txtName.getText());
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal update: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedLecturer() {
        int row = lecturerTable.getSelectedRow();
        if (row == -1)
            return;
        String username = lecturerModel.getValueAt(row, 1).toString();
        if (JOptionPane.showConfirmDialog(this, "Hapus dosen '" + username + "'?", "ðŸ—‘ï¸ Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                lecturerRepository.deleteLecturerByUsername(username);
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal hapus: " + e.getMessage());
            }
        }
    }

    private void openAddSubjectDialog() {
        // Auto-generate Code internally atau sembunyiin it
        // User requested: Name, class (Dropdown), Lecturer (Dropdown), Proctor
        // (Dropdown)

        JTextField txtName = new JTextField();
        JComboBox<String> cbClass = new JComboBox<>();
        JComboBox<String> cbLect = new JComboBox<>();
        JComboBox<String> cbProctor = new JComboBox<>();

        // Populate class dari existing global list
        for (int i = 0; i < cbAddClass.getItemCount(); i++) {
            cbClass.addItem(cbAddClass.getItemAt(i));
        }

        // Populate Lecturers/Proctors dari global list
        for (int i = 0; i < lecturerModel.getRowCount(); i++) {
            String uname = lecturerModel.getValueAt(i, 1).toString();
            cbLect.addItem(uname);
            cbProctor.addItem(uname);
        }

        Object[] message = {
                "Nama Matkul:", txtName,
                "Kelas:", cbClass,
                "Dosen (Pengajar):", cbLect,
                "Pengawas:", cbProctor
        };

        if (JOptionPane.showConfirmDialog(this, message, "âž• Tambah Matkul",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String code = "MK-" + System.currentTimeMillis() % 100000; // Auto-generate simple code
                String selectedLecturer = (String) cbLect.getSelectedItem();
                String selectedProctor = (String) cbProctor.getSelectedItem();
                String selectedClass = (String) cbClass.getSelectedItem();

                subjectRepository.createSubject(code, txtName.getText(), selectedClass, selectedLecturer,
                        selectedProctor);
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal: " + e.getMessage());
            }
        }
    }

    private void openEditSubjectDialog() {
        int row = subjectTable.getSelectedRow();
        if (row == -1)
            return;

        int id = Integer.parseInt(subjectModel.getValueAt(row, 0).toString());
        String currentCode = subjectModel.getValueAt(row, 1).toString();
        JTextField txtName = new JTextField(subjectModel.getValueAt(row, 2).toString());

        // class ComboBox
        JComboBox<String> cbClass = new JComboBox<>();
        String currentClass = subjectModel.getValueAt(row, 3) != null ? subjectModel.getValueAt(row, 3).toString() : "";
        for (int i = 0; i < cbAddClass.getItemCount(); i++) {
            cbClass.addItem(cbAddClass.getItemAt(i));
        }
        cbClass.setSelectedItem(currentClass);

        // Lecturer & Proctor
        JComboBox<String> cbLect = new JComboBox<>();
        JComboBox<String> cbProctor = new JComboBox<>();
        String currentLect = subjectModel.getValueAt(row, 4) != null ? subjectModel.getValueAt(row, 4).toString() : "";
        // Proctor is index 5 now after repo update, but tabel model might need
        // adjustment
        // in loadAllData if we want to show it.
        // For now, let's assume index 5 might be proctor if we update loadAllData, or
        // ambil from DB if needed.
        // Wait, I haven't updated loadAllData table model column!
        // To be safe, I'll update loadAllData logic too or just rely on what is
        // available.
        // sebenernya, let's look di listSubjects di repo, it returns proctor di index
        // 5.
        // So I must update loadAllData to put it in table model.

        // Assuming loadAllData puts it in model, let's grab it.
        // But first, let's implement the dialog assuming data is there.
        String currentProctor = "";
        if (subjectModel.getColumnCount() > 5) { // cek if we updated model
            Object val = subjectModel.getValueAt(row, 5);
            if (val != null)
                currentProctor = val.toString();
        }

        for (int i = 0; i < lecturerModel.getRowCount(); i++) {
            String u = lecturerModel.getValueAt(i, 1).toString();
            cbLect.addItem(u);
            cbProctor.addItem(u);
        }
        cbLect.setSelectedItem(currentLect);
        cbProctor.setSelectedItem(currentProctor);

        Object[] message = {
                "Nama:", txtName,
                "Kelas:", cbClass,
                "Dosen:", cbLect,
                "Pengawas:", cbProctor
        };

        if (JOptionPane.showConfirmDialog(this, message, "âœï¸ Edit Matkul",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                String uLect = (String) cbLect.getSelectedItem();
                String uProctor = (String) cbProctor.getSelectedItem();
                String uClass = (String) cbClass.getSelectedItem();

                subjectRepository.updateSubject(id, currentCode, txtName.getText(), uClass, uLect, uProctor);
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedSubject() {
        int row = subjectTable.getSelectedRow();
        if (row == -1)
            return;
        int id = Integer.parseInt(subjectModel.getValueAt(row, 0).toString());
        if (JOptionPane.showConfirmDialog(this, "Hapus matkul ini?", "ðŸ—‘ï¸ Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                subjectRepository.deleteSubject(id);
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal hapus: " + e.getMessage());
            }
        }
    }

    private void openAddStudentDialog() {
        JTextField txtNim = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtClass = new JTextField();

        Object[] message = {
                "NIM:", txtNim,
                "Nama Lengkap:", txtName,
                "Kelas:", txtClass
        };

        if (JOptionPane.showConfirmDialog(this, message, "âž• Tambah Mahasiswa",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                studentRepository.createStudent(txtNim.getText(), txtName.getText(), txtClass.getText());
                loadAllData();
                JOptionPane.showMessageDialog(this, "âœ… Mahasiswa berhasil ditambahkan.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal: " + e.getMessage());
            }
        }
    }

    private void openEditStudentDialog() {
        int row = studentTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih mahasiswa di tabel.");
            return;
        }

        int id = Integer.parseInt(studentModel.getValueAt(row, 0).toString());
        String currentNim = studentModel.getValueAt(row, 1).toString();
        String currentName = studentModel.getValueAt(row, 2).toString();
        String currentClass = studentModel.getValueAt(row, 3).toString();

        JTextField txtNim = new JTextField(currentNim);
        JTextField txtName = new JTextField(currentName);
        JTextField txtClass = new JTextField(currentClass);

        Object[] message = {
                "NIM:", txtNim,
                "Nama Lengkap:", txtName,
                "Kelas:", txtClass
        };

        if (JOptionPane.showConfirmDialog(this, message, "âœï¸ Edit Mahasiswa",
                JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
            try {
                studentRepository.updateStudent(id, txtNim.getText(), txtName.getText(), txtClass.getText());
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal update: " + e.getMessage());
            }
        }
    }

    private void deleteSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row == -1)
            return;

        String name = studentModel.getValueAt(row, 2).toString();
        int id = Integer.parseInt(studentModel.getValueAt(row, 0).toString());

        if (JOptionPane.showConfirmDialog(this, "Hapus mahasiswa '" + name + "'?", "ðŸ—‘ï¸ Konfirmasi",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            try {
                studentRepository.deleteStudent(id);
                loadAllData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal hapus: " + e.getMessage());
            }
        }
    }

    private void switchView(String viewName, JButton activeButton) {
        contentCardLayout.show(mainContentPanel, viewName);
        updateSidebarButtonState(btnMenuDashboard, false);
        updateSidebarButtonState(btnMenuPenjadwalan, false);
        updateSidebarButtonState(btnMenuDirectory, false);
        updateSidebarButtonState(btnMenuAbout, false);
        updateSidebarButtonState(activeButton, true);
    }

    private void updateSidebarButtonState(JButton btn, boolean isActive) {
        // Use property change to trigger our custom logic
        btn.firePropertyChange("activeState", !isActive, isActive);
        if (isActive) {
            // Ensure properties are consistent
            btn.setFont(FONT_BODY_BOLD);
        } else {
            btn.setFont(FONT_BODY);
        }
    }

    private JTable createStyledTable(DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setFont(FONT_BODY);
        table.setRowHeight(50); // Spacious rows
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(243, 244, 246)); // Very light gray splitter
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(249, 250, 251)); // Very subtle hover gray
        table.setSelectionForeground(Color.BLACK);

        // Hover effect listener
        table.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());
                if (row > -1) {
                    table.clearSelection();
                    table.setRowSelectionInterval(row, row);
                } else {
                    table.clearSelection();
                }
            }
        });

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setForeground(new Color(75, 85, 99));
        header.setBackground(COLOR_TABLE_HEADER);
        header.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_BORDER),
                BorderFactory.createEmptyBorder(0, 20, 0, 20)));
        header.setPreferredSize(new Dimension(0, 52));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(JLabel.LEFT);
        if (table.getColumnCount() > 0) {
            for (int i = 0; i < Math.min(4, table.getColumnCount()); i++) {
                table.getColumnModel().getColumn(i).setCellRenderer(leftRenderer);
            }
        }

        return table;
    }

    private JButton createStyledButton(String text, boolean isPrimary, Color baseColor) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

                if (!getModel().isPressed()) {
                    g2.setColor(Color.BLACK);
                    g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
                }
                int offset = getModel().isPressed() ? 4 : 0;

                Color bg = isPrimary ? baseColor : Color.WHITE;
                g2.setColor(bg);
                g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRect(offset, offset, getWidth() - 6, getHeight() - 6);

                g2.setColor(isPrimary ? Color.WHITE : Color.BLACK);
                FontMetrics fm = g2.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
                int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
                g2.drawString(getText(), x, y);
                g2.dispose();
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setForeground(isPrimary ? Color.WHITE : Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width, 45));
        return btn;
    }

    private void addFormRowSimple(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        JLabel label = new JLabel(labelText);
        label.setFont(FONT_BODY_BOLD);
        label.setForeground(COLOR_TEXT_HEADER);
        panel.add(label, gbc);

        gbc.gridy++;
        field.setFont(FONT_BODY);
        panel.add(field, gbc);
        gbc.gridy++;
    }

    private void showExportDialog() {
        JDialog dialog = new JDialog(this, "ðŸ“¤ Ekspor Data Jadwal", true);
        dialog.setSize(400, 320);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        JPanel pnlHeader = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlHeader.setBackground(Color.WHITE);
        pnlHeader.setBorder(new EmptyBorder(20, 0, 10, 0));
        JLabel lblTitle = new JLabel("Pilih Format Ekspor");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTitle.setForeground(COLOR_TEXT_HEADER);
        pnlHeader.add(lblTitle);

        JPanel pnlContent = new JPanel(new GridLayout(2, 1, 10, 10));
        pnlContent.setBackground(Color.WHITE);
        pnlContent.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnCsv = createStyledButton("Download CSV (Excel)", true, new Color(16, 185, 129));
        btnCsv.setIcon(new IconEkspor(20, Color.WHITE));
        btnCsv.addActionListener(e -> {
            dialog.dispose();
            exportToCSV(dashboardTable);
        });

        JButton btnPdf = createStyledButton("Cetak / Simpan PDF", true, new Color(239, 68, 68));
        btnPdf.setIcon(new IconInfo(20, Color.WHITE)); // Reuse info icon as print icon placeholder
        btnPdf.addActionListener(e -> {
            dialog.dispose();
            exportToPDF(dashboardTable);
        });

        pnlContent.add(btnCsv);
        pnlContent.add(btnPdf);

        dialog.add(pnlHeader, BorderLayout.NORTH);
        dialog.add(pnlContent, BorderLayout.CENTER);
        dialog.setVisible(true);
    }

    private void exportToCSV(JTable table) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Simpan CSV");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getName().toLowerCase().endsWith(".csv")) {
                file = new File(file.getParentFile(), file.getName() + ".csv");
            }

            try (java.io.PrintWriter pw = new java.io.PrintWriter(file)) {
                StringBuilder sb = new StringBuilder();

                // Header
                for (int i = 0; i < table.getColumnCount(); i++) {
                    if (i == 10)
                        continue; // Skip 'Aksi' column
                    sb.append(table.getColumnName(i));
                    if (i < table.getColumnCount() - 2)
                        sb.append(",");
                }
                pw.println(sb.toString());

                // Rows
                for (int i = 0; i < table.getRowCount(); i++) {
                    sb = new StringBuilder();
                    for (int j = 0; j < table.getColumnCount(); j++) {
                        if (j == 10)
                            continue; // Skip 'Aksi' column
                        Object val = table.getValueAt(i, j);
                        String s = val != null ? val.toString() : "";
                        // Escape quotes
                        s = s.replace("\"", "\"\"");
                        if (s.contains(",") || s.contains("\n")) {
                            s = "\"" + s + "\"";
                        }
                        sb.append(s);
                        if (j < table.getColumnCount() - 2)
                            sb.append(",");
                    }
                    pw.println(sb.toString());
                }

                JOptionPane.showMessageDialog(this, "Data berhasil diekspor ke CSV!");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal ekspor: " + ex.getMessage());
            }
        }
    }

    private void exportToPDF(JTable table) {
        try {
            // Use JTable standard print feature which opens system print dialog (allowing
            // Save as PDF)
            java.text.MessageFormat header = new java.text.MessageFormat("Laporan Jadwal Ujian");
            java.text.MessageFormat footer = new java.text.MessageFormat("Halaman {0}");
            boolean complete = table.print(JTable.PrintMode.FIT_WIDTH, header, footer);
            if (complete) {
                JOptionPane.showMessageDialog(this, "Proses cetak selesai.");
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Gagal mencetak: " + ex.getMessage());
        }
    }

    private String generateExamCode() {
        return "EXM-" + System.currentTimeMillis() / 1000;
    }

    class RoundedPanel extends JPanel {
        private int radius;

        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.radius = radius;
            setOpaque(false);
            setBackground(bgColor);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Hard shadow dengan Rounded Corners
            g2.setColor(Color.BLACK);
            g2.fillRoundRect(6, 6, getWidth() - 8, getHeight() - 8, radius, radius);

            // utama body dengan Rounded Corners
            g2.setColor(getBackground() != null ? getBackground() : Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 6, getHeight() - 6, radius, radius);

            // border dengan Rounded Corners
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRoundRect(0, 0, getWidth() - 7, getHeight() - 7, radius, radius);

            g2.dispose();
        }
    }

    private static class RoundedBorder implements Border {
        private int radius;
        private Color color;

        RoundedBorder(Color color, int radius) {
            this.color = color;
            this.radius = radius;
        }

        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        public boolean isBorderOpaque() {
            return true;
        }

        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.drawRoundRect(x, y, width - 1, height - 1, radius, radius);
        }
    }

    private void importStudentCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setDialogTitle("Pilih File CSV Siswa");
        fc.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("CSV Files", "csv"));

        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = fc.getSelectedFile();
            try (java.io.BufferedReader br = new java.io.BufferedReader(new java.io.FileReader(file))) {
                String line;
                // bersihin existing
                pnlStudentInputs.removeAll();
                listStudentNames.clear();
                listStudentNims.clear();

                int count = 0;
                while ((line = br.readLine()) != null) {
                    if (line.trim().isEmpty())
                        continue;
                    // Support comma atau semicolon
                    String[] parts = line.split("[,;]");
                    if (parts.length >= 2) {
                        String name = parts[0].trim(); // Name first
                        String nim = parts[1].trim(); // NIM second

                        // basic validation buat skip header kalo present
                        if (name.equalsIgnoreCase("Nama") && nim.equalsIgnoreCase("NIM"))
                            continue;

                        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
                        row.setBackground(Color.WHITE);

                        JLabel lblStudent = new JLabel("Siswa " + (count + 1) + " - Nama: ");
                        lblStudent.setFont(FONT_BODY);
                        row.add(lblStudent);

                        JTextField txtName = new JTextField(name, 25);
                        styleFormField(txtName);
                        listStudentNames.add(txtName);
                        row.add(txtName);

                        row.add(new JLabel(" NIM: "));
                        JTextField txtNim = new JTextField(nim, 20);
                        styleFormField(txtNim);
                        listStudentNims.add(txtNim);
                        row.add(txtNim);

                        pnlStudentInputs.add(row);
                        count++;
                    }
                }

                txtStudentCount.setText(String.valueOf(count));
                pnlStudentInputs.revalidate();
                pnlStudentInputs.repaint();
                JOptionPane.showMessageDialog(this, "âœ… Berhasil import " + count + " data siswa dari CSV.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "âŒ Gagal import CSV: " + e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }

        SwingUtilities.invokeLater(() -> {
            new KelolaJadwalUjianFrame("AdminTest").setVisible(true);
        });
    }
}
