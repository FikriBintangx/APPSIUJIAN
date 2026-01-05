package id.ac.campus.antiexam.ui.ux;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import id.ac.campus.antiexam.layanan.EksporPdfService;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ManualQuestionEditorDialog extends JDialog {

    // === INI AREA KHUSUS EDITOR, DILARANG PARKIR ===
    private final int examId;
    private DefaultTableModel questionModel;
    private JTable questionTable;

    // field buat isi soal, biar dosen ga pusing
    private JTextArea txtSoal;
    private JTextField txtOptA, txtOptB, txtOptC, txtOptD;

    // Pake Radio Button biar kayak Google Form beneran, ga ribet pilih dropdown
    private JRadioButton rbA, rbB, rbC, rbD;
    private ButtonGroup bgCorrect;

    // Palette Warna Gen Z Neo-Brutalism
    private final Color COL_PRIMARY = new Color(88, 101, 242); // Blurple
    private final Color COL_SUCCESS = new Color(34, 197, 94); // Green
    private final Color COL_DANGER = new Color(239, 68, 68); // Red
    private final Color COL_WARNING = new Color(245, 158, 11); // Orange
    private final Color COL_BG = new Color(255, 255, 255); // White bersih
    private final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 14);
    private final Font FONT_PLAIN = new Font("Segoe UI", Font.PLAIN, 14);

    public ManualQuestionEditorDialog(Frame parent, int examId) {
        super(parent, "Editor Soal Ultra Friendly (Google Form Style)", true);
        this.examId = examId;
        setSize(1280, 800);
        setLocationRelativeTo(parent);

        // setup UI biar estetik parah
        initComponents();
        loadQuestions(); // load data lama biar ga amnesia
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        // === PANEL KIRI: LIST SOAL ===
        // Biar keliatan list soalnya di kiri, kek navigasi gitu
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(new Color(248, 250, 252)); // Agak abu dikit
        leftPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 3, Color.BLACK)); // Garis tebel dikanan
        leftPanel.setPreferredSize(new Dimension(400, 0));

        // header Kiri
        JPanel leftHeader = new JPanel(new BorderLayout());
        leftHeader.setOpaque(false);
        leftHeader.setBorder(new EmptyBorder(20, 20, 0, 20));
        JLabel lblList = new JLabel("Bank Soal");
        lblList.setFont(new Font("Segoe UI", Font.BOLD, 22));
        leftHeader.add(lblList, BorderLayout.CENTER);

        // Tombol Tambah Soal (Paling Gede & Hijau)
        NeoButton btnAdd = new NeoButton("+ Buat Soal Baru", COL_SUCCESS, Color.WHITE);
        btnAdd.setPreferredSize(new Dimension(0, 50));
        btnAdd.setFont(new Font("Segoe UI", Font.BOLD, 16));
        btnAdd.addActionListener(e -> resetForm()); // reset form biar bersih

        JPanel btnAddWrapper = new JPanel(new BorderLayout());
        btnAddWrapper.setOpaque(false);
        btnAddWrapper.setBorder(new EmptyBorder(15, 20, 15, 20));
        btnAddWrapper.add(btnAdd, BorderLayout.CENTER);

        leftPanel.add(leftHeader, BorderLayout.NORTH);

        // tabel List Soal
        questionModel = new DefaultTableModel(new Object[] { "No", "Preview Soal", "Kunci" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        questionTable = new JTable(questionModel);
        styleTable(questionTable);

        // Klik tabel langsung load data ke kanan
        questionTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && questionTable.getSelectedRow() != -1) {
                loadSelectedSoal();
            }
        });

        JScrollPane scrollTable = new JScrollPane(questionTable);
        scrollTable.setBorder(BorderFactory.createEmptyBorder());
        scrollTable.getViewport().setBackground(Color.WHITE);

        // Wrapper tabel biar ada isinya
        JPanel tableWrapper = new JPanel(new BorderLayout());
        tableWrapper.add(btnAddWrapper, BorderLayout.NORTH);
        tableWrapper.add(scrollTable, BorderLayout.CENTER);
        leftPanel.add(tableWrapper, BorderLayout.CENTER);

        // footer Kiri: Tombol Delete & Export
        JPanel leftFooter = new JPanel(new GridLayout(2, 1, 5, 10));
        leftFooter.setOpaque(false);
        leftFooter.setBorder(new EmptyBorder(20, 20, 20, 20));

        NeoButton btnDelete = new NeoButton("Hapus Soal", COL_DANGER, Color.WHITE);
        btnDelete.addActionListener(e -> deleteSoal());

        NeoButton btnExport = new NeoButton("Export PDF", COL_PRIMARY, Color.WHITE);
        btnExport.addActionListener(e -> exportToPdf());

        leftFooter.add(btnDelete);
        leftFooter.add(btnExport);
        leftPanel.add(leftFooter, BorderLayout.SOUTH);

        // === PANEL KANAN: EDITOR FORM (GOOGLE FORM STYLE) ===
        // belakangground abu-abu biar form-nya 'pop-up' kek kartu
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(new Color(240, 244, 248));

        // Wrapper Kartu Putih
        NeoCard cardPanel = new NeoCard();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setPreferredSize(new Dimension(700, 650));

        // Strip Warna di Atas Kartu (Khas G-Form)
        JPanel topStrip = new JPanel();
        topStrip.setBackground(COL_PRIMARY);
        topStrip.setPreferredSize(new Dimension(0, 10));
        cardPanel.add(topStrip, BorderLayout.NORTH);

        // Konten Kartu
        JPanel cardContent = new JPanel(new BorderLayout());
        cardContent.setBackground(Color.WHITE);
        cardContent.setBorder(new EmptyBorder(30, 40, 30, 40));

        // header dalam Kartu
        JPanel cardHeader = new JPanel(new GridLayout(2, 1));
        cardHeader.setBackground(Color.WHITE);
        JLabel lblEdit = new JLabel("Editor Soal");
        lblEdit.setFont(new Font("Segoe UI", Font.BOLD, 24));
        JLabel lblDesc = new JLabel("Silakan edit pertanyaan dan kunci jawaban di bawah ini.");
        lblDesc.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblDesc.setForeground(Color.GRAY);

        cardHeader.add(lblEdit);
        cardHeader.add(lblDesc);
        cardContent.add(cardHeader, BorderLayout.NORTH);

        // form Area
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(20, 0, 5, 0);
        gbc.weightx = 1.0;
        gbc.gridx = 0;
        gbc.gridy = 0;

        // 1. Input Pertanyaan
        formPanel.add(createLabel("Pertanyaan"), gbc);
        gbc.gridy++;

        txtSoal = new JTextArea(3, 20);
        txtSoal.setLineWrap(true);
        txtSoal.setWrapStyleWord(true);
        txtSoal.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        txtSoal.setBackground(new Color(250, 250, 250));
        JScrollPane scrollQ = new JScrollPane(txtSoal);
        // border bawah doang biar kek material
        scrollQ.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(200, 200, 200)));
        formPanel.add(scrollQ, gbc);
        gbc.gridy++;

        // Spacer
        gbc.insets = new Insets(25, 0, 10, 0);
        formPanel.add(createLabel("Opsi Jawaban & Kunci"), gbc);
        gbc.gridy++;
        gbc.insets = new Insets(10, 0, 10, 0);

        // 2. Opsi Jawaban A, B, C, D
        bgCorrect = new ButtonGroup();

        JPanel pA = createOptionRow("A", rbA = new JRadioButton());
        formPanel.add(pA, gbc);
        gbc.gridy++;
        txtOptA = (JTextField) pA.getClientProperty("field");

        JPanel pB = createOptionRow("B", rbB = new JRadioButton());
        formPanel.add(pB, gbc);
        gbc.gridy++;
        txtOptB = (JTextField) pB.getClientProperty("field");

        JPanel pC = createOptionRow("C", rbC = new JRadioButton());
        formPanel.add(pC, gbc);
        gbc.gridy++;
        txtOptC = (JTextField) pC.getClientProperty("field");

        JPanel pD = createOptionRow("D", rbD = new JRadioButton());
        formPanel.add(pD, gbc);
        gbc.gridy++;
        txtOptD = (JTextField) pD.getClientProperty("field");

        // tambahin Radios
        bgCorrect.add(rbA);
        bgCorrect.add(rbB);
        bgCorrect.add(rbC);
        bgCorrect.add(rbD);
        rbA.setActionCommand("A");
        rbB.setActionCommand("B");
        rbC.setActionCommand("C");
        rbD.setActionCommand("D");
        rbA.setSelected(true);

        cardContent.add(formPanel, BorderLayout.CENTER);

        // footer: Action Buttons
        JPanel cardFooter = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        cardFooter.setBackground(Color.WHITE);
        cardFooter.setBorder(new EmptyBorder(20, 0, 0, 0));

        NeoButton btnCancel = new NeoButton("Reset", Color.WHITE, Color.GRAY);
        btnCancel.addActionListener(e -> resetForm());

        NeoButton btnSave = new NeoButton("Simpan Soal", COL_PRIMARY, Color.WHITE);
        btnSave.setPreferredSize(new Dimension(150, 40));
        btnSave.addActionListener(e -> saveSoal());

        cardFooter.add(btnCancel);
        cardFooter.add(Box.createHorizontalStrut(10));
        cardFooter.add(btnSave);

        cardContent.add(cardFooter, BorderLayout.SOUTH);
        cardPanel.add(cardContent, BorderLayout.CENTER);

        rightPanel.add(cardPanel); // tengahin panel kartu yang udah di-style

        // tambahin Panel Kanan & Kiri ke Dialog
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
    }

    // === HELPER BUAT BIKIN UI UI LUCU ===

    private JLabel createLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_BOLD);
        l.setForeground(Color.DARK_GRAY);
        return l;
    }

    private JPanel createOptionRow(String label, JRadioButton rb) {
        JPanel p = new JPanel(new BorderLayout(10, 0));
        p.setOpaque(false);

        // Radio Button buat pilih kunci
        rb.setOpaque(false);
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        rb.setToolTipText("Klik ini kalo jawaban '" + label + "' yang bener ngab");

        // label huruf (A/B/C/D)
        JLabel lbl = new JLabel(label + ".");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 16));
        lbl.setBorder(new EmptyBorder(0, 5, 0, 0));

        // teks Input: G-Form style (Underline border)
        JTextField txt = new JTextField();
        txt.setFont(FONT_PLAIN);
        txt.setBackground(new Color(250, 250, 250));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, Color.GRAY), // Underline
                new EmptyBorder(5, 5, 5, 5)));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        left.setOpaque(false);
        left.add(rb);
        left.add(lbl);

        p.add(left, BorderLayout.WEST);
        p.add(txt, BorderLayout.CENTER);

        // Simpen referensi field biar bisa diambil nanti
        p.putClientProperty("field", txt);
        return p;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(40); // Agak tinggi biar lega
        table.setShowGrid(false); // tampilan bersih
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(230, 230, 230));
        table.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(232, 240, 254)); // Light Blue G-Style
        table.setSelectionForeground(Color.BLACK);

        JTableHeader th = table.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(Color.WHITE);
        th.setForeground(Color.GRAY);
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)));

        // Kolom No & Kunci kecil aja
        table.getColumnModel().getColumn(0).setMaxWidth(40);
        table.getColumnModel().getColumn(2).setMaxWidth(60);
    }

    // === LOGIC DATABASE GAN (UPDATED INDO) ===

    private void loadQuestions() {
        questionModel.setRowCount(0);
        try (Connection conn = KoneksiDatabase.getConnection()) {
            String sql = "SELECT id, pertanyaan, kunci_jawaban FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ResultSet rs = ps.executeQuery();
            int no = 1;
            while (rs.next()) {
                String qText = rs.getString("pertanyaan");
                // Potong dikit biar ga kepanjangan di tabel
                if (qText != null && qText.length() > 35)
                    qText = qText.substring(0, 35) + "...";

                questionModel.addRow(new Object[] {
                        no++,
                        qText,
                        rs.getString("kunci_jawaban")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal load soal: " + e.getMessage());
        }
    }

    private void loadSelectedSoal() {
        int row = questionTable.getSelectedRow();
        if (row == -1)
            return;

        try (Connection conn = KoneksiDatabase.getConnection()) {
            // Kita pake LIMIT OFFSET buat ambil soal ke-sekian
            String sql = "SELECT * FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ps.setInt(2, row);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                txtSoal.setText(rs.getString("pertanyaan"));
                txtOptA.setText(rs.getString("option_a"));
                txtOptB.setText(rs.getString("option_b"));
                txtOptC.setText(rs.getString("option_c"));
                txtOptD.setText(rs.getString("option_d"));

                String correct = rs.getString("kunci_jawaban");
                if ("A".equals(correct))
                    rbA.setSelected(true);
                else if ("B".equals(correct))
                    rbB.setSelected(true);
                else if ("C".equals(correct))
                    rbC.setSelected(true);
                else if ("D".equals(correct))
                    rbD.setSelected(true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void resetForm() {
        questionTable.clearSelection();
        txtSoal.setText("");
        txtOptA.setText("");
        txtOptB.setText("");
        txtOptC.setText("");
        txtOptD.setText("");
        rbA.setSelected(true); // default A
        txtSoal.requestFocus();
    }

    private void saveSoal() {
        // VALIDASI BIAR GA KOSONG KEK HATI JOMBLO
        if (txtSoal.getText().trim().isEmpty() ||
                txtOptA.getText().trim().isEmpty() ||
                txtOptB.getText().trim().isEmpty() ||
                txtOptC.getText().trim().isEmpty() ||
                txtOptD.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(this, "Waduh kosong ngab! Isi semua field dlu ya");
            return;
        }

        String selectedAns = "A";
        if (rbB.isSelected())
            selectedAns = "B";
        if (rbC.isSelected())
            selectedAns = "C";
        if (rbD.isSelected())
            selectedAns = "D";

        // Cek update atau insert
        int row = questionTable.getSelectedRow();
        boolean isUpdate = (row != -1);

        try (Connection conn = KoneksiDatabase.getConnection()) {
            if (isUpdate) {
                // update ngab
                // Ambil ID dlu pake offset yg sama
                String sqlGetId = "SELECT id FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
                PreparedStatement psId = conn.prepareStatement(sqlGetId);
                psId.setInt(1, examId);
                psId.setInt(2, row);
                ResultSet rsId = psId.executeQuery();
                if (rsId.next()) {
                    int qId = rsId.getInt("id");
                    String sqlUpd = "UPDATE soal_ujian SET pertanyaan=?, option_a=?, option_b=?, option_c=?, option_d=?, kunci_jawaban=? WHERE id=?";
                    PreparedStatement psUpd = conn.prepareStatement(sqlUpd);
                    psUpd.setString(1, txtSoal.getText());
                    psUpd.setString(2, txtOptA.getText());
                    psUpd.setString(3, txtOptB.getText());
                    psUpd.setString(4, txtOptC.getText());
                    psUpd.setString(5, txtOptD.getText());
                    psUpd.setString(6, selectedAns);
                    psUpd.setInt(7, qId);
                    psUpd.executeUpdate();
                    JOptionPane.showMessageDialog(this, "Soal berhasil di-update! Mantap jiwa");
                }
            } else {
                // insert baru
                String sqlIns = "INSERT INTO soal_ujian(id_ujian, pertanyaan, option_a, option_b, option_c, option_d, kunci_jawaban) VALUES(?,?,?,?,?,?,?)";
                PreparedStatement psIns = conn.prepareStatement(sqlIns);
                psIns.setInt(1, examId);
                psIns.setString(2, txtSoal.getText());
                psIns.setString(3, txtOptA.getText());
                psIns.setString(4, txtOptB.getText());
                psIns.setString(5, txtOptC.getText());
                psIns.setString(6, txtOptD.getText());
                psIns.setString(7, selectedAns);
                psIns.executeUpdate();
                JOptionPane.showMessageDialog(this, "Soal new tersimpan! Lanjuttt");
            }

            // refresh
            resetForm();
            loadQuestions();

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Gagal save: " + e.getMessage());
        }
    }

    private void deleteSoal() {
        int row = questionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih soal yg mau dihapus dlu ngab!");
            return;
        }

        if (JOptionPane.YES_OPTION != JOptionPane.showConfirmDialog(this, "Yakin mau apus soal ini?", "Confirm Delete",
                JOptionPane.YES_NO_OPTION)) {
            return;
        }

        try (Connection conn = KoneksiDatabase.getConnection()) {
            // Ambil ID dlu pake offset
            String sqlGetId = "SELECT id FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
            PreparedStatement psId = conn.prepareStatement(sqlGetId);
            psId.setInt(1, examId);
            psId.setInt(2, row);
            ResultSet rsId = psId.executeQuery();
            if (rsId.next()) {
                int qId = rsId.getInt("id");
                String sqlDel = "DELETE FROM soal_ujian WHERE id=?";
                PreparedStatement psDel = conn.prepareStatement(sqlDel);
                psDel.setInt(1, qId);
                psDel.executeUpdate();
                JOptionPane.showMessageDialog(this, "Soal berpulang ke rahmatullah (dihapus) ðŸ¥€");
                resetForm();
                loadQuestions();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void exportToPdf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Soal_Ujian_" + examId + ".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                // Helper list
                List<id.ac.campus.antiexam.entitas.Soal> qList = new ArrayList<>();
                try (Connection conn = KoneksiDatabase.getConnection()) {
                    String sql = "SELECT * FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC";
                    PreparedStatement ps = conn.prepareStatement(sql);
                    ps.setInt(1, examId);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        id.ac.campus.antiexam.entitas.Soal q = new id.ac.campus.antiexam.entitas.Soal();
                        q.setQuestionText(rs.getString("pertanyaan"));
                        q.setOptionA(rs.getString("option_a"));
                        q.setOptionB(rs.getString("option_b"));
                        q.setOptionC(rs.getString("option_c"));
                        q.setOptionD(rs.getString("option_d"));
                        qList.add(q);
                    }
                }

                EksporPdfService.exportQuestionsToPdf(qList, "BANK SOAL - EXAM " + examId, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "Export PDF Sukses! Share ke mahasiswa gih ðŸ“„");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal export: " + e.getMessage());
            }
        }
    }

    // === NEO tombol class (Private Styling) ===
    private static class NeoButton extends JButton {
        private Color bg, fg;

        public NeoButton(String text, Color bg, Color fg) {
            super(text);
            this.bg = bg;
            this.fg = fg;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setCursor(new Cursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 14));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);

            // shadow
            g2.setColor(Color.BLACK);
            g2.fillRect(4, 4, getWidth() - 4, getHeight() - 4);

            // utama Rect
            if (getModel().isPressed()) {
                g2.translate(2, 2);
            }
            g2.setColor(bg);
            g2.fillRect(0, 0, getWidth() - 4, getHeight() - 4);
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(0, 0, getWidth() - 4, getHeight() - 4);

            // teks
            g2.setColor(fg);
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - 4 - fm.stringWidth(getText())) / 2;
            int y = (getHeight() - 4 - fm.getHeight()) / 2 + fm.getAscent();
            g2.drawString(getText(), x, y);

            g2.dispose();
        }
    }

    // === NEO CARD class (KARTU ELEGAN) ===
    private static class NeoCard extends JPanel {
        public NeoCard() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int shadowOffset = 5;

            // shadow
            g2.setColor(new Color(200, 200, 200));
            g2.fillRoundRect(shadowOffset, shadowOffset, getWidth() - shadowOffset, getHeight() - shadowOffset, 15, 15);

            // kartu utama
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, 15, 15);

            // border tipis
            g2.setColor(new Color(220, 220, 220));
            g2.drawRoundRect(0, 0, getWidth() - shadowOffset, getHeight() - shadowOffset, 15, 15);

            g2.dispose();
        }
    }
}
