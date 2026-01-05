package id.ac.campus.antiexam.ui.ux;

import id.ac.campus.antiexam.data.AuthData;
import javax.swing.BorderFactory;
import id.ac.campus.antiexam.ui.ux.mahasiswa.BerandaMahasiswaFrame;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Image;
import javax.swing.ImageIcon;

public class LoginFrame extends JFrame {

    private final AuthData authRepository = new AuthData();

    private JTextField txtName;
    private JTextField txtNim;
    private JLabel lblError;

    private final Color COL_PRIMARY = new Color(37, 99, 235);
    private final Color COL_BG_LEFT = new Color(239, 246, 255);
    private final Color COL_TEXT_DARK = new Color(30, 41, 59);
    private final String LOGO_PATH = "C:\\Users\\fikri\\Documents\\NetBeansProjects\\Appujian\\assets\\logo.png";

    public LoginFrame() {
        setTitle("SiUjian - Login Mahasiswa");
        setSize(1000, 650);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(new Color(241, 245, 249));

        JPanel card = new RoundedPanel(24, Color.WHITE);
        card.setLayout(new GridBagLayout());
 card.setPreferredSize(new Dimension(1100, 650)); // 900x550 â†’ 1100x650 BIGGER!

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(COL_BG_LEFT);

        JPanel contentLeft = new JPanel(new GridBagLayout());
        contentLeft.setOpaque(false);

        JLabel lblLogo = new JLabel();
        try {
            ImageIcon icon = new ImageIcon(LOGO_PATH);
 Image img = icon.getImage().getScaledInstance(180, 180, Image.SCALE_SMOOTH); // 120 â†’ 180px GEDEIN!
            lblLogo.setIcon(new ImageIcon(img));
            lblLogo.setHorizontalAlignment(SwingConstants.CENTER);
        } catch (Exception e) {
            lblLogo.setText("ðŸ‘¨â€ðŸŽ“");
 lblLogo.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 120)); // 72 â†’ 120px
        }

        JLabel lblHero = new JLabel("SiUjian");
 lblHero.setFont(new Font("Segoe UI", Font.BOLD, 42)); // 32 â†’ 42px
        lblHero.setForeground(COL_PRIMARY);

        JLabel lblDesc = new JLabel(
                "<html><center><font size='6'><b>Platform Ujian Aman</b></font><br><font size='4'>Fokus kerjakan, jujur hasilkan</font></center></html>",
                SwingConstants.CENTER);
 lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 â†’ 16px
        lblDesc.setForeground(new Color(71, 85, 105));

        GridBagConstraints gbcL = new GridBagConstraints();
        gbcL.gridx = 0;
        gbcL.gridy = 0;
 gbcL.insets = new Insets(0, 0, 30, 0); // 20 â†’ 30px
        contentLeft.add(lblLogo, gbcL);
        gbcL.gridy = 1;
 gbcL.insets = new Insets(15, 0, 15, 0); // 10 â†’ 15px
        contentLeft.add(lblHero, gbcL);
        gbcL.gridy = 2;
        contentLeft.add(lblDesc, gbcL);

        leftPanel.add(contentLeft, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(60, 70, 60, 70)); // 50,60 â†’ 60,70

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        JLabel lblTitle = new JLabel("Masuk Ujian");
 lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 36)); // 28 â†’ 36px BIGGER!
        lblTitle.setForeground(COL_TEXT_DARK);

        JLabel lblSub = new JLabel("Masukkan identitas valid Anda untuk memulai ujian");
 lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 16)); // 14 â†’ 16px
        lblSub.setForeground(Color.GRAY);

        gbc.gridy = 0;
        rightPanel.add(lblTitle, gbc);
        gbc.gridy = 1;
 gbc.insets = new Insets(8, 0, 45, 0); // 5,0,40 â†’ 8,0,45
        rightPanel.add(lblSub, gbc);

        gbc.gridy = 2;
 gbc.insets = new Insets(0, 0, 10, 0); // 8 â†’ 10px
        JLabel lblNameTitle = new JLabel("Nama Lengkap");
 lblNameTitle.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 â†’ 15px
        lblNameTitle.setForeground(new Color(55, 65, 81));
        rightPanel.add(lblNameTitle, gbc);

        gbc.gridy = 3;
 gbc.insets = new Insets(0, 0, 28, 0); // 25 â†’ 28px
        txtName = createStyledField();
        txtName.setToolTipText("Masukkan nama lengkap sesuai kartu mahasiswa");
 txtName.setPreferredSize(new Dimension(0, 55)); // 45 â†’ 55px TALLER!
        rightPanel.add(txtName, gbc);

        gbc.gridy = 4;
 gbc.insets = new Insets(0, 0, 10, 0); // 8 â†’ 10px
        JLabel lblNimTitle = new JLabel("Nomor Induk Mahasiswa (NIM)");
 lblNimTitle.setFont(new Font("Segoe UI", Font.BOLD, 15)); // 13 â†’ 15px
        lblNimTitle.setForeground(new Color(55, 65, 81));
        rightPanel.add(lblNimTitle, gbc);

        gbc.gridy = 5;
 gbc.insets = new Insets(0, 0, 35, 0); // 30 â†’ 35px
        txtNim = createStyledField();
        txtNim.setToolTipText("Masukkan NIM tanpa spasi");
 txtNim.setPreferredSize(new Dimension(0, 55)); // 45 â†’ 55px TALLER!
        rightPanel.add(txtNim, gbc);

        lblError = new JLabel(" ");
        lblError.setForeground(new Color(239, 68, 68));
 lblError.setFont(new Font("Segoe UI", Font.PLAIN, 14)); // 12 â†’ 14px
        gbc.gridy = 6;
 gbc.insets = new Insets(0, 0, 25, 0); // 20 â†’ 25px
        rightPanel.add(lblError, gbc);

        JButton btnLogin = createPrimaryButton("Mulai Sesi Ujian");
        btnLogin.addActionListener(e -> performLogin());
        btnLogin.setPreferredSize(new Dimension(0, 60)); // 50 â†’ 60px BIGGER tombol!
        btnLogin.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Make font bigger too
        gbc.gridy = 7;
        rightPanel.add(btnLogin, gbc);

        JButton btnBack = createTextButton("â† Kembali ke Menu Utama");
        btnBack.addActionListener(e -> {
            new PilihPeranFrame().setVisible(true);
            dispose();
        });
        gbc.gridy = 8;
        gbc.insets = new Insets(20, 0, 0, 0);
        rightPanel.add(btnBack, gbc);

        JLabel lblFooter = new JLabel("Â© 2025 SiUjian - Sistem Ujian Digital", SwingConstants.CENTER);
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
        String name = txtName.getText().trim();
        String nim = txtNim.getText().trim();

        if (name.isEmpty() || nim.isEmpty()) {
            lblError.setText("âš ï¸ Nama dan NIM wajib diisi.");
            return;
        }

        try {
            String[] studentData = authRepository.getStudentDetails(name, nim);

            if (studentData != null) {
                new BerandaMahasiswaFrame().setVisible(true);
                this.dispose();
            } else {
                lblError.setText("âš ï¸ Data tidak ditemukan. Periksa kembali Nama dan NIM.");
            }
        } catch (Exception ex) {
            lblError.setText("âš ï¸ Terjadi kesalahan sistem.");
            ex.printStackTrace();
        }
    }

    private JTextField createStyledField() {
        JTextField field = new JTextField();
        field.setPreferredSize(new Dimension(0, 48));
        field.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(209, 213, 219)),
                new EmptyBorder(12, 15, 12, 15)));
        field.setBackground(new Color(235, 235, 235));
        return field;
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
                    g2.setColor(new Color(29, 78, 216));
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
