package id.ac.campus.antiexam.config;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseSetup {

        public static void initialize() {
                try (Connection conn = DBConnection.getConnection();
                                Statement stmt = conn.createStatement()) {

                        // Admins
                        stmt.execute("CREATE TABLE IF NOT EXISTS admins (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "username TEXT NOT NULL UNIQUE, " +
                                        "password TEXT NOT NULL)");

                        // Lecturers
                        stmt.execute("CREATE TABLE IF NOT EXISTS lecturers (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "username TEXT NOT NULL UNIQUE, " +
                                        "password TEXT NOT NULL, " +
                                        "name TEXT NOT NULL)");

                        // Students
                        stmt.execute("CREATE TABLE IF NOT EXISTS students (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT NOT NULL, " +
                                        "student_number TEXT NOT NULL UNIQUE, " +
                                        "class_name TEXT NOT NULL)");

                        // Subjects
                        stmt.execute("CREATE TABLE IF NOT EXISTS subjects (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "code TEXT NOT NULL, " +
                                        "name TEXT NOT NULL, " +
                                        "class_name TEXT NOT NULL, " +
                                        "lecturer_username TEXT NOT NULL, " +
                                        "proctor_username TEXT, " + // Added proctor
                                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                        // Migration for existing subjects table (Add proctor_username if missing)
                        try {
                                stmt.execute("ALTER TABLE subjects ADD COLUMN proctor_username TEXT");
                        } catch (Exception e) {
                                // Column likely exists, ignore
                        }

                        // Rooms
                        stmt.execute("CREATE TABLE IF NOT EXISTS rooms (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "name TEXT NOT NULL UNIQUE)");

                        // Exams
                        stmt.execute("CREATE TABLE IF NOT EXISTS exams (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "code TEXT NOT NULL, " +
                                        "target_class TEXT NOT NULL, " +
                                        "title TEXT NOT NULL, " +
                                        "subject_code TEXT NOT NULL, " +
                                        "type TEXT NOT NULL, " + // UTS / UAS
                                        "exam_mode TEXT DEFAULT 'PG', " + // Added: PG / ESSAY
                                        "scheduled_at TIMESTAMP, " +
                                        "academic_year INTEGER, " +
                                        "duration_min INTEGER, " +
                                        "lecturer_username TEXT NOT NULL, " +
                                        "proctor_username TEXT, " +
                                        "room TEXT, " +
                                        "question_file_path TEXT, " +
                                        "status TEXT DEFAULT 'SCHEDULED', " +
                                        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");

                        // Migration for exam_mode
                        try {
                                stmt.execute("ALTER TABLE exams ADD COLUMN exam_mode TEXT DEFAULT 'PG'");
                        } catch (Exception e) {
                                // Column likely exists
                        }

                        // Student Exams
                        stmt.execute("CREATE TABLE IF NOT EXISTS student_exams (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "exam_id INTEGER NOT NULL, " +
                                        "student_id INTEGER NOT NULL, " +
                                        "status TEXT NOT NULL, " +
                                        "violation_count INTEGER DEFAULT 0, " +
                                        "score INTEGER DEFAULT 0, " + // Added score column
                                        "started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                                        "finished_at TIMESTAMP, " +
                                        "FOREIGN KEY(exam_id) REFERENCES exams(id), " +
                                        "FOREIGN KEY(student_id) REFERENCES students(id))");

                        // Migration for student_exams score
                        try {
                                stmt.execute("ALTER TABLE student_exams ADD COLUMN score INTEGER DEFAULT 0");
                        } catch (Exception e) {
                                // Column likely exists
                        }

                        // Exam Questions
                        stmt.execute("CREATE TABLE IF NOT EXISTS exam_questions (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "exam_id INTEGER NOT NULL, " +
                                        "question_type TEXT, " +
                                        "question_text TEXT, " +
                                        "option_a TEXT, " +
                                        "option_b TEXT, " +
                                        "option_c TEXT, " +
                                        "option_d TEXT, " +
                                        "correct_answer TEXT, " +
                                        "FOREIGN KEY(exam_id) REFERENCES exams(id))");

                        // Answers (inferred)
                        stmt.execute("CREATE TABLE IF NOT EXISTS answers (" +
                                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                                        "student_exam_id INTEGER NOT NULL, " +
                                        "question_number INTEGER, " +
                                        "answer TEXT, " +
                                        "FOREIGN KEY(student_exam_id) REFERENCES student_exams(id))");

                        // Seed default admin if table is empty
                        if (!stmt.executeQuery("SELECT id FROM admins").next()) {
                                stmt.execute("INSERT INTO admins (username, password) VALUES ('admin', 'admin123')");
                        }

                        // Seed Dummy Data
                        seedDummyData(stmt);

                        System.out.println("Database tables initialized (SQLite) and dummy data seeded.");

                } catch (SQLException e) {
                        e.printStackTrace();
                }
        }

        private static void seedDummyData(Statement stmt) throws SQLException {
                // 1. Admins
                stmt.execute("INSERT OR IGNORE INTO admins (username, password) VALUES ('Staffadmin', 'budilope')");

                // 2. Lecturers
                stmt.execute("INSERT OR IGNORE INTO lecturers (username, password, name) VALUES ('1122334455', '123456', 'Arif Nurochman')");
                stmt.execute("INSERT OR IGNORE INTO lecturers (username, password, name) VALUES ('dosenB', '123456', 'Dr. Budi Santoso')");
                stmt.execute("INSERT OR IGNORE INTO lecturers (username, password, name) VALUES ('dosenC', '123456', 'Sri Minatun, M.Kom')");

                // 3. Rooms
                String[] rooms = { "Lab Komputer 1", "Lab Komputer 2", "Lab Jaringan", "R. 301", "R. 302" };
                for (String r : rooms) {
                        stmt.execute("INSERT OR IGNORE INTO rooms (name) VALUES ('" + r + "')");
                }

                // 4. Subjects
                // Assuming subjects table has unique constraint or we don't care about dupes
                // for now (schema didn't show unique on code)
                // We'll check if exists to be safe
                if (!stmt.executeQuery("SELECT count(*) FROM subjects").next() || stmt.getResultSet().getInt(1) == 0) {
                        stmt.execute("INSERT INTO subjects (code, name, class_name, lecturer_username) VALUES ('IF202', 'Struktur Data', 'IF-21-A', '1122334455')");
                        stmt.execute("INSERT INTO subjects (code, name, class_name, lecturer_username) VALUES ('IF305', 'Pemrograman Web', 'IF-21-A', 'dosenB')");
                        stmt.execute("INSERT INTO subjects (code, name, class_name, lecturer_username) VALUES ('IF401', 'Jaringan Komputer', 'IF-21-B', 'dosenC')");
                        stmt.execute("INSERT INTO subjects (code, name, class_name, lecturer_username) VALUES ('IF101', 'Algoritma', 'IF-21-A', '1122334455')");
                }

                // 5. Students
                stmt.execute("INSERT OR IGNORE INTO students (name, student_number, class_name) VALUES ('Ahmad Ilyas', '1125170130', 'IF-21-A')");
                stmt.execute("INSERT OR IGNORE INTO students (name, student_number, class_name) VALUES ('Deni Dermawan', '1124563216', 'IF-21-A')");
                stmt.execute("INSERT OR IGNORE INTO students (name, student_number, class_name) VALUES ('Ilham Budi Handika', '1124160152', 'IF-21-A')");
                stmt.execute("INSERT OR IGNORE INTO students (name, student_number, class_name) VALUES ('Fikri Bintang Purnomo', '1122140142', 'IF-21-A')");

                // 6. Exams (Checking if empty first to avoid infinite duplicates)
                if (stmt.executeQuery("SELECT count(*) FROM exams").getInt(1) == 0) {
                        // ONGOING
                        stmt.execute("INSERT INTO exams (code, target_class, title, subject_code, type, scheduled_at, duration_min, lecturer_username, room, status) "
                                        +
                                        "VALUES ('EX-001', 'IF-21-A', 'UTS Struktur Data', 'IF202', 'UTS', CURRENT_TIMESTAMP, 90, '1122334455', 'Lab Komputer 1', 'ONGOING')");

                        // SCHEDULED
                        stmt.execute("INSERT INTO exams (code, target_class, title, subject_code, type, scheduled_at, duration_min, lecturer_username, room, status) "
                                        +
                                        "VALUES ('EX-002', 'IF-21-A', 'UAS Pemrograman Web', 'IF305', 'UAS', DATE('now', '+3 days'), 120, 'dosenB', 'Lab Komputer 2', 'SCHEDULED')");

                        stmt.execute("INSERT INTO exams (code, target_class, title, subject_code, type, scheduled_at, duration_min, lecturer_username, room, status) "
                                        +
                                        "VALUES ('EX-003', 'IF-21-B', 'Kuis Jarkom 1', 'IF401', 'HARIAN', DATE('now', '+1 day'), 60, 'dosenC', 'Lab Jaringan', 'SCHEDULED')");

                        stmt.execute("INSERT INTO exams (code, target_class, title, subject_code, type, scheduled_at, duration_min, lecturer_username, room, status) "
                                        +
                                        "VALUES ('EX-004', 'IF-21-A', 'Remedial Algoritma', 'IF101', 'REMEDIAL', DATE('now', '+7 days'), 90, '1122334455', 'R. 301', 'SCHEDULED')");
                }
        }
}
