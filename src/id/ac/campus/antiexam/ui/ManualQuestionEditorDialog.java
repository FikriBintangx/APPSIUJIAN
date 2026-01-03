package id.ac.campus.antiexam.ui;

import id.ac.campus.antiexam.config.DBConnection;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ManualQuestionEditorDialog extends JDialog {

    private final int examId;
    private DefaultTableModel questionModel;
    private JTable questionTable;
    private JTextArea txtQuestion;
    private JTextField txtOptA, txtOptB, txtOptC, txtOptD;
    private JComboBox<String> cmbCorrect;

    private final Color PRIMARY = new Color(37, 99, 235);
    private final Color SUCCESS = new Color(16, 185, 129);
    private final Color DANGER = new Color(239, 68, 68);
    private final Color BG_LIGHT = new Color(249, 250, 251);

    public ManualQuestionEditorDialog(Frame parent, int examId) {
        super(parent, "✍️ Editor Soal Manual (Google Form Style)", true);
        this.examId = examId;
        setSize(1200, 750);
        setLocationRelativeTo(parent);
        initComponents();
        loadQuestions();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        getContentPane().setBackground(BG_LIGHT);

        // === LEFT PANEL: Question List ===
        JPanel leftPanel = new JPanel(new BorderLayout(0, 15));
        leftPanel.setBackground(Color.WHITE);
        leftPanel.setBorder(new EmptyBorder(20, 20, 20, 15));
        leftPanel.setPreferredSize(new Dimension(450, 0));

        JLabel lblListTitle = new JLabel("📋 Daftar Soal Tersimpan:");
        lblListTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblListTitle.setForeground(PRIMARY);

        // Add "New Question" button at top
        JButton btnNew = createBtn("➕ Tambah Soal Baru", true, SUCCESS);
        btnNew.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btnNew.setPreferredSize(new Dimension(0, 50)); // Taller for emphasis
        btnNew.addActionListener(e -> {
            clearForm();
            txtQuestion.requestFocus();
            JOptionPane.showMessageDialog(this,
                    "<html><div style='text-align:center; padding:10px;'>" +
                            "<h2 style='color:#10B981;'>✍️ Mode Tambah Soal</h2>" +
                            "<p style='font-size:13px;'>Silakan isi form di panel kanan,<br>lalu klik tombol <b>\"💾 Simpan Soal\"</b> di bawah.</p>"
                            +
                            "</div></html>",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        JPanel topPanel = new JPanel(new BorderLayout(0, 10));
        topPanel.setBackground(Color.WHITE);
        topPanel.add(lblListTitle, BorderLayout.NORTH);
        topPanel.add(btnNew, BorderLayout.SOUTH);

        questionModel = new DefaultTableModel(new Object[] { "No", "Soal", "Kunci" }, 0) {
            public boolean isCellEditable(int row, int col) {
                return false;
            }
        };
        questionTable = new JTable(questionModel);
        questionTable.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        questionTable.setRowHeight(35);
        questionTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        questionTable.getTableHeader().setBackground(new Color(239, 246, 255));
        questionTable.getTableHeader().setForeground(PRIMARY);
        questionTable.setSelectionBackground(new Color(191, 219, 254));
        questionTable.setGridColor(new Color(229, 231, 235));

        JButton btnLoad = createBtn("📝 Edit Soal Terpilih", false, PRIMARY);
        btnLoad.addActionListener(e -> loadSelectedQuestion());

        JButton btnDelete = createBtn("🗑️ Hapus Soal Terpilih", false, DANGER);
        btnDelete.addActionListener(e -> deleteQuestion());

        JPanel btnPanel = new JPanel(new GridLayout(3, 1, 0, 10)); // 3 rows
        btnPanel.setBackground(Color.WHITE);
        btnPanel.add(btnLoad);
        btnPanel.add(btnDelete);

        JButton btnExport = createBtn("📄 Ekspor Soal ke PDF", false, new Color(59, 130, 246));
        btnExport.addActionListener(e -> exportToPdf());
        btnPanel.add(btnExport);

        leftPanel.add(topPanel, BorderLayout.NORTH);
        leftPanel.add(new JScrollPane(questionTable), BorderLayout.CENTER);
        leftPanel.add(btnPanel, BorderLayout.SOUTH);

        // === RIGHT PANEL: Editor Form ===
        JPanel rightPanel = new JPanel(new BorderLayout(0, 15));
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(20, 15, 20, 20));

        JLabel lblFormTitle = new JLabel("✍ Editor Soal:");
        lblFormTitle.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblFormTitle.setForeground(PRIMARY);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;

        txtQuestion = new JTextArea(4, 40);
        txtQuestion.setLineWrap(true);
        txtQuestion.setWrapStyleWord(true);
        txtQuestion.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtQuestion.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(10, 10, 10, 10)));
        txtQuestion.setBackground(BG_LIGHT);

        txtOptA = createTextField();
        txtOptB = createTextField();
        txtOptC = createTextField();
        txtOptD = createTextField();

        cmbCorrect = new JComboBox<>(new String[] { "A", "B", "C", "D" });
        cmbCorrect.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cmbCorrect.setBackground(new Color(219, 234, 254));
        ((JLabel) cmbCorrect.getRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        addFormRow(formPanel, gbc, "Pertanyaan:", new JScrollPane(txtQuestion));
        addFormRow(formPanel, gbc, "Pilihan A:", txtOptA);
        addFormRow(formPanel, gbc, "Pilihan B:", txtOptB);
        addFormRow(formPanel, gbc, "Pilihan C:", txtOptC);
        addFormRow(formPanel, gbc, "Pilihan D:", txtOptD);
        addFormRow(formPanel, gbc, "Kunci Jawaban:", cmbCorrect);

        // Visual separator
        gbc.gridy++;
        gbc.insets = new Insets(20, 0, 15, 0);
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(229, 231, 235));
        formPanel.add(separator, gbc);
        gbc.insets = new Insets(10, 0, 10, 0); // Reset

        // Action Buttons - More prominent
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 15, 0));
        actionPanel.setBackground(Color.WHITE);

        JButton btnReset = createBtn("🔄 Reset Form", false, new Color(156, 163, 175));
        btnReset.addActionListener(e -> clearForm());

        JButton btnSave = createBtn("💾 SIMPAN SOAL BARU", true, SUCCESS);
        btnSave.setFont(new Font("Segoe UI", Font.BOLD, 16)); // Larger font
        btnSave.setPreferredSize(new Dimension(0, 55)); // Taller
        btnSave.addActionListener(e -> saveQuestion());

        actionPanel.add(btnReset);
        actionPanel.add(btnSave);

        rightPanel.add(lblFormTitle, BorderLayout.NORTH);
        rightPanel.add(formPanel, BorderLayout.CENTER);
        rightPanel.add(actionPanel, BorderLayout.SOUTH);

        // === SPLIT PANE ===
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(450);
        splitPane.setDividerSize(2);
        splitPane.setBorder(null);

        add(splitPane, BorderLayout.CENTER);
    }

    private JTextField createTextField() {
        JTextField txt = new JTextField();
        txt.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txt.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
                new EmptyBorder(8, 12, 8, 12)));
        txt.setBackground(BG_LIGHT);
        return txt;
    }

    private JButton createBtn(String text, boolean bold, Color color) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", bold ? Font.BOLD : Font.PLAIN, 14));
        btn.setBackground(color);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(0, 45));
        return btn;
    }

    private void addFormRow(JPanel p, GridBagConstraints gbc, String label, JComponent comp) {
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lbl.setForeground(new Color(51, 65, 85));
        gbc.gridy++;
        p.add(lbl, gbc);
        gbc.gridy++;
        p.add(comp, gbc);
    }

    private void loadQuestions() {
        questionModel.setRowCount(0);
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT id, question_text, correct_answer FROM exam_questions WHERE exam_id = ? ORDER BY id ASC";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, examId);
                try (ResultSet rs = ps.executeQuery()) {
                    int no = 1;
                    while (rs.next()) {
                        String preview = rs.getString("question_text");
                        if (preview.length() > 60)
                            preview = preview.substring(0, 60) + "...";
                        questionModel.addRow(new Object[] { no++, preview, rs.getString("correct_answer") });
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error load soal: " + e.getMessage());
        }
    }

    private void loadSelectedQuestion() {
        int row = questionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih soal terlebih dahulu!");
            return;
        }

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM exam_questions WHERE exam_id = ? ORDER BY id ASC LIMIT 1 OFFSET ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, examId);
                ps.setInt(2, row);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        txtQuestion.setText(rs.getString("question_text"));
                        txtOptA.setText(rs.getString("option_a"));
                        txtOptB.setText(rs.getString("option_b"));
                        txtOptC.setText(rs.getString("option_c"));
                        txtOptD.setText(rs.getString("option_d"));
                        cmbCorrect.setSelectedItem(rs.getString("correct_answer"));
                    }
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Gagal load: " + e.getMessage());
        }
    }

    private void clearForm() {
        txtQuestion.setText("");
        txtOptA.setText("");
        txtOptB.setText("");
        txtOptC.setText("");
        txtOptD.setText("");
        cmbCorrect.setSelectedIndex(0);
    }

    private void saveQuestion() {
        String q = txtQuestion.getText().trim();
        String a = txtOptA.getText().trim();
        String b = txtOptB.getText().trim();
        String c = txtOptC.getText().trim();
        String d = txtOptD.getText().trim();
        String correct = (String) cmbCorrect.getSelectedItem();

        if (q.isEmpty() || a.isEmpty() || b.isEmpty() || c.isEmpty() || d.isEmpty()) {
            JOptionPane.showMessageDialog(this, "⚠️ Semua field harus diisi!");
            return;
        }

        // Retry logic for database locked error
        int maxRetries = 3;
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try (Connection conn = DBConnection.getConnection()) {
                conn.setAutoCommit(false); // Start transaction

                String sql = "INSERT INTO exam_questions(exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES(?,?,?,?,?,?,?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, examId);
                    ps.setString(2, q);
                    ps.setString(3, a);
                    ps.setString(4, b);
                    ps.setString(5, c);
                    ps.setString(6, d);
                    ps.setString(7, correct);
                    ps.executeUpdate();
                }

                conn.commit(); // Commit transaction
                JOptionPane.showMessageDialog(this, "✅ Soal berhasil disimpan!");
                clearForm();
                loadQuestions();
                return; // Success, exit retry loop

            } catch (Exception e) {
                if (e.getMessage().contains("locked") && attempt < maxRetries) {
                    // Database locked, wait and retry
                    try {
                        Thread.sleep(300); // Wait 300ms before retry
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    // Final attempt failed or different error
                    JOptionPane.showMessageDialog(this,
                            "<html><b>❌ Gagal menyimpan soal!</b><br><br>" +
                                    "Error: " + e.getMessage() + "<br><br>" +
                                    "<i>Tips: Tutup dialog lain yang mungkin masih terbuka.</i></html>",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    return;
                }
            }
        }
    }

    private void deleteQuestion() {
        int row = questionTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Pilih soal yang akan dihapus!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus soal ini?", "Konfirmasi",
                JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        try (Connection conn = DBConnection.getConnection()) {
            String sql = "DELETE FROM exam_questions WHERE exam_id = ? AND id = (SELECT id FROM exam_questions WHERE exam_id = ? ORDER BY id LIMIT 1 OFFSET ?)";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, examId);
            ps.setInt(2, examId);
            ps.setInt(3, row);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "✅ Soal dihapus!");
            clearForm();
            loadQuestions();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "❌ Gagal: " + e.getMessage());
        }
    }

    private void exportToPdf() {
        JFileChooser fc = new JFileChooser();
        fc.setSelectedFile(new java.io.File("Soal_Ujian_" + examId + ".pdf"));
        if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            java.util.List<id.ac.campus.antiexam.model.Question> list = new java.util.ArrayList<>();
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "SELECT * FROM exam_questions WHERE exam_id = ? ORDER BY id ASC";
                PreparedStatement ps = conn.prepareStatement(sql);
                ps.setInt(1, examId);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    id.ac.campus.antiexam.model.Question q = new id.ac.campus.antiexam.model.Question();
                    q.setQuestionText(rs.getString("question_text"));
                    q.setOptionA(rs.getString("option_a"));
                    q.setOptionB(rs.getString("option_b"));
                    q.setOptionC(rs.getString("option_c"));
                    q.setOptionD(rs.getString("option_d"));
                    list.add(q);
                }
                id.ac.campus.antiexam.service.PdfExportService.exportQuestionsToPdf(list,
                        "BANK SOAL - UJIAN ID " + examId, fc.getSelectedFile());
                JOptionPane.showMessageDialog(this, "✅ Soal berhasil diekspor ke PDF!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Gagal ekspor: " + e.getMessage());
            }
        }
    }
}
