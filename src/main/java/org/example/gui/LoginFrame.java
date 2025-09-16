package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {
    private JTextField usernameField = new JTextField(15);

    public LoginFrame() {
        setTitle("Login");
        setSize(300, 150);
        setLayout(new FlowLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        add(new JLabel("Username:"));
        add(usernameField);

        JButton loginButton = new JButton("Login");
        add(loginButton);

        loginButton.addActionListener(e -> {
            String username = usernameField.getText().trim();
            if (!username.isEmpty()) {
                try {
                    ChatClient client = new ChatClient(username);
                    client.connect();
                    dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Cannot connect");
                }
            }
        });

        setVisible(true);
    }
}
