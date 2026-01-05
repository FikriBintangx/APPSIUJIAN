package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SesiUjianData {

    public int createSessionAuto(int studentId, String studentName, String studentClass) throws Exception {
        try (Connection conn = KoneksiDatabase.getConnection()) {
            // Relaxed cek: Find ANY aktif exam to ensure monitoring works buat
            // demo/mismatched data
            String sqlUjian = "SELECT id FROM ujian WHERE status IN ('ONGOING', 'RUNNING') ORDER BY id DESC LIMIT 1";
            try (PreparedStatement psUjian = conn.prepareStatement(sqlUjian)) {

                try (ResultSet rsUjian = psUjian.executeQuery()) {
                    if (!rsUjian.next()) {
                        throw new Exception("Tidak ada ujian aktif untuk kelas: " + studentClass);
                    }
                    int examId = rsUjian.getInt("id");

                    String sqlCheck = "SELECT id FROM ujian_mahasiswa WHERE id_ujian = ? AND id_mahasiswa = ? LIMIT 1";
                    try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                        psCheck.setInt(1, examId);
                        psCheck.setInt(2, studentId);
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            if (rsCheck.next()) {
                                return rsCheck.getInt("id");
                            }
                        }
                    }

                    String sqlInsert = "INSERT INTO ujian_mahasiswa(id_ujian, id_mahasiswa, status, violation_count, waktu_mulai) VALUES(?,?,?,0,CURRENT_TIMESTAMP)";
                    try (PreparedStatement psIns = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                        psIns.setInt(1, examId);
                        psIns.setInt(2, studentId);
                        psIns.setString(3, "ONGOING");
                        psIns.executeUpdate();
                        try (ResultSet rsKey = psIns.getGeneratedKeys()) {
                            if (rsKey.next()) {
                                return rsKey.getInt(1);
                            } else {
                                throw new Exception("Gagal membuat sesi ujian");
                            }
                        }
                    }
                }
            }
        }
    }

    public int createSessionForUjian(int examId, int studentId) throws Exception {
        try (Connection conn = KoneksiDatabase.getConnection()) {
            // cek if exists
            String sqlCheck = "SELECT id FROM ujian_mahasiswa WHERE id_ujian = ? AND id_mahasiswa = ? LIMIT 1";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, examId);
                psCheck.setInt(2, studentId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        return rsCheck.getInt("id");
                    }
                }
            }

            // insert sesi baru
            String sqlInsert = "INSERT INTO ujian_mahasiswa(id_ujian, id_mahasiswa, status, violation_count, waktu_mulai) VALUES(?,?,?,0,CURRENT_TIMESTAMP)";
            try (PreparedStatement psIns = conn.prepareStatement(sqlInsert, Statement.RETURN_GENERATED_KEYS)) {
                psIns.setInt(1, examId);
                psIns.setInt(2, studentId);
                psIns.setString(3, "ONGOING");
                psIns.executeUpdate();
                try (ResultSet rsKey = psIns.getGeneratedKeys()) {
                    if (rsKey.next()) {
                        return rsKey.getInt(1);
                    } else {
                        throw new Exception("Gagal membuat sesi ujian");
                    }
                }
            }
        }
    }

    // === FITUR BUAT LIAT STATUS MAHASISWA SATU KELAS ===
    public List<Object[]> listSessionsSummary(int examIdFilter) throws Exception {
        List<Object[]> list = new ArrayList<>();
        // Select skor juga ya ngab, biar ketauan pinter apa hoki
        String sql = "SELECT se.id, se.id_ujian, s.name, se.status, se.waktu_mulai, se.violation_count, se.nilai FROM ujian_mahasiswa se JOIN mahasiswa s ON se.id_mahasiswa = s.id WHERE se.id_ujian = ? ORDER BY se.id DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examIdFilter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getInt("id_ujian"),
                            rs.getString("name"),
                            rs.getString("status"),
                            rs.getTimestamp("waktu_mulai"),
                            rs.getInt("violation_count"),
                            rs.getInt("nilai")
                    });
                }
            }
        }
        return list;
    }

    // === INI KHUSUS BUAT DOSEN BIAR BISA INTIP SEMUA HASIL UJIAN MEREKA ===
    public List<Object[]> listAllSessionsForLecturer(String lecturerUsername) throws Exception {
        List<Object[]> list = new ArrayList<>();
        // Join banyak tabel nih bos, ati-ati query berat
        String sql = "SELECT se.id, se.id_ujian, s.name, se.status, se.waktu_mulai, se.violation_count, se.nilai " +
                "FROM ujian_mahasiswa se " +
                "JOIN mahasiswa s ON se.id_mahasiswa = s.id " +
                "JOIN ujian e ON se.id_ujian = e.id " +
                "WHERE e.username_dosen = ? " +
                "ORDER BY se.id DESC";

        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, lecturerUsername);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // Masukin data ke list biar bisa dipake di tabel
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getInt("id_ujian"),
                            rs.getString("name"),
                            rs.getString("status"),
                            rs.getTimestamp("waktu_mulai"),
                            rs.getInt("violation_count"),
                            rs.getInt("nilai")
                    });
                }
            }
        }
        return list;
    }

    // === update STATUS SESI, LOCK/UNLOCK GTU DEH ===
    public void updateStatus(int sessionId, String status) throws Exception {
        // Kalo FINISHED, catet waktu selesainya jugak
        String sql = "UPDATE ujian_mahasiswa SET status = ?, waktu_selesai = CASE WHEN ? = 'FINISHED' AND waktu_selesai IS NULL THEN CURRENT_TIMESTAMP ELSE waktu_selesai END WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, sessionId);
            ps.executeUpdate(); // Gass eksekusi
        }
    }

    // === TOMBOL NUKLIR: AKHIRI SEMUA SESI SATU UJIAN ===
    public void updateStatusByUjian(int examId, String status) throws Exception {
        String sql = "UPDATE ujian_mahasiswa SET status = ?, waktu_selesai = CASE WHEN ? = 'FINISHED' AND waktu_selesai IS NULL THEN CURRENT_TIMESTAMP ELSE waktu_selesai END WHERE id_ujian = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, examId);
            ps.executeUpdate();
        }
    }

    public int incrementPelanggaran(int sessionId) throws Exception {
        int count = 0;
        String sqlUpd = "UPDATE ujian_mahasiswa SET violation_count = violation_count + 1 WHERE id = ?";
        String sqlSel = "SELECT violation_count FROM ujian_mahasiswa WHERE id = ?";

        try (Connection conn = KoneksiDatabase.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sqlUpd)) {
                ps.setInt(1, sessionId);
                ps.executeUpdate();
            }
            try (PreparedStatement ps = conn.prepareStatement(sqlSel)) {
                ps.setInt(1, sessionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        count = rs.getInt("violation_count");
                    }
                }
            }
            if (count >= 2) {
                updateStatus(sessionId, "LOCKED");
            }
        }
        return count;
    }

    public String getStatus(int sessionId) throws Exception {
        String sql = "SELECT status FROM ujian_mahasiswa WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("status");
                }
            }
        }
        return null;
    }

    public void deleteSession(int sessionId) throws Exception {
        String sqlAns = "DELETE FROM jawaban WHERE id_ujian_mahasiswa = ?";
        String sqlSess = "DELETE FROM ujian_mahasiswa WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection()) {
            conn.setAutoCommit(false);
            try {
                try (PreparedStatement ps = conn.prepareStatement(sqlAns)) {
                    ps.setInt(1, sessionId);
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = conn.prepareStatement(sqlSess)) {
                    ps.setInt(1, sessionId);
                    ps.executeUpdate();
                }
                conn.commit();
            } catch (Exception e) {
                conn.rollback();
                throw e;
            }
        }
    }

    public int logPelanggaran(int sessionId, String type) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // === AUTO-GRADING: HITUNG NILAI OTOMATIS SAAT MAHASISWA SELESAI ===
    /**
     * Fungsi untuk menghitung nilai otomatis berdasarkan jawaban mahasiswa
     * 
     * @param sessionId ID sesi ujian mahasiswa
     * @return Array berisi [nilai, jumlah_benar, jumlah_salah]
     * @throws Exception jika terjadi error
     */
    public int[] calculateAndSaveScore(int sessionId) throws Exception {
        try (Connection conn = KoneksiDatabase.getConnection()) {
            // 1. Ambil ID ujian dari sesi
            String sqlGetExam = "SELECT id_ujian FROM ujian_mahasiswa WHERE id = ?";
            int examId = 0;
            try (PreparedStatement ps = conn.prepareStatement(sqlGetExam)) {
                ps.setInt(1, sessionId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        examId = rs.getInt("id_ujian");
                    } else {
                        throw new Exception("Sesi ujian tidak ditemukan!");
                    }
                }
            }

            // 2. Ambil semua soal dan kunci jawaban
            String sqlSoal = "SELECT id, kunci_jawaban FROM soal_ujian WHERE id_ujian = ? ORDER BY id ASC";
            List<Object[]> soalList = new ArrayList<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlSoal)) {
                ps.setInt(1, examId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        soalList.add(new Object[] {
                                rs.getInt("id"),
                                rs.getString("kunci_jawaban")
                        });
                    }
                }
            }

            if (soalList.isEmpty()) {
                // Tidak ada soal, nilai 0
                updateScoreInDatabase(sessionId, 0, 0, 0);
                return new int[] { 0, 0, 0 };
            }

            // 3. Ambil jawaban mahasiswa
            String sqlJawaban = "SELECT nomor_soal, jawaban FROM jawaban WHERE id_ujian_mahasiswa = ?";
            java.util.Map<Integer, String> jawabanMap = new java.util.HashMap<>();
            try (PreparedStatement ps = conn.prepareStatement(sqlJawaban)) {
                ps.setInt(1, sessionId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        jawabanMap.put(rs.getInt("nomor_soal"), rs.getString("jawaban"));
                    }
                }
            }

            // 4. Hitung benar dan salah
            int correct = 0;
            int wrong = 0;
            int totalSoal = soalList.size();

            for (Object[] soalData : soalList) {
                int idSoal = (Integer) soalData[0]; // pake ID soal asli
                String kunciJawaban = (String) soalData[1];
                String jawabanMahasiswa = jawabanMap.get(idSoal); // ambil jawaban based on ID SOAL

                if (jawabanMahasiswa != null && jawabanMahasiswa.trim().equalsIgnoreCase(kunciJawaban)) {
                    correct++;
                } else {
                    wrong++;
                }
            }

            // 5. Hitung nilai (0-100)
            int nilai = (int) Math.round((double) correct / totalSoal * 100);

            // 6. Simpan ke database
            updateScoreInDatabase(sessionId, nilai, correct, wrong);

            return new int[] { nilai, correct, wrong };
        }
    }

    /**
     * Helper function untuk update score ke database
     */
    private void updateScoreInDatabase(int sessionId, int nilai, int correct, int wrong) throws Exception {
        String sql = "UPDATE ujian_mahasiswa SET nilai = ?, correct_answers = ?, wrong_answers = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nilai);
            ps.setInt(2, correct);
            ps.setInt(3, wrong);
            ps.setInt(4, sessionId);
            ps.executeUpdate();
        }
    }

    /**
     * Get score details untuk ditampilkan
     * 
     * @param sessionId ID sesi ujian
     * @return Array berisi [nilai, correct_answers, wrong_answers]
     */
    public int[] getScoreDetails(int sessionId) throws Exception {
        String sql = "SELECT nilai, correct_answers, wrong_answers FROM ujian_mahasiswa WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new int[] {
                            rs.getInt("nilai"),
                            rs.getInt("correct_answers"),
                            rs.getInt("wrong_answers")
                    };
                }
            }
        }
        return new int[] { 0, 0, 0 };
    }
}
