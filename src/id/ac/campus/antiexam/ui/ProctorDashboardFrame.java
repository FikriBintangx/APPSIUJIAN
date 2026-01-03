package id.ac.campus.antiexam.ui;

import id.ac.campus.antiexam.repository.ExamRepository;
import id.ac.campus.antiexam.repository.SessionRepository;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ProctorDashboardFrame extends JFrame {

    private static final String APP_LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";
    private final String proctorUsername;
    private final ExamRepository examRepository = new ExamRepository();
    private final SessionRepository sessionRepository = new SessionRepository();

    private CardLayout contentCardLayout;
    private JPanel mainContentPanel;
    private JButton btnMenuOverview;
    private JButton btnMenuAccount;

    private JTable examTable;
    private DefaultTableModel examModel;
    private JTable sessionTable;
    private DefaultTableModel sessionModel;

    private JLabel lblStatTotal, lblStatOngoing, lblStatViolations;
    private JLabel lblMonitoringTitle;

    private Timer liveTimer;
    private int selectedExamId = -1;

    // Neo-Brutalism Palette
    private final Color COL_PRIMARY = new Color(88, 101, 242); // Neo Blue
    private final Color COL_SECONDARY = new Color(16, 185, 129);
    private final Color COL_BG_MAIN = new Color(248, 250, 252);
    private final Color COL_BG_SIDEBAR = new Color(88, 101, 242);

    private final Color COL_DANGER = new Color(239, 68, 68);
    private final Color COL_INFO = new Color(59, 130, 246);
    private final Font FONT_H2 = new Font("Segoe UI", Font.BOLD, 18);

    public ProctorDashboardFrame(String proctorUsername) {
        this.proctorUsername = proctorUsername;
        setTitle("SiUjian - Dashboard Pengawas (Neo)");
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

        rightPanel.add(mainContentPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(COL_BG_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(280, getHeight()));
        sidebar.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 4, Color.BLACK));

        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 18));
        brandPanel.setOpaque(false);
        JLabel lblLogo = new JLabel("SiUjian");
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        lblLogo.setForeground(Color.WHITE);
        JLabel lblSubtitle = new JLabel("Pengawas Panel");
        lblSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitle.setForeground(new Color(224, 231, 255));

        JPanel textPanel = new JPanel();
        textPanel.setOpaque(false);
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.add(lblLogo);
        textPanel.add(lblSubtitle);
        brandPanel.add(textPanel);

        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setOpaque(false);
        menuPanel.setBorder(new EmptyBorder(20, 16, 20, 16));

        btnMenuOverview = new NeoButton("Dashboard & Monitoring", Color.WHITE, COL_PRIMARY); // Active by default
        btnMenuAccount = new NeoButton("Informasi Akun", COL_BG_SIDEBAR, Color.WHITE); // Inactive style

        btnMenuOverview.addActionListener(e -> {
            contentCardLayout.show(mainContentPanel, "VIEW_OVERVIEW");
            ((NeoButton) btnMenuOverview).setColors(Color.WHITE, COL_PRIMARY);
            ((NeoButton) btnMenuAccount).setColors(COL_BG_SIDEBAR, Color.WHITE);
        });
        btnMenuAccount.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Login sebagai Pengawas: " + proctorUsername);
            ((NeoButton) btnMenuAccount).setColors(Color.WHITE, COL_PRIMARY);
            ((NeoButton) btnMenuOverview).setColors(COL_BG_SIDEBAR, Color.WHITE);
        });

        menuPanel.add(btnMenuOverview);
        menuPanel.add(Box.createVerticalStrut(15));
        menuPanel.add(btnMenuAccount);

        sidebar.add(brandPanel, BorderLayout.NORTH);
        sidebar.add(menuPanel, BorderLayout.CENTER);
        return sidebar;
    }

    private JPanel createTopHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(Color.WHITE);
        header.setPreferredSize(new Dimension(getWidth(), 70));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 4, 0, Color.BLACK));
        header.setBorder(new EmptyBorder(0, 32, 0, 32));

        JLabel lblTitle = new JLabel("Pengawasan Ujian");
        lblTitle.setFont(FONT_H2);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        NeoButton btnLogout = new NeoButton("Keluar", COL_DANGER, Color.WHITE);
        btnLogout.addActionListener(e -> {
            new RoleChooserFrame().setVisible(true);
            dispose();
        });
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
        statsPanel.add(createStatCard("Terdeteksi Curang", lblStatViolations, COL_DANGER));

        JPanel contentSplit = new JPanel(new BorderLayout(20, 0));
        contentSplit.setOpaque(false);

        // Exam List (Left)
        NeoPanel exCard = new NeoPanel();
        exCard.setLayout(new BorderLayout());
        exCard.setPreferredSize(new Dimension(450, 0));
        exCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel lblListTitle = new JLabel("Daftar Ujian Anda");
        lblListTitle.setFont(FONT_H2);
        NeoButton btnRefresh = new NeoButton("Refresh", Color.WHITE, Color.BLACK);
        btnRefresh.setPreferredSize(new Dimension(90, 40));
        btnRefresh.addActionListener(e -> loadExams());

        JPanel headEx = new JPanel(new BorderLayout());
        headEx.setOpaque(false);
        headEx.add(lblListTitle, BorderLayout.WEST);
        headEx.add(btnRefresh, BorderLayout.EAST);

        examModel = new DefaultTableModel(new Object[] { "ID", "Kode", "Kelas", "Judul", "Matkul", "Durasi", "Status" },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        examTable = createStyledTable(examModel);

        NeoButton btnSelect = new NeoButton("Mulai Monitoring", COL_PRIMARY, Color.WHITE);
        btnSelect.addActionListener(e -> selectExam());

        exCard.add(headEx, BorderLayout.NORTH);
        exCard.add(new JScrollPane(examTable), BorderLayout.CENTER);
        exCard.add(btnSelect, BorderLayout.SOUTH);

        // Monitoring (Center)
        NeoPanel monCard = new NeoPanel();
        monCard.setLayout(new BorderLayout());
        monCard.setBorder(new EmptyBorder(20, 20, 20, 20));

        lblMonitoringTitle = new JLabel("Live Monitoring");
        lblMonitoringTitle.setFont(FONT_H2);
        monCard.add(lblMonitoringTitle, BorderLayout.NORTH);

        sessionModel = new DefaultTableModel(new Object[] { "ID Sesi", "Mahasiswa", "Status", "Mulai", "Pelanggaran" },
                0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        sessionTable = createStyledTable(sessionModel);
        JScrollPane monScroll = new JScrollPane(sessionTable);
        monScroll.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));

        JPanel footerMon = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerMon.setOpaque(false);
        NeoButton btnFreeze = new NeoButton("Bekukan", COL_INFO, Color.WHITE);
        NeoButton btnUnfreeze = new NeoButton("Buka", COL_SECONDARY, Color.WHITE);
        NeoButton btnKick = new NeoButton("Stop", COL_DANGER, Color.WHITE);

        btnFreeze.addActionListener(e -> performAction("FREEZE"));
        btnUnfreeze.addActionListener(e -> performAction("UNFREEZE"));
        btnKick.addActionListener(e -> performAction("KICK"));

        footerMon.add(btnFreeze);
        footerMon.add(btnUnfreeze);
        footerMon.add(btnKick);

        monCard.add(monScroll, BorderLayout.CENTER);
        monCard.add(footerMon, BorderLayout.SOUTH);

        contentSplit.add(exCard, BorderLayout.WEST);
        contentSplit.add(monCard, BorderLayout.CENTER);

        panel.add(statsPanel, BorderLayout.NORTH);
        panel.add(contentSplit, BorderLayout.CENTER);
        return panel;
    }

    private NeoPanel createStatCard(String title, JLabel val, Color color) {
        NeoPanel p = new NeoPanel();
        p.setLayout(new BorderLayout());
        p.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel t = new JLabel(title);
        t.setFont(new Font("Segoe UI", Font.BOLD, 14));
        t.setForeground(Color.DARK_GRAY);

        val.setFont(new Font("Segoe UI", Font.BOLD, 36));
        val.setForeground(color);

        p.add(t, BorderLayout.NORTH);
        p.add(val, BorderLayout.CENTER);
        return p;
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

    // Logic Methods (Simplified)
    private void loadExams() {
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                try {
                    return examRepository.listExamsForProctor(proctorUsername);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new java.util.ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> list = get();
                    examModel.setRowCount(0);
                    for (Object[] r : list)
                        examModel.addRow(new Object[] { r[0], r[1], r[2], r[3], r[4], r[5], r[6] });
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void selectExam() {
        int r = examTable.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Pilih ujian!");
            return;
        }
        selectedExamId = (Integer) examModel.getValueAt(r, 0);
        String title = (String) examModel.getValueAt(r, 3);
        lblMonitoringTitle.setText("Monitoring: " + title);

        // Start Live Poll
        if (liveTimer == null) {
            liveTimer = new Timer(3000, e -> loadSessions());
            liveTimer.start();
        }
        loadSessions(); // Initial
        JOptionPane.showMessageDialog(this, "Monitoring dimulai untuk: " + title);
    }

    private void loadSessions() {
        if (selectedExamId == -1)
            return;
        new SwingWorker<List<Object[]>, Void>() {
            @Override
            protected List<Object[]> doInBackground() {
                try {
                    return sessionRepository.listSessionsSummary(selectedExamId);
                } catch (Exception e) {
                    e.printStackTrace();
                    return new java.util.ArrayList<>();
                }
            }

            @Override
            protected void done() {
                try {
                    List<Object[]> res = get();
                    sessionModel.setRowCount(0);
                    int total = res.size(), ongoing = 0, vio = 0;
                    for (Object[] row : res) {
                        String st = (String) row[3];
                        int v = (Integer) row[5];
                        if ("ONGOING".equals(st))
                            ongoing++;
                        if (v > 0)
                            vio++;
                        sessionModel.addRow(row);
                    }
                    lblStatTotal.setText("" + total);
                    lblStatOngoing.setText("" + ongoing);
                    lblStatViolations.setText("" + vio);
                } catch (Exception e) {
                }
            }
        }.execute();
    }

    private void performAction(String action) {
        int r = sessionTable.getSelectedRow();
        if (r == -1)
            return;
        int sid = (Integer) sessionModel.getValueAt(r, 0);
        try {
            if ("FREEZE".equals(action))
                sessionRepository.updateStatus(sid, "LOCKED");
            else if ("UNFREEZE".equals(action))
                sessionRepository.updateStatus(sid, "ONGOING");
            else if ("KICK".equals(action))
                sessionRepository.updateStatus(sid, "FINISHED");
            loadSessions();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Neo Components
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
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(off, off, getWidth() - 6, getHeight() - 6);
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(getText(), (getWidth() - fm.stringWidth(getText())) / 2 + off / 2,
                    (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + off / 2);
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
            g2.setColor(Color.BLACK);
            g2.fillRect(6, 6, getWidth() - 6, getHeight() - 6);
            g2.setColor(Color.WHITE);
            g2.fillRect(0, 0, getWidth() - 6, getHeight() - 6);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, getWidth() - 7, getHeight() - 7);
            g2.dispose();
        }
    }
}
