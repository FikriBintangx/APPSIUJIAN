package id.ac.campus.antiexam.repository;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthRepository {

    public String[] getStudentDetails(String name, String nim) throws Exception {
        String sql = "SELECT id, class_name FROM students WHERE name = ? AND student_number = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, nim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                            String.valueOf(rs.getInt("id")),
                            rs.getString("class_name")
                    };
                }
            }
        }
        return null;
    }

    public boolean loginLecturer(String username, String password) throws Exception {
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

    public boolean loginAdmin(String username, String password) throws Exception {
        String sql = "SELECT id FROM admins WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean loginProctor(String username, String password) throws Exception {
        // Assuming proctors table exists, otherwise might be lecturers with role
        String sql = "SELECT id FROM proctors WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}