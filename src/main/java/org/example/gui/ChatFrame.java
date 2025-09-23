package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.util.Collection;

public class ChatFrame extends JFrame {
    private ChatClient client;
    private boolean serverMode;
    private JList<String> friendsList = new JList<>();
    private JTextArea chatArea = new JTextArea();
    private JTextField input = new JTextField();
    private String currentTarget = null; // Lưu bạn chat hiện tại

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
            dispose();
            System.exit(0);
        });
        northPanel.add(logoutButton, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);

        // Panel chính: friendsList bên trái, chatArea và input bên phải
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(150); // Độ rộng danh sách bạn bè

        // Phần bên trái: Danh sách bạn bè
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.add(new JScrollPane(friendsList), BorderLayout.CENTER);
        splitPane.setLeftComponent(leftPanel);

        // Phần bên phải: Chat area và input
        JPanel rightPanel = new JPanel(new BorderLayout());
        chatArea.setEditable(false);
        rightPanel.add(new JScrollPane(chatArea), BorderLayout.CENTER);
        rightPanel.add(input, BorderLayout.SOUTH);

        input.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty() && currentTarget != null) {
                client.sendMessage(currentTarget, text);
                chatArea.append("Me: " + text + "\n");
                input.setText("");
            }
        });

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = friendsList.getSelectedValue();
                if (selected != null && !selected.equals(client.getUsername())) {
                    currentTarget = selected; // Cập nhật bạn chat hiện tại
                    chatArea.setText(""); // Xóa chat area để bắt đầu với bạn mới
                    chatArea.append("Chatting with " + currentTarget + "\n");
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

    public void receiveMessage(String from, String content) {
        if (currentTarget != null && from.equals(currentTarget)) { // Chỉ hiển thị nếu đang chat với người gửi
            chatArea.append(from + ": " + content + "\n");
        }
    }

}