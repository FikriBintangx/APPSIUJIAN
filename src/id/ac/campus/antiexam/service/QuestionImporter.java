package id.ac.campus.antiexam.service;

import id.ac.campus.antiexam.config.DBConnection;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

public class QuestionImporter {

    public void importFromFile(File file, int examId) throws Exception {
        String name = file.getName().toLowerCase();
        String text;
        if (name.endsWith(".pdf")) {
            text = readPdf(file);
        } else if (name.endsWith(".docx")) {
            throw new Exception("Format DOCX sementara dinonaktifkan (library missing). Gunakan PDF atau TXT.");
        } else if (name.endsWith(".txt")) {
            text = readTxt(file);
        } else {
            throw new Exception("Format file belum didukung. Gunakan PDF, DOCX, atau TXT.");
        }

        // 1. Try to parse as Question File (Questions + Options)
        List<QuestionBlock> blocks = parseBlocks(text);
        if (!blocks.isEmpty()) {
            saveToDatabase(examId, blocks);
            return;
        }

        // 2. If no full questions found, try to parse as Answer Key Only ("1. A", "2.
        // B")
        java.util.Map<Integer, String> answerMap = parseAnswerKey(text);
        if (!answerMap.isEmpty()) {
            updateAnswers(examId, answerMap);
            return;
        }

        throw new Exception("Gagal Import: Tidak ditemukan soal PG valid atau kunci jawaban format '1. A'.\n" +
                "Untuk Soal: Pastikan ada Soal & Pilihan A/B/C/D.\n" +
                "Untuk Kunci: Pastikan format '1. A' atau '1. A. Teks'.");
    }

    private java.util.Map<Integer, String> parseAnswerKey(String rawText) {
        java.util.Map<Integer, String> map = new java.util.HashMap<>();
        String[] lines = rawText.split("\\n+");
        for (String line : lines) {
            line = line.trim();
            // Relaxed regex:
            // Starts with number
            // Separator: dot, paren, space
            // Answer: A-D
            // Optional separator: dot, paren, space
            // Optional text
            if (line.matches("(?i)^\\d+.*[A-D].*")) {
                try {
                    // Extract number: first digits
                    java.util.regex.Matcher mNum = java.util.regex.Pattern.compile("^(\\d+)").matcher(line);
                    if (!mNum.find())
                        continue;
                    int num = Integer.parseInt(mNum.group(1));

                    // Extract answer: Valid A-D surrounded by boundary or at specific positions
                    // Look for [space/dot]A[space/dot] or just A at end
                    java.util.regex.Matcher mAns = java.util.regex.Pattern
                            .compile("(?i)(\\s|[\\.\\)]|^)([A-D])([\\.\\)\\s]|$)").matcher(line);
                    // We need to find the one that IS NOT part of the number question (e.g. "1. A")
                    // Usually the first A-D match after the number is the answer
                    String ans = null;
                    while (mAns.find()) {
                        // Ensure it's not the "1" if we somehow matched weirdly, but regex ensures ID
                        // is digits
                        ans = mAns.group(2).toUpperCase();
                        break; // Take first validity
                    }

                    if (ans != null) {
                        map.put(num, ans);
                    }
                } catch (Exception ignored) {
                }
            }
        }
        return map;
    }

    private void updateAnswers(int examId, java.util.Map<Integer, String> map) throws Exception {
        Connection conn = DBConnection.getConnection();
        // 1. Get existing question IDs ordered by ID (assuming insertion order =
        // question number order)
        // This relies on the fact that SQLite usually preserves insertion order for
        // rowid,/autoinc
        String sqlList = "SELECT id FROM exam_questions WHERE exam_id = ? ORDER BY id ASC";
        PreparedStatement psList = conn.prepareStatement(sqlList);
        psList.setInt(1, examId);
        java.sql.ResultSet rs = psList.executeQuery();

        List<Integer> qIds = new ArrayList<>();
        while (rs.next()) {
            qIds.add(rs.getInt("id"));
        }
        rs.close();
        psList.close();

        if (qIds.isEmpty()) {
            throw new Exception("Belum ada soal terupload untuk ujian ini. Upload file Soal dulu.");
        }

        String sqlUpd = "UPDATE exam_questions SET correct_answer = ? WHERE id = ?";
        PreparedStatement psUpd = conn.prepareStatement(sqlUpd);

        int updatedCount = 0;
        for (java.util.Map.Entry<Integer, String> entry : map.entrySet()) {
            int qNum = entry.getKey(); // 1-based index
            String ans = entry.getValue();

            if (qNum > 0 && qNum <= qIds.size()) {
                int dbId = qIds.get(qNum - 1);
                psUpd.setString(1, ans);
                psUpd.setInt(2, dbId);
                psUpd.addBatch();
                updatedCount++;
            }
        }

        psUpd.executeBatch();
        psUpd.close();

        if (updatedCount == 0) {
            throw new Exception("Nomor kunci jawaban tidak cocok dengan jumlah soal yang ada.");
        }
    }

    private String readPdf(File file) throws Exception {
        PDDocument doc = null;
        try {
            doc = Loader.loadPDF(file);
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(doc);
        } finally {
            if (doc != null) {
                doc.close();
            }
        }
    }

    private String readTxt(File file) throws Exception {
        byte[] bytes = java.nio.file.Files.readAllBytes(file.toPath());
        return new String(bytes, java.nio.charset.StandardCharsets.UTF_8);
    }

    private List<QuestionBlock> parseBlocks(String rawText) throws Exception {
        String text = rawText.replace("\r\n", "\n").replace("\r", "\n");
        String[] blockArr = text.split("\\n\\s*\\n+");
        List<QuestionBlock> blocks = new ArrayList<>();

        for (String b : blockArr) {
            String block = b.trim();
            if (block.isEmpty())
                continue;

            String[] lines = block.split("\\n+");
            if (lines.length == 0)
                continue;

            String first = lines[0].trim();
            // Remove typical numbering like "1.", "1)", "Soal 1", etc.
            first = first.replaceFirst("^\\d+[.)]\\s*", "").replaceFirst("^(?i)soal\\s*\\d+[.):]?\\s*", "").trim();

            StringBuilder questionText = new StringBuilder(first);
            String optA = null, optB = null, optC = null, optD = null;
            String answer = null;

            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty())
                    continue;

                // Handle Inline Options (e.g., "A. Java B. Python C. C++ D. Ruby")
                // We check this BEFORE single line checks
                if (line.matches("(?i)^[A][.)]\\s*.*[B][.)].*")) {
                    // Attempt to split by lookahead for option markers
                    // Regex: (?=\s[B-D][.)]\s) -> Look for space followed by B/C/D, dot/paren,
                    // space
                    String[] parts = line.split("(?=\\s[B-D][.)]\\s)");
                    for (String p : parts) {
                        p = p.trim();
                        if (p.matches("(?i)^[A][.)]\\s*.*"))
                            optA = p.replaceFirst("(?i)^[A][.)]\\s*", "").trim();
                        else if (p.matches("(?i)^[B][.)]\\s*.*"))
                            optB = p.replaceFirst("(?i)^[B][.)]\\s*", "").trim();
                        else if (p.matches("(?i)^[C][.)]\\s*.*"))
                            optC = p.replaceFirst("(?i)^[C][.)]\\s*", "").trim();
                        else if (p.matches("(?i)^[D][.)]\\s*.*"))
                            optD = p.replaceFirst("(?i)^[D][.)]\\s*", "").trim();
                    }
                    continue;
                }

                // Parse Options using single line checks
                if (line.matches("^(?i)[A][.)]\\s*.*")) {
                    optA = line.replaceFirst("^(?i)[A][.)]\\s*", "").trim();
                } else if (line.matches("^(?i)[B][.)]\\s*.*")) {
                    optB = line.replaceFirst("^(?i)[B][.)]\\s*", "").trim();
                } else if (line.matches("^(?i)[C][.)]\\s*.*")) {
                    optC = line.replaceFirst("^(?i)[C][.)]\\s*", "").trim();
                } else if (line.matches("^(?i)[D][.)]\\s*.*")) {
                    optD = line.replaceFirst("^(?i)[D][.)]\\s*", "").trim();
                } else if (line.matches("^(?i)(Answer|Jawaban|Kunci)\\s*[:=]\\s*[A-D].*")) {
                    // Parse Answer key like "Jawaban: A"
                    String[] parts = line.split("[:=]");
                    if (parts.length > 1) {
                        answer = parts[1].trim().toUpperCase().substring(0, 1);
                    }
                } else {
                    if (optA == null) {
                        questionText.append("\n").append(line);
                    }
                }
            }

            if (optA == null) {
                // FALLBACK: Check if options are embedded in the question text (Single Line
                // Case)
                // Pattern: "... A. optA B. optB C. optC D. optD"
                String fullText = questionText.toString();
                // We look for " A. " or " A) " followed by content, then " B. " ...
                // Using simplified splitting to be robust
                // Matches " A. " or " A) " with boundary checks
                if (fullText.matches("(?si).*\\s+[ABCD][\\.\\)].*")) {
                    try {
                        // Try to split by tokens that look like Option Markers
                        // We iterate backwards to find D, then C, then B, then A

                        int idxD = findLastOptionIndex(fullText, "D");
                        int idxC = findLastOptionIndex(fullText, "C");
                        int idxB = findLastOptionIndex(fullText, "B");
                        int idxA = findLastOptionIndex(fullText, "A");

                        if (idxA != -1 && idxB != -1 && idxC != -1 && idxD != -1 &&
                                idxA < idxB && idxB < idxC && idxC < idxD) {

                            optD = fullText.substring(idxD + 2).trim(); // +2 for "D." length approx
                            optC = fullText.substring(idxC + 2, idxD).trim();
                            optB = fullText.substring(idxB + 2, idxC).trim();
                            optA = fullText.substring(idxA + 2, idxB).trim();

                            // Update question text to exclude options
                            String cleanQ = fullText.substring(0, idxA).trim();
                            // Remove trailing " A" if regex matched weirdly, but usually substring(0, idxA)
                            // is safe
                            // idxA is the start of "A." or "A)", so we take 0..idxA
                            questionText = new StringBuilder(cleanQ);
                        }
                    } catch (Exception ignored) {
                    }
                }
            }

            if (optA != null && optB != null && optC != null && optD != null) {
                QuestionBlock qb = new QuestionBlock();
                qb.type = "PG";
                qb.questionText = questionText.toString().trim();
                qb.optA = optA;
                qb.optB = optB;
                qb.optC = optC;
                qb.optD = optD;
                qb.answer = answer; // Can be null
                blocks.add(qb);
            }
        }

        return blocks;
    }

    // Helper to find option marker like " A. " or " A) " or newline start "^A."
    private int findLastOptionIndex(String text, String letter) {
        // We look for regex matches manually-ish or check lastIndexOf
        // Regex: (\s|^)Letter[\.\)]\s
        // returns START index of the Letter
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("(\\s|^)" + letter + "[\\.\\)]\\s");
        java.util.regex.Matcher m = p.matcher(text);
        int lastIdx = -1;
        while (m.find()) {
            lastIdx = m.start();
            // m.start() points to the space before char, or char itself if start of line
            // If space, we want to point to Char.
            if (Character.isWhitespace(text.charAt(lastIdx))) {
                lastIdx++;
            }
        }
        return lastIdx;
    }

    private void saveToDatabase(int examId, List<QuestionBlock> blocks) throws Exception {
        Connection conn = DBConnection.getConnection();
        String delSql = "DELETE FROM exam_questions WHERE exam_id = ?";
        PreparedStatement psDel = conn.prepareStatement(delSql);
        psDel.setInt(1, examId);
        psDel.executeUpdate();
        psDel.close();

        String sql = "INSERT INTO exam_questions(exam_id, question_type, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement ps = conn.prepareStatement(sql);
        for (QuestionBlock qb : blocks) {
            ps.setInt(1, examId);
            ps.setString(2, qb.type);
            ps.setString(3, qb.questionText);
            ps.setString(4, qb.optA);
            ps.setString(5, qb.optB);
            ps.setString(6, qb.optC);
            ps.setString(7, qb.optD);
            ps.setString(8, qb.answer);
            ps.addBatch();
        }
        ps.executeBatch();
        ps.close();
    }

    private static class QuestionBlock {
        String type;
        String questionText;
        String optA;
        String optB;
        String optC;
        String optD;
        String answer;
    }
}
