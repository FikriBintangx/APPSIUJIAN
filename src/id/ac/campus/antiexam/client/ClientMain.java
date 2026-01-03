package id.ac.campus.antiexam.client;

import id.ac.campus.antiexam.ui.LoginFrame;

public class ClientMain {
    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
