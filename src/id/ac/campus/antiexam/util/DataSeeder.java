package id.ac.campus.antiexam.util;

import id.ac.campus.antiexam.config.DBConnection;
import java.sql.Connection;
import java.sql.Statement;

public class DataSeeder {

    public static void main(String[] args) {
        seed();
    }

    public static void seed() {
        System.out.println("🌱 Starting Data Seeder...");
        try (Connection conn = DBConnection.getConnection()) {

            // 1. Create Tables if not exist
            Statement stmt = conn.createStatement();

            // Admins
            stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT)");

            // Lecturers
            stmt.execute("CREATE TABLE IF NOT EXISTS lecturers (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT)");

            // Proctors
            stmt.execute("CREATE TABLE IF NOT EXISTS proctors (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "username TEXT UNIQUE, " +
                    "password TEXT, " +
                    "name TEXT)");

            // Students
            stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "name TEXT, " +
                    "student_number TEXT UNIQUE, " +
                    "class_name TEXT)");

            // Exams (Needed for student test) -- minimal
            stmt.execute("CREATE TABLE IF NOT EXISTS exams (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "token TEXT, code TEXT, class_name TEXT, title TEXT, course TEXT, " +
                    "type TEXT, duration_min INTEGER, start_time DATETIME, status TEXT)");

            // 2. Insert Test Data

            // Admin
            insertOrSkip(conn, "INSERT INTO admins (username, password) VALUES ('admin', 'admin123')");

            // Lecturer
            insertOrSkip(conn,
                    "INSERT INTO lecturers (username, password, name) VALUES ('dosen', 'dosen123', 'Dr. Budi Santoso')");

            // Proctor
            insertOrSkip(conn,
                    "INSERT INTO proctors (username, password, name) VALUES ('pengawas', 'pengawas123', 'Siti Aminah, S.Kom')");

            // Student
            insertOrSkip(conn,
                    "INSERT INTO students (name, student_number, class_name) VALUES ('Mahasiswa Test', '12345678', 'TI-2024')");

            System.out.println("✅ Data Seeder Completed Successfully!");
            System.out.println("   -------------------------------------------------");
            System.out.println("   🔑 Admin    : admin / admin123");
            System.out.println("   🔑 Dosen    : dosen / dosen123");
            System.out.println("   🔑 Pengawas : pengawas / pengawas123");
            System.out.println("   🔑 Student  : 'Mahasiswa Test' / NIM: 12345678");
            System.out.println("   -------------------------------------------------");

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Seeder Failed: " + e.getMessage());
        }
    }

    private static void insertOrSkip(Connection conn, String sql) {
        try {
            conn.createStatement().execute(sql);
        } catch (Exception e) {
            // Ignore unique constraint violations (data already exists)
            if (!e.getMessage().contains("UNIQUE constraint failed")) {
                // System.out.println(" Info: " + e.getMessage());
            }
        }
    }
}
