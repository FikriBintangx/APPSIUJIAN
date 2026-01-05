package id.ac.campus.antiexam;

import id.ac.campus.antiexam.ui.ux.PilihPeranFrame;

public class AplikasiUjian {
    public static void main(String[] args) {
        // inisialisasi Database (create tables if not exist)
        id.ac.campus.antiexam.konfigurasi.InisialisasiDatabase.initialize();

        java.awt.EventQueue.invokeLater(() -> {
            new PilihPeranFrame().setVisible(true);
        });
    }
}
