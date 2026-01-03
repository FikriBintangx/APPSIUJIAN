package id.ac.campus.antiexam.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static final String JDBC_URL = "jdbc:sqlite:examguard.db";

    private DBConnection() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new SQLException(
                    "SQLite JDBC Driver tidak ditemukan. Pastikan library sqlite-jdbc sudah ditambahkan.", ex);
        }
        return DriverManager.getConnection(JDBC_URL);
    }

    public static void main(String[] args) {
        try (Connection conn = getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Berhasil konek ke database SQLite");
            }
        } catch (SQLException e) {
            System.err.println("Gagal konek: " + e.getMessage());
        }
    }
}
