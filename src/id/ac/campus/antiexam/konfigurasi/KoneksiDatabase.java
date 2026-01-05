package id.ac.campus.antiexam.konfigurasi;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class KoneksiDatabase {
    private static final String JDBC_URL = "jdbc:sqlite:examguard.db";

    private KoneksiDatabase() {
    }

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            throw new SQLException(
                    "SQLite JDBC Driver tidak ditemukan. Pastikan library sqlite-jdbc sudah ditambahkan.", ex);
        }
        org.sqlite.SQLiteConfig config = new org.sqlite.SQLiteConfig();
        config.setBusyTimeout(30000); // 30 seconds
        config.setJournalMode(org.sqlite.SQLiteConfig.JournalMode.WAL);
        config.setSynchronous(org.sqlite.SQLiteConfig.SynchronousMode.NORMAL);
        return DriverManager.getConnection(JDBC_URL, config.toProperties());
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
