import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

public class RemoveAllEmojis {
    public static void main(String[] args) throws Exception {
        File f = new File("src/id/ac/campus/antiexam/ui/ux/admin/KelolaJadwalUjianFrame.java");
        String content = Files.readString(f.toPath(), StandardCharsets.UTF_8);

        // ini buat ngilangin emoji doang ngab, teks aslinya tetep aman
        content = content.replaceAll("📅\\s*", "");
        content = content.replaceAll("📋\\s*", "");
        content = content.replaceAll("➕\\s*", "");
        content = content.replaceAll("✏️\\s*", "");
        content = content.replaceAll("👥\\s*", "");
        content = content.replaceAll("🏫\\s*", "");
        content = content.replaceAll("📄\\s*", "");
        content = content.replaceAll("💾\\s*", "");
        content = content.replaceAll("🗑️\\s*", "");
        content = content.replaceAll("📥\\s*", "");
        content = content.replaceAll("🔍\\s*", "");
        content = content.replaceAll("📊\\s*", "");
        content = content.replaceAll("🎓\\s*", "");
        content = content.replaceAll("🔒\\s*", "");
        content = content.replaceAll("📧\\s*", "");
        content = content.replaceAll("⚠️\\s*", "");
        content = content.replaceAll("✅\\s*", "");
        content = content.replaceAll("❌\\s*", "");
        content = content.replaceAll("🔄\\s*", "");
        content = content.replaceAll("🏢\\s*", "");
        content = content.replaceAll("👨‍🏫\\s*", "");
        content = content.replaceAll("📚\\s*", "");
        content = content.replaceAll("🕐\\s*", "");
        content = content.replaceAll("🛡️\\s*", "");
        content = content.replaceAll("📤\\s*", "");

        Files.writeString(f.toPath(), content, StandardCharsets.UTF_8);
        System.out.println("Removed all emojis from KelolaJadwalUjianFrame.java");
    }
}
