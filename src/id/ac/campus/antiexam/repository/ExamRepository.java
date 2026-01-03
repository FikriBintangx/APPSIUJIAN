package id.ac.campus.antiexam.repository;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ExamRepository {

    public void startExam(int examId) throws Exception {
        String sql = "UPDATE exams SET status = 'ONGOING' WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.executeUpdate();
        }
    }

    public void createExam(String code, String targetClass, String title, String subjectCode, String type,
            String scheduledDateTime, int year, int duration, String lecturerUsername, String proctorUsername,
            String room) throws Exception {
        String sql = "INSERT INTO exams (code, target_class, title, subject_code, type, scheduled_at, academic_year, duration_min, lecturer_username, proctor_username, room, question_file_path, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 'SCHEDULED', CURRENT_TIMESTAMP)";
        try (Connection conn = DBConnection.getConnection();
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
            ps.setString(12, ""); // question_file_path (empty initially)
            ps.executeUpdate();
        }
    }

    public void updateExam(int id, String code, String targetClass, String title, String subjectCode, String type,
            String scheduledDateTime, int year, int duration, String lecturerUsername, String proctorUsername,
            String room) throws Exception {
        String sql = "UPDATE exams SET code = ?, target_class = ?, title = ?, subject_code = ?, type = ?, scheduled_at = ?, academic_year = ?, duration_min = ?, lecturer_username = ?, proctor_username = ?, room = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
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
        String sql = "UPDATE exams SET type = ?, exam_mode = ?, duration_min = ?, subject_code = ?, question_file_path = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
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
        String sql = "UPDATE exams SET type = ?, exam_mode = ?, duration_min = ?, subject_code = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, type);
            ps.setString(2, examMode);
            ps.setInt(3, duration);
            ps.setString(4, subjectCode);
            ps.setInt(5, id);
            ps.executeUpdate();
        }
    }

    public void deleteExam(int id) throws Exception {
        String sql = "DELETE FROM exams WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<Object[]> listAllExamsForAdmin() throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, code, target_class, title, subject_code, type, academic_year, duration_min, lecturer_username, proctor_username, room, status, scheduled_at FROM exams ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Object[] {
                        rs.getInt("id"),
                        rs.getString("code"),
                        rs.getString("target_class"),
                        rs.getString("title"),
                        rs.getString("subject_code"),
                        rs.getString("type"),
                        rs.getInt("academic_year"),
                        rs.getInt("duration_min"),
                        rs.getString("lecturer_username"),
                        rs.getString("proctor_username"),
                        rs.getString("room"),
                        rs.getString("status"),
                        rs.getTimestamp("scheduled_at")
                });
            }
        }
        return list;
    }

    public List<Object[]> listExamsForLecturer(String username) throws Exception {
        List<Object[]> list = new ArrayList<>();
        // Now selecting exam_mode as well (index 8)
        String sql = "SELECT id, code, target_class, title, subject_code, type, duration_min, status, exam_mode FROM exams WHERE lecturer_username = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("target_class"),
                            rs.getString("title"),
                            rs.getString("subject_code"),
                            rs.getString("type"),
                            rs.getInt("duration_min"),
                            rs.getString("status"),
                            rs.getString("exam_mode") // Added
                    });
                }
            }
        }
        return list;
    }

    public String getExamFilePath(int examId) throws Exception {
        String sql = "SELECT question_file_path FROM exams WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next())
                    return rs.getString("question_file_path");
            }
        }
        return null;
    }

    public List<Object[]> listExamsForProctor(String username) throws Exception {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT id, code, target_class, title, subject_code, type, duration_min, status FROM exams WHERE proctor_username = ? ORDER BY id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getString("code"),
                            rs.getString("target_class"),
                            rs.getString("title"),
                            rs.getString("subject_code"),
                            rs.getString("type"),
                            rs.getInt("duration_min"),
                            rs.getString("status")
                    });
                }
            }
        }
        return list;
    }
}