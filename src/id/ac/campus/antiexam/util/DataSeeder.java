package id.ac.campus.antiexam.util;

import id.ac.campus.antiexam.konfigurasi.KoneksiDatabase;
import java.sql.Connection;
import java.sql.Statement;

public class DataSeeder {

    public static void main(String[] args) {
        seed();
    }

    public static void seed() {
        System.out.println("ðŸŒ± Starting Data Seeder...");
        try (Connection conn = KoneksiDatabase.getConnection()) {

            // 1. bikin tabel kalo belum ada
            Statement stmt = conn.createStatement();

            // tabel admin
            stmt.execute("CREATE TABLE IF NOT EXISTS admin (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)");

            // tabel dosen
            stmt.execute("CREATE TABLE IF NOT EXISTS dosen (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT)");

            // tabel pengawas
            stmt.execute("CREATE TABLE IF NOT EXISTS pengawas (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT)");

            // tabel mahasiswa
            stmt.execute("CREATE TABLE IF NOT EXISTS mahasiswa (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "nim TEXT UNIQUE, " +
                    "nama_kelas TEXT)");

            // tabel ujian (versi simple buat seeder)
            stmt.execute("CREATE TABLE IF NOT EXISTS ujian (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "token TEXT, code TEXT, target_kelas TEXT, title TEXT, kode_matkul TEXT, " +
                    "type TEXT, durasi_menit INTEGER, jadwal_waktu DATETIME, status TEXT)");

            // 2. masukin data testing

            // data admin
            insertOrSkip(conn, "INSERT INTO admin (username, password) VALUES ('admin', 'admin123')");

            // data dosen
            insertOrSkip(conn,
                    "INSERT INTO dosen (username, password, name) VALUES ('dosen', 'dosen123', 'Dr. Budi Santoso')");

            // data pengawas
            insertOrSkip(conn,
                    "INSERT INTO pengawas (username, password, name) VALUES ('pengawas', 'pengawas123', 'Siti Aminah, S.Kom')");

            // data mahasiswa
            insertOrSkip(conn,
                    "INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Mahasiswa Test', '12345678', 'TI-2024')");

            System.out.println("âœ… Data Seeder Completed Successfully!");
            System.out.println("   -------------------------------------------------");
            System.out.println("   ðŸ”‘ Admin    : admin / admin123");
            System.out.println("   ðŸ”‘ Dosen    : dosen / dosen123");
            System.out.println("   ðŸ”‘ Pengawas : pengawas / pengawas123");
            System.out.println("   ðŸ”‘ Student  : 'Mahasiswa Test' / NIM: 12345678");
            System.out.println("   -------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("âŒ Seeder Failed: " + e.getMessage());
        }
    }

    private static void insertOrSkip(Connection conn, String sql) {
        try {
            conn.createStatement().execute(sql);
        } catch (Exception e) {
            // abaikan error unique constraint (data udah ada)
            if (!e.getMessage().contains("UNIQUE constraint failed")) {
 // System.out.println(" info: " + e.getMessage());
            }
        }
    }
}
