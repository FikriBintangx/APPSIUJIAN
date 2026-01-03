package id.ac.campus.antiexam;

import id.ac.campus.antiexam.ui.RoleChooserFrame;

public class Main {
    public static void main(String[] args) {
        // Initialize Database (create tables if not exist)
        id.ac.campus.antiexam.config.DatabaseSetup.initialize();

        java.awt.EventQueue.invokeLater(() -> {
            new RoleChooserFrame().setVisible(true);
        });
    }
}
