import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class TranslateComments {

    // mapping komentar Inggris ke Indonesia gaya Gen Z
    private static final Map<String, String> COMMENT_MAP = new HashMap<>();

    static {
        // kata-kata umum
        COMMENT_MAP.put("// Colors", "// warna-warna");
        COMMENT_MAP.put("// Color", "// warna");
        COMMENT_MAP.put("// Role Constants", "// konstanta role");
        COMMENT_MAP.put("// Toggle", "// toggle");
        COMMENT_MAP.put("// Background", "// background");
        COMMENT_MAP.put("// Dot pattern", "// pola titik-titik");
        COMMENT_MAP.put("// Main Card", "// kartu utama");
        COMMENT_MAP.put("// Shadow", "// shadow");
        COMMENT_MAP.put("// Border", "// border");
        COMMENT_MAP.put("// Text", "// teks");
        COMMENT_MAP.put("// Icon", "// ikon");
        COMMENT_MAP.put("// Button", "// tombol");
        COMMENT_MAP.put("// Label", "// label");
        COMMENT_MAP.put("// Input", "// input");
        COMMENT_MAP.put("// Field", "// field");
        COMMENT_MAP.put("// Panel", "// panel");
        COMMENT_MAP.put("// Header", "// header");
        COMMENT_MAP.put("// Footer", "// footer");
        COMMENT_MAP.put("// Table", "// tabel");
        COMMENT_MAP.put("// Form", "// form");
        COMMENT_MAP.put("// Layout", "// layout");
        COMMENT_MAP.put("// Container", "// container");

        // aksi-aksi
        COMMENT_MAP.put("// Initialize", "// inisialisasi");
        COMMENT_MAP.put("// Create", "// bikin");
        COMMENT_MAP.put("// Update", "// update");
        COMMENT_MAP.put("// Delete", "// hapus");
        COMMENT_MAP.put("// Add", "// tambahin");
        COMMENT_MAP.put("// Remove", "// ilangin");
        COMMENT_MAP.put("// Set", "// set");
        COMMENT_MAP.put("// Get", "// ambil");
        COMMENT_MAP.put("// Load", "// load");
        COMMENT_MAP.put("// Save", "// simpan");
        COMMENT_MAP.put("// Check", "// cek");
        COMMENT_MAP.put("// Validate", "// validasi");
        COMMENT_MAP.put("// Handle", "// handle");
        COMMENT_MAP.put("// Process", "// proses");
        COMMENT_MAP.put("// Execute", "// jalanin");
        COMMENT_MAP.put("// Run", "// jalanin");
        COMMENT_MAP.put("// Start", "// mulai");
        COMMENT_MAP.put("// Stop", "// stop");
        COMMENT_MAP.put("// Close", "// tutup");
        COMMENT_MAP.put("// Open", "// buka");
        COMMENT_MAP.put("// Read", "// baca");
        COMMENT_MAP.put("// Write", "// tulis");
        COMMENT_MAP.put("// Build", "// build");
        COMMENT_MAP.put("// Generate", "// generate");
        COMMENT_MAP.put("// Parse", "// parse");
        COMMENT_MAP.put("// Format", "// format");
        COMMENT_MAP.put("// Convert", "// convert");
        COMMENT_MAP.put("// Calculate", "// hitung");
        COMMENT_MAP.put("// Render", "// render");
        COMMENT_MAP.put("// Draw", "// gambar");
        COMMENT_MAP.put("// Paint", "// cat");
        COMMENT_MAP.put("// Display", "// tampilin");
        COMMENT_MAP.put("// Show", "// tampilin");
        COMMENT_MAP.put("// Hide", "// sembunyiin");
        COMMENT_MAP.put("// Enable", "// aktifin");
        COMMENT_MAP.put("// Disable", "// matiin");
        COMMENT_MAP.put("// Refresh", "// refresh");
        COMMENT_MAP.put("// Reload", "// reload");
        COMMENT_MAP.put("// Reset", "// reset");
        COMMENT_MAP.put("// Clear", "// bersihin");
        COMMENT_MAP.put("// Clean", "// bersihin");

        // komentar spesifik yang sering muncul
        COMMENT_MAP.put("// Fix garbled emojis", "// benerin emoji yang berantakan");
        COMMENT_MAP.put("// Calendar emoji", "// emoji kalender");
        COMMENT_MAP.put("// Clipboard emoji", "// emoji clipboard");
        COMMENT_MAP.put("// Plus emoji", "// emoji plus");
        COMMENT_MAP.put("// Pencil emoji", "// emoji pensil");
        COMMENT_MAP.put("// People emoji", "// emoji orang");
        COMMENT_MAP.put("// School emoji", "// emoji sekolah");
        COMMENT_MAP.put("// Document emoji", "// emoji dokumen");
        COMMENT_MAP.put("// Floppy disk emoji", "// emoji disket");
        COMMENT_MAP.put("// Trash emoji", "// emoji tong sampah");
        COMMENT_MAP.put("// Download emoji", "// emoji download");
        COMMENT_MAP.put("// Magnifying glass emoji", "// emoji kaca pembesar");
        COMMENT_MAP.put("// Chart emoji", "// emoji chart");
        COMMENT_MAP.put("// Graduation cap emoji", "// emoji toga wisuda");
        COMMENT_MAP.put("// Lock emoji", "// emoji gembok");
        COMMENT_MAP.put("// Email emoji", "// emoji email");
        COMMENT_MAP.put("// Warning emoji", "// emoji peringatan");
        COMMENT_MAP.put("// Check mark emoji", "// emoji centang");
        COMMENT_MAP.put("// Cross mark emoji", "// emoji silang");
        COMMENT_MAP.put("// Reset emoji", "// emoji reset");
        COMMENT_MAP.put("// Building emoji", "// emoji gedung");

        // komentar panjang
        COMMENT_MAP.put("// Remove all emoji patterns - just remove the emoji part, keep the text",
                "// ini buat ngilangin emoji doang ngab, teks aslinya tetep aman");
        COMMENT_MAP.put("// 1. Create Tables if not exist", "// 1. bikin tabel kalo belum ada");
        COMMENT_MAP.put("// 2. Insert Test Data", "// 2. masukin data testing");
        COMMENT_MAP.put("// Ignore unique constraint violations (data already exists)",
                "// abaikan error unique constraint (data udah ada)");
        COMMENT_MAP.put("// Vibrant Blue from image", "// biru vibrant dari gambar");
        COMMENT_MAP.put("// Light background", "// background terang");
        COMMENT_MAP.put("// Background with Dot Pattern", "// background dengan pola titik-titik");
        COMMENT_MAP.put("// Main Card Shadow Container", "// container shadow buat kartu utama");
        COMMENT_MAP.put("// The actual card content", "// konten kartu yang sebenarnya");
        COMMENT_MAP.put("// Thick border", "// border tebel");
        COMMENT_MAP.put("// === LEFT PANEL ===", "// === PANEL KIRI ===");
        COMMENT_MAP.put("// === RIGHT PANEL ===", "// === PANEL KANAN ===");
        COMMENT_MAP.put("// Icon (Graduation Cap)", "// ikon toga wisuda");
        COMMENT_MAP.put("// Use GBL for centering form", "// pake GBL buat centering form");
        COMMENT_MAP.put("// Role Dropdown (Added as requested to keep functionality)",
                "// dropdown role (ditambah sesuai request buat jaga fungsionalitas)");
        COMMENT_MAP.put("// Input 1 Label (Dynamic)", "// label input 1 (dinamis)");
        COMMENT_MAP.put("// Input 2 Label (Dynamic)", "// label input 2 (dinamis)");
        COMMENT_MAP.put("// No border himself", "// ga pake border sendiri");
        COMMENT_MAP.put("// Remember", "// checkbox ingat saya");
        COMMENT_MAP.put("// Login Button", "// tombol login");
        COMMENT_MAP.put("// Add to Form Container", "// tambahin ke container form");
        COMMENT_MAP.put("// Add Shadow Effect (Simple offset black panel behind)",
                "// tambahin efek shadow (panel hitam di belakang dengan offset)");
        COMMENT_MAP.put("// Offset by 10px", "// offset 10px");
        COMMENT_MAP.put("// Top", "// atas");
        COMMENT_MAP.put("// Bottom", "// bawah");
        COMMENT_MAP.put("// Front", "// depan");
        COMMENT_MAP.put("// Back", "// belakang");
        COMMENT_MAP.put("// Initial Logic", "// logic awal");
        COMMENT_MAP.put("// --- Logic ---", "// --- logika utama ---");
        COMMENT_MAP.put("// Visible by default for NIM", "// keliatan by default buat NIM");
        COMMENT_MAP.put("// Visible by default for NID", "// keliatan by default buat NID");
        COMMENT_MAP.put("// Map roles to repo calls", "// mapping role ke repo calls");
        COMMENT_MAP.put("// --- Custom Components ---", "// --- komponen custom ---");
        COMMENT_MAP.put("// Neo-Brutalism Button", "// tombol neo-brutalism");
        COMMENT_MAP.put("// Sharp edges", "// tepi tajam");
        COMMENT_MAP.put("// Main Body", "// main body");
        COMMENT_MAP.put("// Adjust for stroke", "// sesuaiin buat stroke");
        COMMENT_MAP.put("// Admin", "// tabel admin");
        COMMENT_MAP.put("// Dosen", "// tabel dosen");
        COMMENT_MAP.put("// Pengawas", "// tabel pengawas");
        COMMENT_MAP.put("// Mahasiswa", "// tabel mahasiswa");
        COMMENT_MAP.put("// Ujian (Simplified for seeder)", "// tabel ujian (versi simple buat seeder)");
        COMMENT_MAP.put("// Load PDF in background to avoid freezing UI",
                "// load PDF di background biar UI ga freeze");
        COMMENT_MAP.put("// Render in worker to allow smooth UI",
                "// render di worker biar UI smooth");
        COMMENT_MAP.put("// Start auto-save", "// mulai auto-save");
        COMMENT_MAP.put("// Show finish button only on last question for PG",
                "// tampilin tombol selesai cuma di soal terakhir buat PG");
        COMMENT_MAP.put("// Main split: Left sidebar (question palette) + Right content",
                "// split utama: sidebar kiri (palet soal) + konten kanan");
        COMMENT_MAP.put("// Check for PDF file and integrate PDF Viewer if exists",
                "// cek file PDF dan integrasiin PDF Viewer kalo ada");
        COMMENT_MAP.put("// Update button visibility based on position",
                "// update visibilitas tombol berdasarkan posisi");
        COMMENT_MAP.put("// Enable if not first question", "// aktifin kalo bukan soal pertama");
        COMMENT_MAP.put("// Last question: hide next, show finish",
                "// soal terakhir: sembunyiin next, tampilin finish");
        COMMENT_MAP.put("// Update question palette and progress",
                "// update palet soal dan progress");
        COMMENT_MAP.put("// Update UI immediately", "// update UI langsung");
        COMMENT_MAP.put("// Update progress label", "// update label progress");
        COMMENT_MAP.put("// Change color based on completion",
                "// ganti warna berdasarkan penyelesaian");
        COMMENT_MAP.put("// Create question palette with clickable buttons",
                "// bikin palet soal dengan tombol yang bisa diklik");
        COMMENT_MAP.put("// Clean look", "// tampilan bersih");
        COMMENT_MAP.put("// Close Button", "// tombol tutup");
    }

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Mulai translate komentar ke Bahasa Indonesia Gen Z...\n");

        Path srcDir = Paths.get("src");
        int fileCount = 0;
        int commentCount = 0;

        Files.walk(srcDir)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(path -> {
                    try {
                        String content = Files.readString(path, StandardCharsets.UTF_8);
                        String originalContent = content;

                        // ganti komentar pake mapping
                        for (Map.Entry<String, String> entry : COMMENT_MAP.entrySet()) {
                            content = content.replace(entry.getKey(), entry.getValue());
                        }

                        // kalo ada perubahan, save file
                        if (!content.equals(originalContent)) {
                            Files.writeString(path, content, StandardCharsets.UTF_8);
                            System.out.println("✅ Updated: " + path.getFileName());
                        }

                    } catch (Exception e) {
                        System.err.println("❌ Error processing " + path + ": " + e.getMessage());
                    }
                });

        System.out.println("\n🎉 Selesai ngab! Semua komentar udah di-translate ke Bahasa Indonesia Gen Z!");
    }
}
