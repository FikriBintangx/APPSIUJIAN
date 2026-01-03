package id.ac.campus.antiexam.repository;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StudentRepository {

    public void createStudent(String studentNumber, String name, String className) throws SQLException {
        String sql = "INSERT INTO students (student_number, name, class_name) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ps.setString(2, name);
            ps.setString(3, className);
            ps.executeUpdate();
        }
    }

    public void updateStudent(int id, String studentNumber, String name, String className) throws SQLException {
        String sql = "UPDATE students SET student_number = ?, name = ?, class_name = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, studentNumber);
            ps.setString(2, name);
            ps.setString(3, className);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listStudents() throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, student_number, name, class_name FROM students ORDER BY class_name ASC, name ASC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("student_number"),
                        rs.getString("name"),
                        rs.getString("class_name")
                });
            }
        }
        return list;
    }

    public Object[] getStudentById(int id) throws SQLException {
        String sql = "SELECT id, student_number, name, class_name FROM students WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Object[] {
                            rs.getInt("id"),
                            rs.getString("student_number"),
                            rs.getString("name"),
                            rs.getString("class_name")
                    };
                }
            }
        }
        return null;
    }
}
