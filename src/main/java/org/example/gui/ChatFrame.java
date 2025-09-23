package org.example.gui;

import org.example.client.ChatClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.Collection;

public class ChatFrame extends JFrame {
    private ChatClient client;
    private boolean serverMode;
    private JList<String> friendsList = new JList<>();
    private JTextArea chatArea = new JTextArea();
    private JTextField input = new JTextField();
    private String currentTarget = null;

    public ChatFrame(ChatClient client, boolean serverMode) {
        this.client = client;
        this.serverMode = serverMode;

        // Tùy chỉnh giao diện Swing
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        setTitle("Chat App - " + client.getUsername() + " (" + (serverMode ? "Server Mode" : "P2P Mode") + ")");
        setSize(900, 700);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null); // Căn giữa màn hình
        setDefaultLookAndFeelDecorated(true);

        // Màu sắc gradient và hiện đại
        Color darkBackground = new Color(25, 28, 37);       // Deep Dark
        Color sidebarColor = new Color(153, 255, 246);         // Darker Sidebar
        Color chatBackground = new Color(116, 128, 183);       // Dark Chat Background
        Color primaryAccent = new Color(116, 225, 192);       // Rich Blue
        Color secondaryAccent = new Color(180, 255, 223);    // Teal Green
        Color dangerColor = new Color(225, 119, 119);         // Red
        Color textLight = new Color(229, 231, 235);         // Light Gray
        Color textDark = new Color(203, 213, 224);          // Soft White
        Color borderColor = new Color(158, 168, 201);          // Subtle Border

        Font mainFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font boldFont = new Font("Segoe UI", Font.BOLD, 16);

        getContentPane().setBackground(darkBackground);
        setFont(mainFont);

        // Panel phía Bắc với gradient
        JPanel northPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, primaryAccent, getWidth(), 0, primaryAccent.darker().darker());
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        northPanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        northPanel.setOpaque(false);

        JLabel label = new JLabel("👥 Friends Online");
        label.setForeground(textLight);
        label.setFont(boldFont);
        northPanel.add(label, BorderLayout.WEST);

        // Nút Logout với bo góc và shadow
        JButton logoutButton = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Shadow effect
                g2d.setColor(new Color(0, 0, 0, 50));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 15, 15);

                // Button background
                Color bgColor = getModel().isRollover() ? dangerColor.brighter() : dangerColor;
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Text
                g2d.setColor(textLight);
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2d.drawString(text, (getWidth() - textWidth) / 2, (getHeight() + textHeight) / 2 - 2);
            }
        };
        logoutButton.setForeground(textLight);
        logoutButton.setFont(mainFont);
        logoutButton.setFocusPainted(false);
        logoutButton.setBorderPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setPreferredSize(new Dimension(90, 40));
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.addActionListener(e -> {
            client.logout();
            dispose();
            System.exit(0);
        });
        logoutButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutButton.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                logoutButton.repaint();
            }
        });
        northPanel.add(logoutButton, BorderLayout.EAST);

        add(northPanel, BorderLayout.NORTH);

        // Panel chính với JSplitPane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setDividerLocation(250);
        splitPane.setBackground(darkBackground);
        splitPane.setBorder(null);
        splitPane.setDividerSize(2);

        // Phần bên trái: Danh sách bạn bè với bo góc và shadow
        JPanel leftPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(sidebarColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        leftPanel.setOpaque(false);
        leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Custom friends list với hover effect
        friendsList = new JList<String>() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        friendsList.setBackground(sidebarColor);
        friendsList.setForeground(textLight);
        friendsList.setFont(mainFont);
        friendsList.setSelectionBackground(primaryAccent);
        friendsList.setSelectionForeground(textLight);
        friendsList.setBorder(new EmptyBorder(10, 15, 10, 15));
        friendsList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setForeground(isSelected ? textLight : textLight);
                label.setBackground(isSelected ? primaryAccent : sidebarColor);
                label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                return label;
            }
        });
        friendsList.setOpaque(false);

        JScrollPane friendsScroll = new JScrollPane(friendsList);
        friendsScroll.setBackground(sidebarColor);
        friendsScroll.setBorder(null);
        friendsScroll.setOpaque(false);
        friendsScroll.getViewport().setOpaque(false);
        leftPanel.add(friendsScroll, BorderLayout.CENTER);

        splitPane.setLeftComponent(leftPanel);

        // Phần bên phải: Chat area với design chi tiết
        JPanel rightPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(chatBackground);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(new Color(0, 0, 0, 40));
                g2d.fillRoundRect(5, 5, getWidth() - 10, getHeight() - 10, 20, 20);
            }
        };
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // Chat area với style đẹp
        chatArea.setBackground(chatBackground);
        chatArea.setForeground(textDark);
        chatArea.setFont(mainFont);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setEditable(false);
        chatArea.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createLineBorder(borderColor, 1, true));
        chatScroll.getViewport().setBackground(chatBackground);
        rightPanel.add(chatScroll, BorderLayout.CENTER);

        // Input field với bo góc và icon
        JPanel inputPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2d.setColor(borderColor);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 25, 25);
            }
        };
        inputPanel.setOpaque(false);
        inputPanel.setBorder(new EmptyBorder(10, 15, 10, 15));

        JLabel sendIcon = new JLabel("📩");
        sendIcon.setForeground(secondaryAccent);
        sendIcon.setBorder(new EmptyBorder(0, 5, 0, 10));
        inputPanel.add(sendIcon, BorderLayout.EAST);

        input.setBackground(Color.WHITE);
        input.setForeground(textDark);
        input.setFont(mainFont);
        input.setBorder(new EmptyBorder(0, 10, 0, 0));
        input.setOpaque(false);

        input.addActionListener(e -> {
            String text = input.getText().trim();
            if (!text.isEmpty() && currentTarget != null) {
                client.sendMessage(currentTarget, text);
                chatArea.append("Me: " + text + "\n");
                input.setText("");
                chatArea.setCaretPosition(chatArea.getDocument().getLength());
            }
        });

        inputPanel.add(input, BorderLayout.CENTER);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);

        splitPane.setRightComponent(rightPanel);
        add(splitPane, BorderLayout.CENTER);

        friendsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendsList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = friendsList.getSelectedValue();
                if (selected != null && !selected.equals(client.getUsername())) {
                    currentTarget = selected.replace("👤 ", ""); // Loại bỏ icon khi gán
                    chatArea.setText("");
                    chatArea.append("💬 Chatting with " + currentTarget + "\n\n");
                    input.requestFocus();
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
        users.forEach(user -> model.addElement("👤 " + user));
        friendsList.setModel(model);
    }

    public void receiveMessage(String from, String content) {
        if (currentTarget != null && from.equals(currentTarget)) {
            chatArea.append(from + ": " + content + "\n");
            chatArea.setCaretPosition(chatArea.getDocument().getLength());
        }
    }
}