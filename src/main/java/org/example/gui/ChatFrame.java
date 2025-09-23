package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ChatFrame extends JFrame {
    private ChatClient client;
    private boolean serverMode;
    private JList<String> friendsList = new JList<>();
    private Map<String, PrivateChatFrame> privateChats = new HashMap<>();

    public ChatFrame(ChatClient client, boolean serverMode) {
        this.client = client;
        this.serverMode = serverMode;

        setTitle("Chat App - " + client.getUsername() + " (" + (serverMode ? "Server Mode" : "P2P Mode") + ")");
        setSize(600, 400);
        setLayout(new BorderLayout());

        // Panel phía Bắc cho nút Logout và nhãn
        JPanel northPanel = new JPanel(new BorderLayout());
        JLabel label = new JLabel("Friends Online:");
        northPanel.add(label, BorderLayout.CENTER);

        // Thêm nút Logout
        JButton logoutButton = new JButton("Logout");
        logoutButton.addActionListener(e -> {
            client.logout();
            dispose(); // Đóng cửa sổ hiện tại
            System.exit(0); // Thoát hoàn toàn ứng dụng
        });
        northPanel.add(logoutButton, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);

        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(friendsList), BorderLayout.CENTER);

        friendsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = friendsList.getSelectedValue();
                if (selected != null && !selected.equals(client.getUsername())) {
                    openPrivateChat(selected);
                }
            }
        });

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                client.logout();
            }
        });
        setVisible(true);
    }

    public void updateMode(boolean serverMode) {
        this.serverMode = serverMode;
        setTitle("Chat App - " + client.getUsername() + " (" + (serverMode ? "Server Mode" : "P2P Mode") + ")");
    }

    public void updateFriendsList(Collection<String> users) {
        DefaultListModel<String> model = new DefaultListModel<>();
        users.forEach(model::addElement);
        friendsList.setModel(model);
    }

    private void openPrivateChat(String target) {
        PrivateChatFrame frame = privateChats.get(target);
        if (frame == null) {
            frame = new PrivateChatFrame(client, target);
            privateChats.put(target, frame);
        }
        frame.setVisible(true);
    }

    public void receiveMessage(String from, String content) {
        PrivateChatFrame frame = privateChats.get(from);
        if (frame == null) {
            openPrivateChat(from);
            frame = privateChats.get(from);
        }
        if (frame != null) {
            frame.showMessage(from, content);
        }
    }

    public void appendLocalMessage(String to, String content) {
        PrivateChatFrame frame = privateChats.get(to);
        if (frame != null) {
            frame.appendLocal("Me: " + content);
        }
    }
}