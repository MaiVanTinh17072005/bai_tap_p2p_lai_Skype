package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ChatFrame extends JFrame {
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> userList = new JList<>(userListModel);
    private ChatClient client;

    public ChatFrame(ChatClient client) {
        this.client = client;
        setTitle("Chat - " + client.getUsername());
        setSize(400, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        add(new JLabel("Online Users"), BorderLayout.NORTH);
        add(new JScrollPane(userList), BorderLayout.CENTER);

        userList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    String selectedUser = userList.getSelectedValue();
                    if (selectedUser != null) {
                        new PrivateChatFrame(client, selectedUser);
                    }
                }
            }
        });

        setVisible(true);
    }

    public void updateUserList(String[] users) {
        SwingUtilities.invokeLater(() -> {
            userListModel.clear();
            for (String u : users) {
                if (!u.isEmpty() && !u.equals(client.getUsername())) {
                    userListModel.addElement(u);
                }
            }
        });
    }

    public void receiveMessage(String from, String content) {
        PrivateChatFrame.showMessage(from, content);
    }
}
