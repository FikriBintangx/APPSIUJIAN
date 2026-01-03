package id.ac.campus.antiexam.ui;

import id.ac.campus.antiexam.repository.AuthRepository;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class RoleChooserFrame extends JFrame {

    private final AuthRepository authRepository = new AuthRepository();

    // Colors
    private final Color CLR_BLUE = new Color(88, 101, 242); // Vibrant Blue from image
    private final Color CLR_BLACK = Color.BLACK;
    private final Color CLR_WHITE = Color.WHITE;
    private final Color CLR_BG = new Color(248, 250, 252); // Light background
    private final Font FONT_BOLD_L = new Font("Segoe UI", Font.BOLD, 36);
    private final Font FONT_BOLD_S = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    private JComboBox<String> cmbRole;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JLabel lblLabel1, lblLabel2;

    // Role Constants
    private final String ROLE_MAHASISWA = "Mahasiswa";
    private final String ROLE_DOSEN = "Dosen";
    private final String ROLE_PENGAWAS = "Pengawas";
    private final String ROLE_ADMIN = "Admin";

    // Toggle
    private boolean isPasswordVisible = false;

    public RoleChooserFrame() {
        setTitle("SiUjian - Masuk");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Background with Dot Pattern
        JPanel validContentPane = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(CLR_BG);
                g2.fillRect(0, 0, getWidth(), getHeight());

                // Dot pattern
                g2.setColor(new Color(226, 232, 240));
                for (int x = 0; x < getWidth(); x += 30) {
                    for (int y = 0; y < getHeight(); y += 30) {
                        g2.fillOval(x, y, 3, 3);
                    }
                }
            }
        };
        setContentPane(validContentPane);

        // Main Card Shadow Container
        JPanel mainCard = new JPanel(null);
        mainCard.setPreferredSize(new Dimension(900, 550));
        mainCard.setOpaque(false);

        // The actual card content
        JPanel cardContent = new JPanel(new GridLayout(1, 2));
        cardContent.setBounds(0, 0, 900, 550);
        cardContent.setBorder(BorderFactory.createLineBorder(CLR_BLACK, 4)); // Thick border

        // === LEFT PANEL ===
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(CLR_BLUE);
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));

        JLabel lblTitleLeft = new JLabel(
                "<html><div style='text-align:center;'>SiUjian: Akses<br>Berbasis<br>Peran Tersedia!</div></html>");
        lblTitleLeft.setFont(FONT_BOLD_L);
        lblTitleLeft.setForeground(CLR_WHITE);
        lblTitleLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblDescLeft = new JLabel(
                "<html><div style='text-align:center; width:280px;'>Pilih peran Anda untuk pengalaman yang disesuaikan: Admin, Pengawas, Mahasiswa, Dosen.</div></html>");
        lblDescLeft.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        lblDescLeft.setForeground(new Color(224, 231, 255));
        lblDescLeft.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Icon (Graduation Cap - approximated with text/emoji for simplicity in pure
        // java)
        JLabel lblIcon = new JLabel("🎓");
        lblIcon.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120));
        lblIcon.setForeground(CLR_BLACK); // Black icon as in image silhouette
        lblIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        // Add a black box behind it to simulate the silhouette style if needed, but
        // emoji is easiest.

        leftPanel.add(Box.createVerticalGlue());
        leftPanel.add(lblTitleLeft);
        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(lblDescLeft);
        leftPanel.add(Box.createVerticalStrut(30));
        leftPanel.add(lblIcon);
        leftPanel.add(Box.createVerticalGlue());

        // === RIGHT PANEL ===
        JPanel rightPanel = new JPanel(new GridBagLayout()); // Use GBL for centering form
        rightPanel.setBackground(CLR_WHITE);
        rightPanel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel formContainer = new JPanel();
        formContainer.setLayout(new BoxLayout(formContainer, BoxLayout.Y_AXIS));
        formContainer.setOpaque(false);
        formContainer.setPreferredSize(new Dimension(350, 450));

        JLabel lblTitleRight = new JLabel("MASUK AKUN");
        lblTitleRight.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitleRight.setForeground(CLR_BLACK);
        lblTitleRight.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblSubRight = new JLabel("Masuk dengan kredensial akun SiUjian Anda.");
        lblSubRight.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubRight.setForeground(Color.GRAY);
        lblSubRight.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Role Dropdown (Added as requested to keep functionality)
        JLabel lblRole = new JLabel("Pilih Peran");
        lblRole.setFont(FONT_BOLD_S);
        lblRole.setAlignmentX(Component.LEFT_ALIGNMENT);

        cmbRole = new JComboBox<>(new String[] { ROLE_DOSEN, ROLE_ADMIN, ROLE_MAHASISWA, ROLE_PENGAWAS });
        cmbRole.setFont(FONT_PLAIN);
        cmbRole.setBackground(CLR_WHITE);
        cmbRole.setBorder(BorderFactory.createLineBorder(CLR_BLACK, 2));
        cmbRole.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        cmbRole.setAlignmentX(Component.LEFT_ALIGNMENT);
        cmbRole.addActionListener(e -> updateLabels());

        // Input 1 Label (Dynamic)
        lblLabel1 = new JLabel("Nama Lengkap");
        lblLabel1.setFont(FONT_BOLD_S);
        lblLabel1.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtUsername = new JTextField();
        txtUsername.setFont(FONT_PLAIN);
        txtUsername.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(CLR_BLACK, 2),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        txtUsername.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        txtUsername.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Input 2 Label (Dynamic)
        JPanel pwdLabelPanel = new JPanel(new BorderLayout());
        pwdLabelPanel.setOpaque(false);
        pwdLabelPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 20));
        pwdLabelPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        lblLabel2 = new JLabel("NID");
        lblLabel2.setFont(FONT_BOLD_S);

        JLabel lblForgot = new JLabel("Lupa password?");
        lblForgot.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblForgot.setForeground(CLR_BLUE);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));

        pwdLabelPanel.add(lblLabel2, BorderLayout.WEST);
        // pwdLabelPanel.add(lblForgot, BorderLayout.EAST); // Removed forgot for
        // cleaner look as per "INPUT NYA" request implicit simplicity

        JPanel pwdContainer = new JPanel(new BorderLayout());
        pwdContainer.setBorder(BorderFactory.createLineBorder(CLR_BLACK, 2));
        pwdContainer.setBackground(CLR_WHITE);
        pwdContainer.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pwdContainer.setAlignmentX(Component.LEFT_ALIGNMENT);

        txtPassword = new JPasswordField();
        txtPassword.setBorder(new EmptyBorder(8, 10, 8, 0)); // No border himself
        txtPassword.setFont(FONT_PLAIN);

        JButton btnEye = new JButton("👁️");
        btnEye.setBorderPainted(false);
        btnEye.setContentAreaFilled(false);
        btnEye.setFocusPainted(false);
        btnEye.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnEye.addActionListener(e -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible)
                txtPassword.setEchoChar((char) 0);
            else
                txtPassword.setEchoChar('•');
        });

        pwdContainer.add(txtPassword, BorderLayout.CENTER);
        pwdContainer.add(btnEye, BorderLayout.EAST);

        // Remember
        JCheckBox chkRemember = new JCheckBox("Ingat saya");
        chkRemember.setFont(new Font("Segoe UI", Font.BOLD, 12));
        chkRemember.setOpaque(false);
        chkRemember.setIcon(createCheckBoxIcon(false));
        chkRemember.setSelectedIcon(createCheckBoxIcon(true));
        chkRemember.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Login Button
        btnLogin = new NeoButton("MASUK SEKARANG", CLR_BLUE, CLR_WHITE);
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 45));
        btnLogin.setAlignmentX(Component.LEFT_ALIGNMENT);
        btnLogin.addActionListener(e -> performLogin());

        // Add to Form Container
        formContainer.add(lblTitleRight);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(lblSubRight);
        formContainer.add(Box.createVerticalStrut(25));

        formContainer.add(lblRole);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(cmbRole);
        formContainer.add(Box.createVerticalStrut(15));

        formContainer.add(lblLabel1);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(txtUsername);
        formContainer.add(Box.createVerticalStrut(15));

        formContainer.add(pwdLabelPanel);
        formContainer.add(Box.createVerticalStrut(5));
        formContainer.add(pwdContainer);
        formContainer.add(Box.createVerticalStrut(10));

        formContainer.add(chkRemember);
        formContainer.add(Box.createVerticalStrut(20));

        formContainer.add(btnLogin);
        formContainer.add(Box.createVerticalStrut(20));

        rightPanel.add(formContainer);

        cardContent.add(leftPanel);
        cardContent.add(rightPanel);

        // Add Shadow Effect (Simple offset black panel behind)
        JPanel shadowPanel = new JPanel();
        shadowPanel.setBackground(CLR_BLACK);
        shadowPanel.setBounds(10, 10, 900, 550); // Offset by 10px

        mainCard.add(cardContent); // Top
        mainCard.add(shadowPanel); // Bottom
        mainCard.setComponentZOrder(cardContent, 0); // Front
        mainCard.setComponentZOrder(shadowPanel, 1); // Back

        add(mainCard);

        // Initial Logic
        updateLabels();
    }

    // --- Logic ---
    private void updateLabels() {
        String role = (String) cmbRole.getSelectedItem();
        txtUsername.setText("");
        txtPassword.setText("");

        if (ROLE_ADMIN.equals(role)) {
            lblLabel1.setText("USERNAME");
            lblLabel2.setText("PASSWORD");
            txtPassword.setEchoChar('•');
            isPasswordVisible = false;
        } else if (ROLE_MAHASISWA.equals(role)) {
            lblLabel1.setText("NAMA");
            lblLabel2.setText("NIM");
            txtPassword.setEchoChar((char) 0); // Visible by default for NIM
            isPasswordVisible = true;
        } else if (ROLE_DOSEN.equals(role)) {
            lblLabel1.setText("NAMA");
            lblLabel2.setText("NID");
            txtPassword.setEchoChar((char) 0); // Visible by default for NID
            isPasswordVisible = true;
        } else if (ROLE_PENGAWAS.equals(role)) {
            lblLabel1.setText("NAMA");
            lblLabel2.setText("NID");
            txtPassword.setEchoChar((char) 0);
            isPasswordVisible = true;
        }
    }

    private void performLogin() {
        String role = (String) cmbRole.getSelectedItem();
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword()).trim();

        if (u.isEmpty() || p.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Mohon lengkapi data login!", "Validasi", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            boolean success = false;
            // Map roles to repo calls
            if (ROLE_MAHASISWA.equals(role)) {
                String[] data = authRepository.getStudentDetails(u, p);
                if (data != null) {
                    success = true;
                    new id.ac.campus.antiexam.ui.StudentDashboardFrame().setVisible(true);
                }
            } else if (ROLE_DOSEN.equals(role)) {
                if (authRepository.loginLecturer(u, p)) {
                    success = true;
                    new LecturerDashboardFrame(u).setVisible(true);
                }
            } else if (ROLE_PENGAWAS.equals(role)) {
                if (authRepository.loginProctor(u, p)) {
                    success = true;
                    new ProctorDashboardFrame(u).setVisible(true);
                }
            } else if (ROLE_ADMIN.equals(role)) {
                if (authRepository.loginAdmin(u, p)) {
                    success = true;
                    new AdminScheduleFrame(u).setVisible(true);
                }
            }

            if (success)
                dispose();
            else
                JOptionPane.showMessageDialog(this, "Login Gagal! Cek kredensial Anda.", "Gagal",
                        JOptionPane.ERROR_MESSAGE);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
        }
    }

    // --- Custom Components ---

    // Neo-Brutalism Button
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
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF); // Sharp edges

            // Shadow
            if (!getModel().isPressed()) {
                g2.setColor(CLR_BLACK);
                g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);
            }

            // Main Body
            int offset = getModel().isPressed() ? 4 : 0;
            g2.setColor(bgColor);
            g2.fillRect(offset, offset, getWidth() - 4, getHeight() - 4);

            // Border
            g2.setColor(CLR_BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(offset, offset, getWidth() - 6, getHeight() - 6); // Adjust for stroke

            // Text
            g2.setColor(fgColor);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(getText())) / 2 + (offset / 2);
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent() + (offset / 2);
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    private Icon createCheckBoxIcon(boolean selected) {
        return new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.setColor(CLR_BLACK);
                g2.drawRect(x, y, 16, 16);
                if (selected) {
                    g2.fillRect(x + 4, y + 4, 9, 9);
                }
            }

            public int getIconWidth() {
                return 18;
            }

            public int getIconHeight() {
                return 18;
            }
        };
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
        }
        new RoleChooserFrame().setVisible(true);
    }
}
