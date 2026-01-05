$enc = [System.Text.Encoding]::UTF8

# --- BerandaDosenFrame ---
$p1 = "src/id/ac/campus/antiexam/ui/ux/dosen/BerandaDosenFrame.java"
if (Test-Path $p1) {
    Write-Host "Fixing Dosen..."
    $c1 = Get-Content -Path $p1 -Raw -Encoding UTF8
    $c1 = $c1 -replace 'createSidebarButton\("Dashboard Utama", ".*?"\)', 'createSidebarButton("Dashboard Utama", "\uD83D\uDCCA")'
    $c1 = $c1 -replace 'createSidebarButton\("Atur Ujian", ".*?"\)', 'createSidebarButton("Atur Ujian", "\u2699\uFE0F")'
    $c1 = $c1 -replace 'createSidebarButton\("Laporan Hasil", ".*?"\)', 'createSidebarButton("Laporan Hasil", "\uD83D\uDCC4")'
    $c1 = $c1 -replace 'createSidebarButton\("Profil Saya", ".*?"\)', 'createSidebarButton("Profil Saya", "\uD83D\uDC64")'
    $c1 = $c1 -replace 'new NeoButton\(".*? Atur Soal \(Editor\)"', 'new NeoButton("\u270F\uFE0F Atur Soal (Editor)"'
    $c1 = $c1 -replace 'new NeoButton\(".*? Atur Soal Ujian Ini"', 'new NeoButton("\uD83D\uDCDD Atur Soal Ujian Ini"'
    $c1 = $c1 -replace 'new BasicStroke\(2\)', 'new BasicStroke(3)'
    Set-Content -Path $p1 -Value $c1 -Encoding UTF8
}

# --- BerandaPengawasFrame ---
$p2 = "src/id/ac/campus/antiexam/ui/ux/pengawas/BerandaPengawasFrame.java"
if (Test-Path $p2) {
    Write-Host "Fixing Pengawas..."
    $c2 = Get-Content -Path $p2 -Raw -Encoding UTF8
    $c2 = $c2 -replace 'NeoButton\(".*? Preview Soal"', 'NeoButton("\uD83D\uDC41\uFE0F Preview Soal"'
    $c2 = $c2 -replace 'Mode serius activacted .*?"\)', 'Mode serius activated \uD83E\uA4EB")'
    $c2 = $c2 -replace 'new BasicStroke\(2\)', 'new BasicStroke(3)'
    Set-Content -Path $p2 -Value $c2 -Encoding UTF8
}

# --- BerandaMahasiswaFrame ---
$p3 = "src/id/ac/campus/antiexam/ui/ux/mahasiswa/BerandaMahasiswaFrame.java"
if (Test-Path $p3) {
    Write-Host "Fixing Mahasiswa..."
    $c3 = Get-Content -Path $p3 -Raw -Encoding UTF8
    $c3 = $c3 -replace 'lblLogo.setText\(".*?"\)', 'lblLogo.setText("\uD83D\uDEE1\uFE0F")'
    $c3 = $c3 -replace ' \+ " .*?"\)', ' + " \uD83D\uDC4B")'
    $c3 = $c3 -replace 'new NeoButton\(".*? Refresh"', 'new NeoButton("\uD83D\uDD04 Refresh"'
    $c3 = $c3 -replace 'String\[\] icons = new String\[\] \{ .*? \};', 'String[] icons = new String[] { "\uD83D\uDC68\u200D\uD83D\uDCBB", "\uD83E\uDD16", "\u26A1", "\uD83D\uDCDD", "\uD83E\uDDEA", "\uD83D\uDCCA", "\uD83D\uDCBB" };'
    $c3 = $c3 -replace 'new BasicStroke\(2\)', 'new BasicStroke(3)'
    Set-Content -Path $p3 -Value $c3 -Encoding UTF8
}
