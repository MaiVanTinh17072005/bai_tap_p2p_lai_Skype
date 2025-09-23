package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField = new JTextField(20);

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 150);
        setLayout(new FlowLayout());

        add(new JLabel("Username:"));
        add(usernameField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                try {
                    ChatClient client = new ChatClient(username);
                    client.connect();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Lỗi kết nối: " + ex.getMessage());
                }
            }
        });
        add(loginButton);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}