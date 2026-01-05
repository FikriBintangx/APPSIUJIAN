$enc = [System.Text.Encoding]::UTF8

# --- LoginDosenFrame ---
$p1 = "src/id/ac/campus/antiexam/ui/ux/dosen/LoginDosenFrame.java"
if (Test-Path $p1) {
    Write-Host "Fixing LoginDosen..."
    $c1 = Get-Content -Path $p1 -Raw -Encoding UTF8
    $c1 = $c1 -replace 'lblLogo.setText\(.*?\)', 'lblLogo.setText("\uD83D\uDC68\u200D\uD83C\uDFEB")'
    $c1 = $c1 -replace 'createTextButton\(.*?Kembali.*?\)', 'createTextButton("\u2190 Kembali ke Menu Utama")'
    $c1 = $c1 -replace 'lblError.setText\(.*?Lengkapi.*?\)', 'lblError.setText("\u26A0\uFE0F Lengkapi username dan password.")'
    $c1 = $c1 -replace 'lblError.setText\(.*?Username.*?\)', 'lblError.setText("\u26A0\uFE0F Username atau password salah.")'
    $c1 = $c1 -replace 'lblError.setText\(.*?Error:.*?\)', 'lblError.setText("\u26A0\uFE0F Error: " + ex.getMessage())'
    $c1 = $c1 -replace 'Â©', '\u00A9'
    $c1 = $c1 -replace 'g2.drawRoundRect\(0, 0,', 'g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(0, 0,'
    Set-Content -Path $p1 -Value $c1 -Encoding UTF8
}

# --- LoginPengawasFrame ---
$p2 = "src/id/ac/campus/antiexam/ui/ux/pengawas/LoginPengawasFrame.java"
if (Test-Path $p2) {
    Write-Host "Fixing LoginPengawas..."
    $c2 = Get-Content -Path $p2 -Raw -Encoding UTF8
    $c2 = $c2 -replace 'lblLogo.setText\(.*?\)', 'lblLogo.setText("\uD83D\uDC40")'
    $c2 = $c2 -replace 'createTextButton\(.*?Kembali.*?\)', 'createTextButton("\u2190 Kembali ke Menu Utama")'
    $c2 = $c2 -replace 'lblError.setText\(.*?Lengkapi.*?\)', 'lblError.setText("\u26A0\uFE0F Lengkapi username dan password.")'
    $c2 = $c2 -replace 'lblError.setText\(.*?Username.*?\)', 'lblError.setText("\u26A0\uFE0F Username atau password salah.")'
    $c2 = $c2 -replace 'lblError.setText\(.*?Error:.*?\)', 'lblError.setText("\u26A0\uFE0F Error: " + ex.getMessage())'
    $c2 = $c2 -replace 'Â©', '\u00A9'
    $c2 = $c2 -replace 'g2.drawRoundRect\(0, 0,', 'g2.setStroke(new BasicStroke(3)); g2.drawRoundRect(0, 0,'
    Set-Content -Path $p2 -Value $c2 -Encoding UTF8
}

# --- ManualQuestionEditorDialog ---
$p3 = "src/id/ac/campus/antiexam/ui/ux/ManualQuestionEditorDialog.java"
if (Test-Path $p3) {
    Write-Host "Fixing ManualEditor..."
    $c3 = Get-Content -Path $p3 -Raw -Encoding UTF8
    $c3 = $c3 -replace 'super\(parent, .*?Editor Soal.*?"', 'super(parent, "\u2728 Editor Soal Ultra Friendly (Google Form Style)"'
    $c3 = $c3 -replace 'JLabel\(".*?Bank Soal"\)', 'JLabel("\uD83D\uDCDA Bank Soal")'
    $c3 = $c3 -replace 'new NeoButton\(".*?Buat Soal Baru"', 'new NeoButton("\u2795 Buat Soal Baru"'
    $c3 = $c3 -replace 'new NeoButton\(".*?Hapus Soal"', 'new NeoButton("\uD83D\uDDD1\uFE0F Hapus Soal"'
    $c3 = $c3 -replace 'new NeoButton\(".*?Export PDF"', 'new NeoButton("\uD83D\uDCC4 Export PDF"'
    
    # Messages
    $c3 = $c3 -replace 'ya .*?"\)', 'ya \uD83D\uDE4F")'
    $c3 = $c3 -replace 'jiwa .*?"\)', 'jiwa \uD83D\uDD25")'
    $c3 = $c3 -replace 'Lanjuttt .*?"\)', 'Lanjuttt \uD83D\uDE80")'
    $c3 = $c3 -replace '\(dihapus\) .*?"\)', '(dihapus) \uD83E\uDD40")'
    $c3 = $c3 -replace 'gih .*?"\)', 'gih \uD83D\uDCC4")'
    
    # Border
    $c3 = $c3 -replace 'new BasicStroke\(2\)', 'new BasicStroke(3)'
    Set-Content -Path $p3 -Value $c3 -Encoding UTF8
}
