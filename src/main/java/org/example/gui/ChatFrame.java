package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChatFrame extends JFrame {
    private DefaultListModel<String> userListModel = new DefaultListModel<>();
    private JList<String> userList = new JList<>(userListModel);
    private ChatClient client;
    private boolean serverMode;

    public ChatFrame(ChatClient client, boolean serverMode) {
        this.client = client;
        this.serverMode = serverMode;

        setTitle("Chat - " + client.getUsername() + (serverMode ? " (Server Mode)" : " (P2P Mode)"));
        setSize(400, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        if (serverMode) {
            JPanel topPanel = new JPanel(new BorderLayout());
            topPanel.add(new JLabel("Online Users"), BorderLayout.NORTH);
            topPanel.add(new JScrollPane(userList), BorderLayout.CENTER);
            add(topPanel, BorderLayout.CENTER);

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
        } else {
            JPanel p2pPanel = new JPanel(new BorderLayout());
            JButton connectBtn = new JButton("Kết nối P2P");
            p2pPanel.add(connectBtn, BorderLayout.NORTH);

            connectBtn.addActionListener(e -> {
                String ip = JOptionPane.showInputDialog(this, "Nhập IP:Port (vd: 127.0.0.1:5000)");
                if (ip != null && !ip.isEmpty()) {
                    new PrivateChatFrame(client, ip);
                }
            });

            add(p2pPanel, BorderLayout.CENTER);
        }

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton logoutBtn = new JButton("Logout");
        bottomPanel.add(logoutBtn);
        add(bottomPanel, BorderLayout.SOUTH);

        logoutBtn.addActionListener(e -> {
            client.logout();
            PrivateChatFrame.closeAllChats();
            dispose();
            new LoginFrame();
        });

        setVisible(true);
    }

    public void updateUserList(String[] users) {
        if (!serverMode) return;
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
