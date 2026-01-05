import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FinalCommentTranslator {

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 FINAL PASS: Translate SEMUA komentar Inggris ke Indo Gen Z...\n");

        Path srcDir = Paths.get("src");
        int fileCount = 0;
        int commentCount = 0;

        List<Path> javaFiles = new ArrayList<>();
        Files.walk(srcDir)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(javaFiles::add);

        System.out.println("📁 Total file Java ditemukan: " + javaFiles.size() + "\n");

        for (Path path : javaFiles) {
            try {
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                List<String> newLines = new ArrayList<>();
                boolean modified = false;

                for (String line : lines) {
                    String newLine = translateLine(line);
                    if (!newLine.equals(line)) {
                        modified = true;
                        commentCount++;
                    }
                    newLines.add(newLine);
                }

                if (modified) {
                    Files.write(path, newLines, StandardCharsets.UTF_8);
                    System.out.println("✅ " + path.getFileName());
                    fileCount++;
                }

            } catch (Exception e) {
                System.err.println("❌ Error: " + path + " - " + e.getMessage());
            }
        }

        System.out.println("\n🎉 SELESAI!");
        System.out.println("📊 File diupdate: " + fileCount);
        System.out.println("💬 Komentar ditranslate: " + commentCount);
    }

    private static String translateLine(String line) {
        if (!line.contains("//") && !line.contains("/*") && !line.contains("*/") && !line.contains("*")) {
            return line;
        }

        // skip kalo udah bahasa Indonesia
        if (containsIndo(line)) {
            return line;
        }

        String result = line;

        // translate frasa umum
        result = result.replace("// keliatan by default buat", "// keliatan by default buat");
        result = result.replace("// aktif by default", "// aktif by default");
        result = result.replace("// Initial load", "// load awal");
        result = result.replace("// load PDF in background to avoid freezing UI",
                "// load PDF di background biar UI ga freeze");
        result = result.replace("// Klik tabel langsung load data ke kanan",
                "// klik tabel langsung load data ke kanan");
        result = result.replace("// Cek update atau insert", "// cek update atau insert");
        result = result.replace("// update BOSS", "// update ngab");
        result = result.replace("// Auto-simpan every 30s", "// auto-simpan tiap 30 detik");
        result = result.replace("// Pre-fill answers map if resuming (optional, logika needed)",
                "// isi ulang jawaban kalo lanjutin ujian (optional, perlu logika)");
        result = result.replace("// tampilin finish button only on last question for PG",
                "// tampilin tombol selesai cuma di soal terakhir buat PG");
        result = result.replace("// Will be shown on terakhir question", "// bakal ditampilin di soal terakhir");
        result = result.replace("// update tombol visibility berdasarkan position",
                "// update visibilitas tombol berdasarkan posisi");
        result = result.replace("// aktifin if not first question", "// aktifin kalo bukan soal pertama");
        result = result.replace("// update palet soal dan progress", "// update palet soal dan progress");
        result = result.replace("// update UI langsung", "// update UI langsung");
        result = result.replace("// update progress label", "// update label progress");
        result = result.replace("// warna based on status", "// warna berdasarkan status");
        result = result.replace("// tampilin review page before final submit",
                "// tampilin halaman review sebelum submit final");
        result = result.replace("// Auto-simpan answers every 30 seconds", "// auto-simpan jawaban tiap 30 detik");
        result = result.replace("// Silent simpan - no notification biar ga disturbing student",
                "// simpan diam-diam - ga ada notif biar ga ganggu mahasiswa");
        result = result.replace("// simpan all answers to database first", "// simpan semua jawaban ke database dulu");
        result = result.replace("// Essay logika omitted as per request", "// logika essay dilewat sesuai request");
        result = result.replace("// update status to FINISHED", "// update status jadi FINISHED");
        result = result.replace("// tampilin result dialog then exit", "// tampilin dialog hasil terus keluar");
        result = result.replace("// tambahined for grading", "// ditambahin buat grading");
        result = result.replace("// Re-add content based on new data",
                "// tambahin ulang konten berdasarkan data baru");
        result = result.replace("// tambahin minimal border to status badge",
                "// tambahin border minimal ke badge status");
        result = result.replace("// validasi Token against Database (examCode is loaded from DB)",
                "// validasi token ke database (examCode diload dari DB)");
        result = result.replace("// Auto-Join / bikin Session if pertama time",
                "// auto-join / bikin session kalo pertama kali");
        result = result.replace("// cuma Ujian List buat Lecturer, no monitoring",
                "// cuma list ujian buat dosen, ga ada monitoring");
        result = result.replace("// Full width now", "// full width sekarang");
        result = result.replace("// No Action tombol buat Lecturer here, they cuma view",
                "// ga ada tombol aksi buat dosen di sini, mereka cuma liat");
        result = result.replace("// Buka editor soal, mode serius on", "// buka editor soal, mode serius on");
        result = result.replace("// Put references to be used in saveSettings",
                "// simpen referensi buat dipake di saveSettings");
        result = result.replace("// cuma username buat simplicity in saving",
                "// cuma username buat simplicity pas nyimpen");
        result = result.replace("// update", "// update");
        result = result.replace("// We will cuma update basic fields buat now atau call specific update",
                "// kita cuma update field basic buat sekarang atau panggil update spesifik");
        result = result.replace("// Actually we probably want updateUjian full params but we don't present all",
                "// sebenernya kita mau updateUjian full params tapi ga nampil semua");
        result = result.replace("// fields here.", "// field di sini.");
        result = result.replace("// Let's assume this minimal update is enough for settings view.",
                "// anggap aja update minimal ini cukup buat view settings.");
        result = result.replace("// Ideally we should have used updateUjian but we are missing some fields in",
                "// idealnya kita harusnya pake updateUjian tapi ada beberapa field yang kurang di");
        result = result.replace("// this", "// ini");
        result = result.replace("// It misses proctor update.", "// kurang update proctor.");
        result = result.replace("// Let's rely on examRepository.updateUjian which we can't easily call tanpa",
                "// kita andalin examRepository.updateUjian yang ga bisa dipanggil gampang tanpa");
        result = result.replace("// loading all data first.", "// load semua data dulu.");
        result = result.replace("// So buat now, we stick to what was there atau what fits.",
                "// jadi buat sekarang, kita pake apa yang udah ada atau yang cocok.");
        result = result.replace("// custom tombol class same as Proctor", "// class tombol custom sama kayak Proctor");
        result = result.replace("// Hover effect? Maybe specific cek if aktif",
                "// efek hover? mungkin cek spesifik kalo aktif");

        // kata-kata umum
        result = result.replaceAll("\\b(the|The)\\b", "");
        result = result.replaceAll("\\b(to|To)\\b", "buat");
        result = result.replaceAll("\\b(for|For)\\b", "buat");
        result = result.replaceAll("\\b(with|With)\\b", "dengan");
        result = result.replaceAll("\\b(from|From)\\b", "dari");
        result = result.replaceAll("\\b(in|In)\\b", "di");
        result = result.replaceAll("\\b(on|On)\\b", "di");
        result = result.replaceAll("\\b(at|At)\\b", "di");
        result = result.replaceAll("\\b(by|By)\\b", "oleh");
        result = result.replaceAll("\\b(of|Of)\\b", "dari");
        result = result.replaceAll("\\b(and|And)\\b", "dan");
        result = result.replaceAll("\\b(or|Or)\\b", "atau");
        result = result.replaceAll("\\b(is|Is)\\b", "adalah");
        result = result.replaceAll("\\b(are|Are)\\b", "adalah");
        result = result.replaceAll("\\b(if|If)\\b", "kalo");
        result = result.replaceAll("\\b(when|When)\\b", "pas");
        result = result.replaceAll("\\b(this|This)\\b", "ini");
        result = result.replaceAll("\\b(that|That)\\b", "itu");
        result = result.replaceAll("\\b(all|All)\\b", "semua");
        result = result.replaceAll("\\b(only|Only)\\b", "cuma");
        result = result.replaceAll("\\b(just|Just)\\b", "cuma");
        result = result.replaceAll("\\b(not|Not)\\b", "ga");
        result = result.replaceAll("\\b(no|No)\\b", "ga ada");
        result = result.replaceAll("\\b(can|Can)\\b", "bisa");
        result = result.replaceAll("\\b(will|Will)\\b", "bakal");
        result = result.replaceAll("\\b(should|Should)\\b", "harusnya");
        result = result.replaceAll("\\b(must|Must)\\b", "harus");
        result = result.replaceAll("\\b(have|Have)\\b", "punya");
        result = result.replaceAll("\\b(has|Has)\\b", "punya");
        result = result.replaceAll("\\b(get|Get)\\b", "ambil");
        result = result.replaceAll("\\b(set|Set)\\b", "set");
        result = result.replaceAll("\\b(add|Add)\\b", "tambahin");
        result = result.replaceAll("\\b(remove|Remove)\\b", "ilangin");
        result = result.replaceAll("\\b(delete|Delete)\\b", "hapus");
        result = result.replaceAll("\\b(update|Update)\\b", "update");
        result = result.replaceAll("\\b(create|Create)\\b", "bikin");
        result = result.replaceAll("\\b(make|Make)\\b", "bikin");
        result = result.replaceAll("\\b(load|Load)\\b", "load");
        result = result.replaceAll("\\b(save|Save)\\b", "simpan");
        result = result.replaceAll("\\b(check|Check)\\b", "cek");
        result = result.replaceAll("\\b(validate|Validate)\\b", "validasi");
        result = result.replaceAll("\\b(handle|Handle)\\b", "handle");
        result = result.replaceAll("\\b(process|Process)\\b", "proses");
        result = result.replaceAll("\\b(show|Show)\\b", "tampilin");
        result = result.replaceAll("\\b(hide|Hide)\\b", "sembunyiin");
        result = result.replaceAll("\\b(enable|Enable)\\b", "aktifin");
        result = result.replaceAll("\\b(disable|Disable)\\b", "matiin");
        result = result.replaceAll("\\b(start|Start)\\b", "mulai");
        result = result.replaceAll("\\b(stop|Stop)\\b", "stop");
        result = result.replaceAll("\\b(open|Open)\\b", "buka");
        result = result.replaceAll("\\b(close|Close)\\b", "tutup");
        result = result.replaceAll("\\b(first|First)\\b", "pertama");
        result = result.replaceAll("\\b(last|Last)\\b", "terakhir");
        result = result.replaceAll("\\b(next|Next)\\b", "selanjutnya");
        result = result.replaceAll("\\b(previous|Previous)\\b", "sebelumnya");
        result = result.replaceAll("\\b(new|New)\\b", "baru");
        result = result.replaceAll("\\b(old|Old)\\b", "lama");
        result = result.replaceAll("\\b(current|Current)\\b", "sekarang");
        result = result.replaceAll("\\b(default|Default)\\b", "default");
        result = result.replaceAll("\\b(initial|Initial)\\b", "awal");
        result = result.replaceAll("\\b(final|Final)\\b", "final");
        result = result.replaceAll("\\b(based|Based)\\b", "berdasarkan");
        result = result.replaceAll("\\b(every|Every)\\b", "tiap");
        result = result.replaceAll("\\b(each|Each)\\b", "tiap");
        result = result.replaceAll("\\b(before|Before)\\b", "sebelum");
        result = result.replaceAll("\\b(after|After)\\b", "setelah");
        result = result.replaceAll("\\b(then|Then)\\b", "terus");
        result = result.replaceAll("\\b(now|Now)\\b", "sekarang");
        result = result.replaceAll("\\b(here|Here)\\b", "di sini");
        result = result.replaceAll("\\b(there|There)\\b", "di sana");
        result = result.replaceAll("\\b(same|Same)\\b", "sama");
        result = result.replaceAll("\\b(different|Different)\\b", "beda");
        result = result.replaceAll("\\b(against|Against)\\b", "ke");
        result = result.replaceAll("\\b(without|Without)\\b", "tanpa");
        result = result.replaceAll("\\b(already|Already)\\b", "udah");
        result = result.replaceAll("\\b(still|Still)\\b", "masih");
        result = result.replaceAll("\\b(yet|Yet)\\b", "belum");
        result = result.replaceAll("\\b(also|Also)\\b", "juga");
        result = result.replaceAll("\\b(even|Even)\\b", "bahkan");
        result = result.replaceAll("\\b(maybe|Maybe)\\b", "mungkin");
        result = result.replaceAll("\\b(probably|Probably)\\b", "mungkin");
        result = result.replaceAll("\\b(actually|Actually)\\b", "sebenernya");
        result = result.replaceAll("\\b(ideally|Ideally)\\b", "idealnya");
        result = result.replaceAll("\\b(minimal|Minimal)\\b", "minimal");
        result = result.replaceAll("\\b(specific|Specific)\\b", "spesifik");
        result = result.replaceAll("\\b(basic|Basic)\\b", "basic");
        result = result.replaceAll("\\b(full|Full)\\b", "full");
        result = result.replaceAll("\\b(some|Some)\\b", "beberapa");
        result = result.replaceAll("\\b(missing|Missing)\\b", "kurang");
        result = result.replaceAll("\\b(enough|Enough)\\b", "cukup");
        result = result.replaceAll("\\b(easily|Easily)\\b", "gampang");
        result = result.replaceAll("\\b(simply|Simply)\\b", "cuma");
        result = result.replaceAll("\\b(simplicity|Simplicity)\\b", "simplicity");
        result = result.replaceAll("\\b(assume|Assume)\\b", "anggap");
        result = result.replaceAll("\\b(rely|Rely)\\b", "andalin");
        result = result.replaceAll("\\b(stick|Stick)\\b", "pake");
        result = result.replaceAll("\\b(fits|Fits)\\b", "cocok");
        result = result.replaceAll("\\b(custom|Custom)\\b", "custom");
        result = result.replaceAll("\\b(effect|Effect)\\b", "efek");
        result = result.replaceAll("\\b(active|Active)\\b", "aktif");

        // bersihin spasi ganda
        result = result.replaceAll("\\s+", " ");
        result = result.replaceAll("// +", "// ");

        return result;
    }

    private static boolean containsIndo(String line) {
        String lower = line.toLowerCase();
        String[] indoWords = {
                "buat", "ngab", "kalo", "udah", "aja", "doang", "banget", "gak", "ga ",
                "yang", "ini", "itu", "sama", "juga", "cuma", "biar", "keren", "kece",
                "tabel", "data", "warna", "tombol", "panel", "ikon", "label", "input",
                "ambil", "simpan", "hapus", "tambahin", "ilangin", "cek", "load",
                "bersihin", "aktifin", "matiin", "tampilin", "sembunyiin", "jalanin",
                "ngatur", "dipake", "pas ", "terus", "sekarang", "dulu", "lagi",
                "jangan", "harus", "bisa", "mau", "ada", "punya", "bikin", "dari",
                "ke ", "di ", "dengan", "atau", "dan", "untuk", "pada", "oleh"
        };

        for (String word : indoWords) {
            if (lower.contains(word)) {
                return true;
            }
        }
        return false;
    }
}
