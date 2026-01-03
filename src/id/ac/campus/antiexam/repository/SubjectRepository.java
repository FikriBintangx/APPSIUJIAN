package id.ac.campus.antiexam.repository;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SubjectRepository {

    public List<Object[]> listSubjects() throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, code, name, class_name, lecturer_username, proctor_username FROM subjects ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("name"),
                        rs.getString("class_name"),
                        rs.getString("lecturer_username"),
                        rs.getString("proctor_username") // Added
                });
            }
        }
        return list;
    }

    public void createSubject(String code, String name, String className, String lecturerUsername,
            String proctorUsername) throws Exception {
        String sql = "INSERT INTO subjects(code, name, class_name, lecturer_username, proctor_username, created_at) VALUES(?,?,?,?,?,CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
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
        String sql = "UPDATE subjects SET code = ?, name = ?, class_name = ?, lecturer_username = ?, proctor_username = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
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
        String sql = "DELETE FROM subjects WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}