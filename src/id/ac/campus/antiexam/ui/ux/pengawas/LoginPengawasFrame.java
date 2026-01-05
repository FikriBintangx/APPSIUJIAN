package id.ac.campus.antiexam.ui.ux.pengawas;

import id.ac.campus.antiexam.data.AuthData;
import id.ac.campus.antiexam.ui.icon.IconEye;
import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.Image;

public class LoginPengawasFrame extends JFrame {

    private final AuthData authRepository = new AuthData();
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblError;

    private final Color COL_PRIMARY = new Color(124, 58, 237); // Purple buat Proctor
    private final Color COL_BG_LEFT = new Color(245, 243, 255);
    private final String LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    public LoginPengawasFrame() {
        setTitle("SiUjian - Login Pengawas");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(241, 245, 249));

        JPanel card = new RoundedPanel(24, Color.WHITE);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(900, 550));

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COL_BG_LEFT);

        JPanel contentLeft = new JPanel(new GridBagLayout());
        contentLeft.setOpaque(false);

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            lblLogo.setIcon(new IconEye(100, COL_PRIMARY));
            lblLogo.setText("");
        }

        JLabel lblHero = new JLabel("SiUjian");
        lblHero.setFont(new Font("Segoe UI", Font.BOLD, 32));
        lblHero.setForeground(COL_PRIMARY);

        JLabel lblDesc = new JLabel(
                "<html><center><font size='4'>Panel Pengawas Ujian<br><font size='3'>Monitoring & Kontrol Sesi</font></center></html>",
                SwingConstants.CENTER);
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(new Color(88, 28, 135));

        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0;
        gbcL.gridy = 0;
        gbcL.insets = new Insets(0, 0, 20, 0);
        contentLeft.add(lblLogo, gbcL);
        gbcL.gridy = 1;
        gbcL.insets = new Insets(10, 0, 10, 0);
        contentLeft.add(lblHero, gbcL);
        gbcL.gridy = 2;
        contentLeft.add(lblDesc, gbcL);

        leftPanel.add(contentLeft, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(50, 60, 50, 60));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel("Login Pengawas");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblTitle.setForeground(new Color(30, 41, 59));

        gbc.gridy = 0;
        rightPanel.add(lblTitle, gbc);
        gbc.gridy = 1;
        gbc.insets = new Insets(5, 0, 40, 0);
        JLabel lblSub = new JLabel("Masuk untuk memonitor jalannya ujian");
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(Color.GRAY);
        rightPanel.add(lblSub, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel lblUserTitle = new JLabel("Username Pengawas");
        lblUserTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblUserTitle.setForeground(new Color(55, 65, 81));
        rightPanel.add(lblUserTitle, gbc);

        gbc.gridy = 3;
        gbc.insets = new Insets(0, 0, 25, 0);
        txtUsername = createStyledField();
        rightPanel.add(txtUsername, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(0, 0, 8, 0);
        JLabel lblPassTitle = new JLabel("Password");
        lblPassTitle.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblPassTitle.setForeground(new Color(55, 65, 81));
        rightPanel.add(lblPassTitle, gbc);

        gbc.gridy = 5;
        gbc.insets = new Insets(0, 0, 30, 0);
        txtPassword = new JPasswordField();
        styleField(txtPassword);
        rightPanel.add(txtPassword, gbc);

        lblError = new JLabel(" ");
        lblError.setForeground(new Color(239, 68, 68));
        lblError.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        gbc.gridy = 6;
        gbc.insets = new Insets(0, 0, 20, 0);
        rightPanel.add(lblError, gbc);

        JButton btnLogin = createPrimaryButton("Masuk Dashboard Pengawas");
        btnLogin.addActionListener(e -> performLogin());
        gbc.gridy = 7;
        rightPanel.add(btnLogin, gbc);

        JButton btnBack = createTextButton("<< Kembali ke Menu Utama");
        btnBack.addActionListener(e -> {
            new PilihPeranFrame().setVisible(true);
            dispose();
        });
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 0, 0);
        rightPanel.add(btnBack, gbc);

        JLabel lblFooter = new JLabel("(c) 2025 SiUjian - Sistem Ujian Digital", SwingConstants.CENTER);
        lblFooter.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblFooter.setForeground(new Color(156, 163, 175));
        gbc.gridy = 9;
        gbc.insets = new Insets(30, 0, 0, 0);
        rightPanel.add(lblFooter, gbc);

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.weighty = 1.0;
        c.gridx = 0;
        c.weightx = 0.45;
        card.add(leftPanel, c);
        c.gridx = 1;
        c.weightx = 0.55;
        card.add(rightPanel, c);

        add(card);
        getRootPane().setDefaultButton(btnLogin);
    }

    private void performLogin() {
        String u = txtUsername.getText().trim();
        String p = new String(txtPassword.getPassword());

        if (u.isEmpty() || p.isEmpty()) {
            lblError.setText("[!] Lengkapi username dan password.");
            return;
        }

        try {
            if (authRepository.loginProctor(u, p)) {
                new BerandaPengawasFrame(u).setVisible(true);
                dispose();
            } else {
                lblError.setText("[!] Username atau password salah.");
            }
        } catch (Exception ex) {
            lblError.setText("[!] Error: " + ex.getMessage());
        }
    }

    private JTextField createStyledField() {
        JTextField f = new JTextField();
        styleField(f);
        return f;
    }

    private void styleField(JTextField f) {
        f.setPreferredSize(new Dimension(0, 48));
        f.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(203, 213, 225)),
                new EmptyBorder(12, 15, 12, 15)));
        f.setBackground(new Color(235, 235, 235));
    }

    private JButton createPrimaryButton(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isPressed()) {
                    g2.setColor(COL_PRIMARY.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(new Color(109, 40, 217));
                } else {
                    g2.setColor(COL_PRIMARY);
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setPreferredSize(new Dimension(0, 50));
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JButton createTextButton(String text) {
        JButton btn = new JButton(text);
        btn.setForeground(new Color(107, 114, 128));
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        btn.setBorderPainted(false);
        btn.setContentAreaFilled(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setFocusPainted(false);
        return btn;
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
            g2.setColor(new Color(0, 0, 0, 5));
            g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(bgColor);
            g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, radius, radius);
            g2.setColor(new Color(229, 231, 235));
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, radius, radius);
            g2.dispose();
        }
    }
}
