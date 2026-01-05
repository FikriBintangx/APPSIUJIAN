import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

public class FixAdminUIEmojis {
    public static void main(String[] args) throws Exception {
        File f = new File("src/id/ac/campus/antiexam/ui/ux/admin/KelolaJadwalUjianFrame.java");
        List<String> lines = Files.readAllLines(f.toPath(), StandardCharsets.UTF_8);
        List<String> newLines = new ArrayList<>();

        for (String line : lines) {
            String fixed = line;

            // benerin emoji yang berantakan di judul sama label
            fixed = fixed.replaceAll("📅\\s*", ""); // emoji kalender
            fixed = fixed.replaceAll("📋\\s*", ""); // emoji clipboard
            fixed = fixed.replaceAll("➕\\s*", ""); // emoji plus
            fixed = fixed.replaceAll("✏️\\s*", ""); // emoji pensil
            fixed = fixed.replaceAll("👥\\s*", ""); // emoji orang
            fixed = fixed.replaceAll("🏫\\s*", ""); // emoji sekolah
            fixed = fixed.replaceAll("📄\\s*", ""); // emoji dokumen
            fixed = fixed.replaceAll("💾\\s*", ""); // emoji disket
            fixed = fixed.replaceAll("🗑️\\s*", ""); // emoji tong sampah
            fixed = fixed.replaceAll("📥\\s*", ""); // emoji download
            fixed = fixed.replaceAll("🔍\\s*", ""); // emoji kaca pembesar
            fixed = fixed.replaceAll("📊\\s*", ""); // emoji chart
            fixed = fixed.replaceAll("🎓\\s*", ""); // emoji toga wisuda
            fixed = fixed.replaceAll("🔒\\s*", ""); // emoji gembok
            fixed = fixed.replaceAll("📧\\s*", ""); // emoji email
            fixed = fixed.replaceAll("⚠️\\s*", ""); // emoji peringatan
            fixed = fixed.replaceAll("✅\\s*", ""); // emoji centang
            fixed = fixed.replaceAll("❌\\s*", ""); // emoji silang
            fixed = fixed.replaceAll("🔄\\s*", ""); // emoji reset
            fixed = fixed.replaceAll("🏢\\s*", ""); // emoji gedung

            newLines.add(fixed);
        }

        Files.write(f.toPath(), newLines, StandardCharsets.UTF_8);
        System.out.println("Fixed all garbled emojis in KelolaJadwalUjianFrame.java");
    }
}
