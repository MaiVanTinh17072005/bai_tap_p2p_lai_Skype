package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PrivateChatFrame extends JFrame {
    private static final Map<String, PrivateChatFrame> openChats = new HashMap<>();

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
                chatArea.append("Me: " + text + "\n");
                input.setText("");
            }
        });

        openChats.put(targetUser, this);
        setVisible(true);
    }

    public static void showMessage(String from, String content) {
        PrivateChatFrame frame = openChats.get(from);
        if (frame != null) {
            frame.chatArea.append(from + ": " + content + "\n");
        } else {
            JOptionPane.showMessageDialog(null, from + ": " + content);
        }
    }
}
