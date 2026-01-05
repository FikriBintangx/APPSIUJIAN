package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class UjianData {

    public void startUjian(int examId, String token) throws Exception {
        String sql = "UPDATE ujian SET status = 'ONGOING', token = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setInt(2, examId);
            ps.executeUpdate();
        }
    }

    public void stopUjian(int examId) throws Exception {
        String sql = "UPDATE ujian SET status = 'FINISHED' WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.executeUpdate();
        }
    }

    public void createUjian(String code, String targetClass, String title, String subjectCode, String type,
            String scheduledDateTime, int year, int duration, String lecturerUsername, String proctorUsername,
            String room) throws Exception {
        String sql = "INSERT INTO ujian (code, target_kelas, title, kode_matkul, type, jadwal_waktu, tahun_akademik, durasi_menit, username_dosen, username_pengawas, ruangan, path_file_soal, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SCHEDULED', CURRENT_TIMESTAMP)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, targetClass);
            ps.setString(3, title);
            ps.setString(4, subjectCode);
            ps.setString(5, type);
            ps.setString(6, scheduledDateTime);
            ps.setInt(7, year);
            ps.setInt(8, duration);
            ps.setString(9, lecturerUsername);
            ps.setString(10, proctorUsername);
            ps.setString(11, room);
            ps.setString(12, ""); // path_file_soal
            ps.executeUpdate();
        }
    }

    public void updateUjian(int id, String code, String targetClass, String title, String subjectCode, String type,
            String scheduledDateTime, int year, int duration, String lecturerUsername, String proctorUsername,
            String room) throws Exception {
        String sql = "UPDATE ujian SET code = ?, target_kelas = ?, title = ?, kode_matkul = ?, type = ?, jadwal_waktu = ?, tahun_akademik = ?, durasi_menit = ?, username_dosen = ?, username_pengawas = ?, ruangan = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, targetClass);
            ps.setString(3, title);
            ps.setString(4, subjectCode);
            ps.setString(5, type);
            ps.setString(6, scheduledDateTime);
            ps.setInt(7, year);
            ps.setInt(8, duration);
            ps.setString(9, lecturerUsername);
            ps.setString(10, proctorUsername);
            ps.setString(11, room);
            ps.setInt(12, id);
            ps.executeUpdate();
        }
    }

    public void updateExamWithFile(int id, String type, String examMode, int duration, String subjectCode,
            String filePath)
            throws Exception {
        String sql = "UPDATE ujian SET type = ?, exam_mode = ?, durasi_menit = ?, kode_matkul = ?, path_file_soal = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, examMode);
            ps.setInt(3, duration);
            ps.setString(4, subjectCode);
            ps.setString(5, filePath);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    public void updateExamTypeAndDuration(int id, String type, String examMode, int duration, String subjectCode)
            throws Exception {
        String sql = "UPDATE ujian SET type = ?, exam_mode = ?, durasi_menit = ?, kode_matkul = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, examMode);
            ps.setInt(3, duration);
            ps.setString(4, subjectCode);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void deleteUjian(int id) throws Exception {
        String sql = "DELETE FROM ujian WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listAllExamsForAdmin() throws Exception {
        List<Object[]> list = new ArrayList<>();
        // Ambil kode_matkul DAN nama_matkul
        String sql = "SELECT u.id, u.code, u.target_kelas, u.title, u.kode_matkul, mk.name as nama_matkul, u.type, u.tahun_akademik, u.durasi_menit, u.username_dosen, u.username_pengawas, u.ruangan, u.status, u.jadwal_waktu FROM ujian u LEFT JOIN mata_kuliah mk ON u.kode_matkul = mk.kode_matkul ORDER BY u.id DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("target_kelas"),
                        rs.getString("title"),
                        rs.getString("kode_matkul"), // Index 4: Kode (penting buat Edit form)
                        rs.getString("type"),
                        rs.getInt("tahun_akademik"),
                        rs.getInt("durasi_menit"),
                        rs.getString("username_dosen"),
                        rs.getString("username_pengawas"),
                        rs.getString("ruangan"),
                        rs.getString("status"),
                        rs.getString("jadwal_waktu"),
                        rs.getString("nama_matkul") // Index 13: Nama Matkul (Display)
                });
            }
        }
        return list;
    }

    public List<Object[]> listExamsForLecturer(String username) throws Exception {
        List<Object[]> list = new ArrayList<>();
        // sekarang selecting exam_mode as well (index 8)
        String sql = "SELECT id, code, target_kelas, title, kode_matkul, type, durasi_menit, status, exam_mode FROM ujian WHERE username_dosen = ? ORDER BY id DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("target_kelas"),
                            rs.getString("title"),
                            rs.getString("kode_matkul"),
                            rs.getString("type"),
                            rs.getInt("durasi_menit"),
                            rs.getString("status"),
                            rs.getString("exam_mode")
                    });
                }
            }
        }
        return list;
    }

    public String getExamFilePath(int examId) throws Exception {
        String sql = "SELECT path_file_soal FROM ujian WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("path_file_soal");
            }
        }
        return null;
    }

    public List<Object[]> listExamsForProctor(String username) throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, code, target_kelas, title, kode_matkul, type, durasi_menit, status FROM ujian WHERE username_pengawas = ? ORDER BY id DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("target_kelas"),
                            rs.getString("title"),
                            rs.getString("kode_matkul"),
                            rs.getString("type"),
                            rs.getInt("durasi_menit"),
                            rs.getString("status")
                    });
                }
            }
        }
        return list;
    }
}
