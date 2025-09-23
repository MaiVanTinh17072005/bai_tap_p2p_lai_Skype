package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;

public class PrivateChatFrame extends JFrame {
    private JTextArea chatArea = new JTextArea();
    private JTextField input = new JTextField();
    private ChatClient client;
    private String targetUser;

    public PrivateChatFrame(ChatClient client, String targetUser) {
        this.client = client;
        this.targetUser = targetUser;

        setTitle("Chat with " + targetUser);
        setSize(400, 300);
        setLayout(new BorderLayout());

        chatArea.setEditable(false);
        add(new JScrollPane(chatArea), BorderLayout.CENTER);
        add(input, BorderLayout.SOUTH);

        input.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty()) {
                client.sendMessage(targetUser, text);
                appendLocal("Me: " + text); // Append local ở đây
                input.setText("");
            }
        });

        setVisible(true);
    }

    public void showMessage(String from, String content) {
        chatArea.append(from + ": " + content + "\n");
    }

    // Phương thức append local cho tin gửi
    public void appendLocal(String message) {
        chatArea.append(message + "\n");
    }
}