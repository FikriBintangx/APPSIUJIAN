import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

public class TranslateAllComments {

    public static void main(String[] args) throws Exception {
        System.out.println("🚀 Mulai translate SEMUA komentar ke Bahasa Indonesia Gen Z...\n");

        Path srcDir = Paths.get("src");
        int fileCount = 0;
        int commentCount = 0;

        List<Path> javaFiles = new ArrayList<>();
        Files.walk(srcDir)
                .filter(p -> p.toString().endsWith(".java"))
                .forEach(javaFiles::add);

        for (Path path : javaFiles) {
            try {
                List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);
                List<String> newLines = new ArrayList<>();
                boolean modified = false;

                for (String line : lines) {
                    String newLine = translateComment(line);
                    if (!newLine.equals(line)) {
                        modified = true;
                        commentCount++;
                    }
                    newLines.add(newLine);
                }

                if (modified) {
                    Files.write(path, newLines, StandardCharsets.UTF_8);
                    System.out.println("✅ Updated: " + path.getFileName());
                    fileCount++;
                }

            } catch (Exception e) {
                System.err.println("❌ Error processing " + path + ": " + e.getMessage());
            }
        }

        System.out.println("\n🎉 Selesai ngab!");
        System.out.println("📊 Total file diupdate: " + fileCount);
        System.out.println("💬 Total komentar ditranslate: " + commentCount);
    }

    private static String translateComment(String line) {
        // cek apakah ada komentar di line ini
        if (!line.contains("//")) {
            return line;
        }

        // ambil bagian komentar
        int commentStart = line.indexOf("//");
        String beforeComment = line.substring(0, commentStart);
        String comment = line.substring(commentStart);

        // skip kalo udah bahasa Indonesia (ada kata-kata Indo)
        if (containsIndonesian(comment)) {
            return line;
        }

        // translate komentar
        String translated = translateToIndo(comment);

        return beforeComment + translated;
    }

    private static boolean containsIndonesian(String comment) {
        String lower = comment.toLowerCase();
        // cek kata-kata Indonesia umum
        String[] indoWords = {
                "buat", "ngab", "kalo", "udah", "aja", "doang", "banget", "gak", "ga ",
                "yang", "ini", "itu", "sama", "juga", "cuma", "biar", "keren", "kece",
                "tabel", "data", "warna", "tombol", "panel", "ikon", "label", "input",
                "ambil", "simpan", "hapus", "tambahin", "ilangin", "cek", "load",
                "bersihin", "aktifin", "matiin", "tampilin", "sembunyiin", "jalanin"
        };

        for (String word : indoWords) {
            if (lower.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private static String translateToIndo(String comment) {
        // mapping kata per kata
        Map<String, String> wordMap = new HashMap<>();

        // kata kerja
        wordMap.put("initialize", "inisialisasi");
        wordMap.put("create", "bikin");
        wordMap.put("update", "update");
        wordMap.put("delete", "hapus");
        wordMap.put("remove", "ilangin");
        wordMap.put("add", "tambahin");
        wordMap.put("set", "set");
        wordMap.put("get", "ambil");
        wordMap.put("load", "load");
        wordMap.put("save", "simpan");
        wordMap.put("check", "cek");
        wordMap.put("validate", "validasi");
        wordMap.put("handle", "handle");
        wordMap.put("process", "proses");
        wordMap.put("execute", "jalanin");
        wordMap.put("run", "jalanin");
        wordMap.put("start", "mulai");
        wordMap.put("stop", "stop");
        wordMap.put("close", "tutup");
        wordMap.put("open", "buka");
        wordMap.put("read", "baca");
        wordMap.put("write", "tulis");
        wordMap.put("build", "build");
        wordMap.put("generate", "generate");
        wordMap.put("render", "render");
        wordMap.put("draw", "gambar");
        wordMap.put("display", "tampilin");
        wordMap.put("show", "tampilin");
        wordMap.put("hide", "sembunyiin");
        wordMap.put("enable", "aktifin");
        wordMap.put("disable", "matiin");
        wordMap.put("refresh", "refresh");
        wordMap.put("reload", "reload");
        wordMap.put("reset", "reset");
        wordMap.put("clear", "bersihin");
        wordMap.put("clean", "bersihin");
        wordMap.put("fix", "benerin");
        wordMap.put("repair", "benerin");
        wordMap.put("calculate", "hitung");
        wordMap.put("compute", "hitung");
        wordMap.put("toggle", "toggle");
        wordMap.put("switch", "ganti");
        wordMap.put("change", "ganti");
        wordMap.put("modify", "ubah");
        wordMap.put("edit", "edit");
        wordMap.put("fetch", "ambil");
        wordMap.put("retrieve", "ambil");

        // kata benda
        wordMap.put("color", "warna");
        wordMap.put("colors", "warna-warna");
        wordMap.put("button", "tombol");
        wordMap.put("label", "label");
        wordMap.put("input", "input");
        wordMap.put("field", "field");
        wordMap.put("panel", "panel");
        wordMap.put("header", "header");
        wordMap.put("footer", "footer");
        wordMap.put("table", "tabel");
        wordMap.put("form", "form");
        wordMap.put("layout", "layout");
        wordMap.put("container", "container");
        wordMap.put("icon", "ikon");
        wordMap.put("image", "gambar");
        wordMap.put("shadow", "shadow");
        wordMap.put("border", "border");
        wordMap.put("text", "teks");
        wordMap.put("background", "background");
        wordMap.put("data", "data");
        wordMap.put("file", "file");
        wordMap.put("path", "path");
        wordMap.put("error", "error");
        wordMap.put("warning", "warning");
        wordMap.put("info", "info");
        wordMap.put("debug", "debug");
        wordMap.put("log", "log");
        wordMap.put("value", "value");
        wordMap.put("constant", "konstanta");
        wordMap.put("constants", "konstanta");
        wordMap.put("variable", "variabel");
        wordMap.put("parameter", "parameter");
        wordMap.put("argument", "argumen");
        wordMap.put("method", "method");
        wordMap.put("function", "fungsi");
        wordMap.put("class", "class");
        wordMap.put("object", "objek");
        wordMap.put("component", "komponen");
        wordMap.put("components", "komponen");
        wordMap.put("service", "service");
        wordMap.put("controller", "controller");
        wordMap.put("model", "model");
        wordMap.put("view", "view");
        wordMap.put("logic", "logika");
        wordMap.put("main", "utama");
        wordMap.put("custom", "custom");
        wordMap.put("default", "default");
        wordMap.put("initial", "awal");
        wordMap.put("final", "akhir");

        // kata sifat
        wordMap.put("active", "aktif");
        wordMap.put("visible", "keliatan");
        wordMap.put("hidden", "tersembunyi");
        wordMap.put("enabled", "aktif");
        wordMap.put("disabled", "nonaktif");
        wordMap.put("selected", "dipilih");
        wordMap.put("current", "sekarang");
        wordMap.put("new", "baru");
        wordMap.put("old", "lama");
        wordMap.put("first", "pertama");
        wordMap.put("last", "terakhir");
        wordMap.put("next", "selanjutnya");
        wordMap.put("previous", "sebelumnya");
        wordMap.put("left", "kiri");
        wordMap.put("right", "kanan");
        wordMap.put("top", "atas");
        wordMap.put("bottom", "bawah");
        wordMap.put("center", "tengah");
        wordMap.put("middle", "tengah");
        wordMap.put("front", "depan");
        wordMap.put("back", "belakang");
        wordMap.put("inner", "dalam");
        wordMap.put("outer", "luar");
        wordMap.put("thick", "tebel");
        wordMap.put("thin", "tipis");
        wordMap.put("sharp", "tajam");
        wordMap.put("smooth", "halus");
        wordMap.put("clean", "bersih");
        wordMap.put("simple", "simple");
        wordMap.put("complex", "kompleks");
        wordMap.put("dynamic", "dinamis");
        wordMap.put("static", "statis");

        // frasa umum
        wordMap.put("by default", "by default");
        wordMap.put("if not", "kalo ga");
        wordMap.put("if exists", "kalo ada");
        wordMap.put("to avoid", "biar ga");
        wordMap.put("based on", "berdasarkan");
        wordMap.put("for", "buat");
        wordMap.put("with", "dengan");
        wordMap.put("without", "tanpa");
        wordMap.put("only", "cuma");
        wordMap.put("just", "cuma");
        wordMap.put("already", "udah");
        wordMap.put("not", "ga");
        wordMap.put("and", "dan");
        wordMap.put("or", "atau");
        wordMap.put("the", "");
        wordMap.put("a ", "");
        wordMap.put("an ", "");

        // translate
        String result = comment;
        String lower = comment.toLowerCase();

        // ganti kata per kata (case insensitive)
        for (Map.Entry<String, String> entry : wordMap.entrySet()) {
            String pattern = "(?i)\\b" + Pattern.quote(entry.getKey()) + "\\b";
            result = result.replaceAll(pattern, entry.getValue());
        }

        // bersihin spasi ganda
        result = result.replaceAll("\\s+", " ");
        result = result.replaceAll("// +", "// ");

        return result;
    }
}
