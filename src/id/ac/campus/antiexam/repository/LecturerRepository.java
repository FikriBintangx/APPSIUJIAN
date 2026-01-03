package id.ac.campus.antiexam.repository;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class LecturerRepository {

    public boolean checkLogin(String username, String password) throws Exception {
        String sql = "SELECT id FROM lecturers WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
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
        String sql = "SELECT id, username, name FROM lecturers ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[]{
                    rs.getInt("id"),
                    rs.getString("username"),
                    rs.getString("name")
                });
            }
        }
        return list;
    }

    public void createLecturer(String username, String password, String name) throws Exception {
        String sql = "INSERT INTO lecturers(username, password, name) VALUES(?,?,?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, name);
            ps.executeUpdate();
        }
    }

    public void updateLecturer(int id, String username, String name) throws Exception {
        String sql = "UPDATE lecturers SET username = ?, name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, name);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteLecturerByUsername(String username) throws Exception {
        String sql = "DELETE FROM lecturers WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.executeUpdate();
        }
    }
}