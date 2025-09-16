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
        setSize(400, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== Panel trên hiển thị danh sách user online =====
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(new JLabel("Online Users"), BorderLayout.NORTH);
        topPanel.add(new JScrollPane(userList), BorderLayout.CENTER);

        // ===== Panel dưới có nút Logout =====
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        bottomPanel.add(logoutBtn);

        add(topPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // ===== Event double click vào user để mở chat riêng =====
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

        // ===== Event nút Logout =====
        logoutBtn.addActionListener(e -> {
            client.logout();
            PrivateChatFrame.closeAllChats(); // đóng hết cửa sổ chat riêng
            dispose(); // đóng cửa sổ ChatFrame
            new LoginFrame(); // quay về login
        });

        setVisible(true);
    }

    // Cập nhật danh sách online
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

    // Nhận tin nhắn từ user khác
    public void receiveMessage(String from, String content) {
        PrivateChatFrame.showMessage(from, content);
    }
}
