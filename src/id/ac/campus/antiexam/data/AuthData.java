package id.ac.campus.antiexam.data;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class AuthData {

    public String[] getStudentDetails(String name, String nim) throws Exception {
        // Relaxed login: Only check NIM, ignore name/password for easier testing
        String sql = "SELECT id, nama_kelas FROM mahasiswa WHERE nim = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nim);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new String[] {
                            String.valueOf(rs.getInt("id")),
                            rs.getString("nama_kelas")
                    };
                }
            }
        }
        return null;
    }

    public boolean loginLecturer(String username, String password) throws Exception {
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

    public boolean loginAdmin(String username, String password) throws Exception {
        String sql = "SELECT id FROM admin WHERE username = ? AND password = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean loginProctor(String username, String password) throws Exception {
        // Now using 'pengawas' tabel
        String sql = "SELECT id FROM pengawas WHERE username = ? AND password = ?";
        try (Connection conn = KoneksiDatabase.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }
}
