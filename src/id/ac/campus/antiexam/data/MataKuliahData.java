package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class MataKuliahData {

    public List<Object[]> listSubjects() throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, kode_matkul, name, nama_kelas, username_dosen, username_pengawas FROM mata_kuliah ORDER BY id DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("kode_matkul"),
                        rs.getString("name"),
                        rs.getString("nama_kelas"),
                        rs.getString("username_dosen"),
                        rs.getString("username_pengawas")
                });
            }
        }
        return list;
    }

    public void createSubject(String code, String name, String className, String lecturerUsername,
            String proctorUsername) throws Exception {
        String sql = "INSERT INTO mata_kuliah(kode_matkul, name, nama_kelas, username_dosen, username_pengawas, created_at) VALUES(?,?,?,?,?,CURRENT_TIMESTAMP)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, className);
            ps.setString(4, lecturerUsername);
            ps.setString(5, proctorUsername);
            ps.executeUpdate();
        }
    }

    public void updateSubject(int id, String code, String name, String className, String lecturerUsername,
            String proctorUsername) throws Exception {
        String sql = "UPDATE mata_kuliah SET kode_matkul = ?, name = ?, nama_kelas = ?, username_dosen = ?, username_pengawas = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, className);
            ps.setString(4, lecturerUsername);
            ps.setString(5, proctorUsername);
            ps.setInt(6, id);
            ps.executeUpdate();
        }
    }

    public void deleteSubject(int id) throws Exception {
        String sql = "DELETE FROM mata_kuliah WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}
