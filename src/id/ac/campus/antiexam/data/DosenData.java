package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DosenData {

    public boolean checkLogin(String username, String password) throws Exception {
        String sql = "SELECT id FROM dosen WHERE username = ? AND password = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public List<Object[]> listLecturers() throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, username, name FROM dosen ORDER BY id DESC";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("name")
                });
            }
        }
        return list;
    }

    public void createLecturer(String username, String password, String name) throws Exception {
        String sql = "INSERT INTO dosen(username, password, name) VALUES(?,?,?)";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.executeUpdate();
        }
    }

    public void updateLecturer(int id, String username, String name) throws Exception {
        String sql = "UPDATE dosen SET username = ?, name = ? WHERE id = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteLecturerByUsername(String username) throws Exception {
        String sql = "DELETE FROM dosen WHERE username = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }
}
