package org.example;

import org.example.gui.LoginFrame;

public class MainDemo {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
