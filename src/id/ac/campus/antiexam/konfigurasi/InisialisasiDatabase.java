package id.ac.campus.antiexam.konfigurasi;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InisialisasiDatabase {

        public static void initialize() {
                try (Connection conn = KoneksiDatabase.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // DROP TABLES FOR RESET (CLEAN SLATE)
                        stmt.execute("DROP TABLE IF EXISTS jawaban");
                        stmt.execute("DROP TABLE IF EXISTS soal_ujian");
                        stmt.execute("DROP TABLE IF EXISTS ujian_mahasiswa");
                        stmt.execute("DROP TABLE IF EXISTS ujian");
                        stmt.execute("DROP TABLE IF EXISTS ruangan");
                        stmt.execute("DROP TABLE IF EXISTS mata_kuliah");
                        stmt.execute("DROP TABLE IF EXISTS mahasiswa");
                        stmt.execute("DROP TABLE IF EXISTS pengawas");
                        stmt.execute("DROP TABLE IF EXISTS dosen");
                        stmt.execute("DROP TABLE IF EXISTS admin");

                        // tabel admin
                        stmt.execute("CREATE TABLE IF NOT EXISTS admin (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "username TEXT NOT NULL UNIQUE, " +
                                        "password TEXT NOT NULL)");

                        // tabel dosen (Lecturers)
                        stmt.execute("CREATE TABLE IF NOT EXISTS dosen (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "username TEXT NOT NULL UNIQUE, " +
                                        "password TEXT NOT NULL, " +
                                        "name TEXT NOT NULL)");

                        // tabel pengawas (Proctors)
                        stmt.execute("CREATE TABLE IF NOT EXISTS pengawas (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "username TEXT NOT NULL UNIQUE, " +
                                        "password TEXT NOT NULL, " +
                                        "name TEXT NOT NULL)");

                        // tabel mahasiswa (Students)
                        stmt.execute("CREATE TABLE IF NOT EXISTS mahasiswa (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT NOT NULL, " +
                                        "nim TEXT NOT NULL UNIQUE, " +
                                        "nama_kelas TEXT NOT NULL)");

                        // Mata Kuliah (Subjects)
                        stmt.execute("CREATE TABLE IF NOT EXISTS mata_kuliah (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "kode_matkul TEXT NOT NULL, " +
                                        "name TEXT NOT NULL, " +
                                        "nama_kelas TEXT NOT NULL, " +
                                        "username_dosen TEXT NOT NULL, " +
                                        "username_pengawas TEXT, " +
                                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                        // Ruangan (Rooms)
                        stmt.execute("CREATE TABLE IF NOT EXISTS ruangan (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT NOT NULL UNIQUE)");

                        // Ujian (Exams)
                        stmt.execute("CREATE TABLE IF NOT EXISTS ujian (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "code TEXT NOT NULL, " +
                                        "target_kelas TEXT NOT NULL, " +
                                        "title TEXT NOT NULL, " +
                                        "kode_matkul TEXT NOT NULL, " +
                                        "type TEXT NOT NULL, " +
                                        "exam_mode TEXT DEFAULT 'PG', " +
                                        "jadwal_waktu TIMESTAMP, " +
                                        "tahun_akademik INTEGER, " +
                                        "durasi_menit INTEGER, " +
                                        "username_dosen TEXT NOT NULL, " +
                                        "username_pengawas TEXT, " +
                                        "ruangan TEXT, " +
                                        "path_file_soal TEXT, " +
                                        "token TEXT, " +
                                        "broadcast_message TEXT, " +
                                        "status TEXT DEFAULT 'SCHEDULED', " +
                                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                        // Ujian Mahasiswa (Student Exams)
                        stmt.execute("CREATE TABLE IF NOT EXISTS ujian_mahasiswa (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "id_ujian INTEGER NOT NULL, " +
                                        "id_mahasiswa INTEGER NOT NULL, " +
                                        "status TEXT NOT NULL, " +
                                        "violation_count INTEGER DEFAULT 0, " +
                                        "nilai INTEGER DEFAULT 0, " +
                                        "correct_answers INTEGER DEFAULT 0, " +
                                        "wrong_answers INTEGER DEFAULT 0, " +
                                        "waktu_mulai TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "waktu_selesai TIMESTAMP, " +
                                        "FOREIGN KEY(id_ujian) REFERENCES ujian(id), " +
                                        "FOREIGN KEY(id_mahasiswa) REFERENCES mahasiswa(id))");

                        // Soal Ujian (Ujian Questions)
                        stmt.execute("CREATE TABLE IF NOT EXISTS soal_ujian (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "id_ujian INTEGER NOT NULL, " +
                                        "type TEXT, " +
                                        "pertanyaan TEXT, " +
                                        "option_a TEXT, " +
                                        "option_b TEXT, " +
                                        "option_c TEXT, " +
                                        "option_d TEXT, " +
                                        "kunci_jawaban TEXT, " +
                                        "FOREIGN KEY(id_ujian) REFERENCES ujian(id))");

                        // Jawaban (Answers)
                        stmt.execute("CREATE TABLE IF NOT EXISTS jawaban (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "id_ujian_mahasiswa INTEGER NOT NULL, " +
                                        "nomor_soal INTEGER, " +
                                        "jawaban TEXT, " +
                                        "FOREIGN KEY(id_ujian_mahasiswa) REFERENCES ujian_mahasiswa(id))");

                        // Seed Dummy Data
                        seedDummyData(stmt);

                        System.out.println("Database reset and filled with requested data.");

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        private static void seedDummyData(Statement stmt) throws SQLException {
                // 1. Admin
                stmt.execute("INSERT INTO admin (username, password) VALUES ('ADMIN1', 'admin123')");
                stmt.execute("INSERT INTO admin (username, password) VALUES ('ADMIN909', 'admin123')");

                // 2. Dosen (NIDN 11009010 - 11009012)
                stmt.execute("INSERT INTO dosen (username, password, name) VALUES ('11009010', '123456', 'Arif Nurochman')");
                stmt.execute("INSERT INTO dosen (username, password, name) VALUES ('11009011', '123456', 'Jalal')");
                stmt.execute("INSERT INTO dosen (username, password, name) VALUES ('11009012', '123456', 'Ramadhan')");

                // 3. Pengawas
                stmt.execute("INSERT INTO pengawas (username, password, name) VALUES ('Petugas1', '123456', 'Petugas Ujian')");

                // 4. Mahasiswa (NIM 1124140140 - 1124140144)
                stmt.execute("INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Fikri Bintang', '1124140140', 'IF-21-A')");
                stmt.execute("INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Ahmad ilyas', '1124140141', 'IF-21-A')");
                stmt.execute("INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Anom rizki', '1124140142', 'IF-21-A')");
                stmt.execute("INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Budi lope', '1124140143', 'IF-21-A')");
                stmt.execute("INSERT INTO mahasiswa (name, nim, nama_kelas) VALUES ('Deny dermawan', '1124140144', 'IF-21-A')");

                // 5. Ruangan
                String[] rooms = { "Lab Komputer 1", "Lab Komputer 2", "Lab Jaringan", "R. 301", "R. 302" };
                for (String r : rooms) {
                        stmt.execute("INSERT INTO ruangan (name) VALUES ('" + r + "')");
                }

                // 6. Mata Kuliah & Ujian Sample
                // Arif Nurochman (11009010) - Struktur Data
                stmt.execute("INSERT INTO mata_kuliah (kode_matkul, name, nama_kelas, username_dosen) VALUES ('IF202', 'Struktur Data', 'IF-21-A', '11009010')");
                stmt.execute("INSERT INTO ujian (code, target_kelas, title, kode_matkul, type, jadwal_waktu, durasi_menit, username_dosen, ruangan, status) "
                                + "VALUES ('EXM-001', 'IF-21-A', 'UTS Struktur Data', 'IF202', 'UTS', CURRENT_TIMESTAMP, 90, '11009010', 'Lab Komputer 1', 'ONGOING')");

                // Questions for EXM-001 (Assuming ID=1)
                // Question 1 (PG)
                stmt.execute("INSERT INTO soal_ujian (id_ujian, pertanyaan, option_a, option_b, option_c, option_d, kunci_jawaban, type) "
                                +
                                "VALUES (1, 'Apa itu Stack?', 'Tumpukan data LIFO', 'Antrian data FIFO', 'Pohon biner', 'Graph berarah', 'A', 'PG')");
                // Question 2 (PG)
                stmt.execute("INSERT INTO soal_ujian (id_ujian, pertanyaan, option_a, option_b, option_c, option_d, kunci_jawaban, type) "
                                +
                                "VALUES (1, 'Manakah yang bukan struktur data linear?', 'Array', 'LinkedList', 'Queue', 'Tree', 'D', 'PG')");
                // Question 3 (Essay)
                stmt.execute("INSERT INTO soal_ujian (id_ujian, pertanyaan, type) " +
                                "VALUES (1, 'Jelaskan perbedaan Array dan LinkedList!', 'ESSAY')");

                // Jalal (11009011) - Algoritma
                stmt.execute("INSERT INTO mata_kuliah (kode_matkul, name, nama_kelas, username_dosen) VALUES ('IF101', 'Algoritma', 'IF-21-A', '11009011')");
                stmt.execute("INSERT INTO ujian (code, target_kelas, title, kode_matkul, type, jadwal_waktu, durasi_menit, username_dosen, ruangan, status) "
                                + "VALUES ('EXM-002', 'IF-21-A', 'UAS Algoritma', 'IF101', 'UAS', DATETIME('now', '+1 day'), 120, '11009011', 'Lab Komputer 2', 'SCHEDULED')");

                // Ramadhan (11009012) - Pemrograman Web
                stmt.execute("INSERT INTO mata_kuliah (kode_matkul, name, nama_kelas, username_dosen) VALUES ('IF305', 'Pemrograman Web', 'IF-21-A', '11009012')");
        }

        public static void main(String[] args) {
                initialize();
        }
}
