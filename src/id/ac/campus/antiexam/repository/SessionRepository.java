package id.ac.campus.antiexam.repository;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SessionRepository {

    public int createSessionAuto(int studentId, String studentName, String studentClass) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            // Relaxed check: Find ANY active exam to ensure monitoring works for
            // demo/mismatched data
            String sqlExam = "SELECT id FROM exams WHERE status IN ('ONGOING', 'RUNNING') ORDER BY id DESC LIMIT 1";
            try (PreparedStatement psExam = conn.prepareStatement(sqlExam)) {

                try (ResultSet rsExam = psExam.executeQuery()) {
                    if (!rsExam.next()) {
                        throw new Exception("Tidak ada ujian aktif untuk kelas: " + studentClass);
                    }
                    int examId = rsExam.getInt("id");

                    String sqlCheck = "SELECT id FROM student_exams WHERE exam_id = ? AND student_id = ? LIMIT 1";
                    try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                        psCheck.setInt(1, examId);
                        psCheck.setInt(2, studentId);
                        try (ResultSet rsCheck = psCheck.executeQuery()) {
                            if (rsCheck.next()) {
                                return rsCheck.getInt("id");
                            }
                        }
                    }

                    String sqlInsert = "INSERT INTO student_exams(exam_id, student_id, status, violation_count, started_at) VALUES(?,?,?,0,CURRENT_TIMESTAMP)";
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

    public int createSessionForExam(int examId, int studentId) throws Exception {
        try (Connection conn = DBConnection.getConnection()) {
            // Check if exists
            String sqlCheck = "SELECT id FROM student_exams WHERE exam_id = ? AND student_id = ? LIMIT 1";
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, examId);
                psCheck.setInt(2, studentId);
                try (ResultSet rsCheck = psCheck.executeQuery()) {
                    if (rsCheck.next()) {
                        return rsCheck.getInt("id");
                    }
                }
            }

            // Insert new
            String sqlInsert = "INSERT INTO student_exams(exam_id, student_id, status, violation_count, started_at) VALUES(?,?,?,0,CURRENT_TIMESTAMP)";
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

    public List<Object[]> listSessionsSummary(int examIdFilter) throws Exception {
        List<Object[]> list = new ArrayList<>();
        // Added score to SELECT query
        String sql = "SELECT se.id, se.exam_id, s.name, se.status, se.started_at, se.violation_count, se.score FROM student_exams se JOIN students s ON se.student_id = s.id WHERE se.exam_id = ? ORDER BY se.id DESC";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examIdFilter);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[] {
                            rs.getInt("id"),
                            rs.getInt("exam_id"),
                            rs.getString("name"),
                            rs.getString("status"),
                            rs.getTimestamp("started_at"),
                            rs.getInt("violation_count"),
                            rs.getInt("score")
                    });
                }
            }
        }
        return list;
    }

    public void updateStatus(int sessionId, String status) throws Exception {
        String sql = "UPDATE student_exams SET status = ?, finished_at = CASE WHEN ? = 'FINISHED' AND finished_at IS NULL THEN CURRENT_TIMESTAMP ELSE finished_at END WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, sessionId);
            ps.executeUpdate();
        }
    }

    public void updateStatusByExam(int examId, String status) throws Exception {
        String sql = "UPDATE student_exams SET status = ?, finished_at = CASE WHEN ? = 'FINISHED' AND finished_at IS NULL THEN CURRENT_TIMESTAMP ELSE finished_at END WHERE exam_id = ?";
        try (Connection conn = DBConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, status);
            ps.setInt(3, examId);
            ps.executeUpdate();
        }
    }

    public int incrementViolation(int sessionId) throws Exception {
        int count = 0;
        String sqlUpd = "UPDATE student_exams SET violation_count = violation_count + 1 WHERE id = ?";
        String sqlSel = "SELECT violation_count FROM student_exams WHERE id = ?";

        try (Connection conn = DBConnection.getConnection()) {
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
        String sql = "SELECT status FROM student_exams WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
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
        String sqlAns = "DELETE FROM answers WHERE student_exam_id = ?";
        String sqlSess = "DELETE FROM student_exams WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
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

    public int logViolation(int sessionId, String type) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from
                                                                       // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}