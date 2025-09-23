package org.example.gui;
import org.example.client.ChatClient;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

public class LoginFrame extends JFrame {
    private JTextField usernameField = new JTextField(20);

    public LoginFrame() {
        // Màu sắc gradient đẹp
        Color backgroundColor = new Color(234, 199, 146);      // Blue gradient start
        Color backgroundEnd = new Color(143, 148, 251);       // Blue gradient end
        Color panelColor = new Color(255, 255, 255, 240);     // Semi-transparent white
        Color accentColor = new Color(99, 102, 241);          // Indigo accent
        Color textColor = new Color(55, 65, 81);              // Dark gray text
        Color labelColor = new Color(107, 114, 128);          // Gray label
        Color buttonColor = new Color(34, 197, 94);           // Emerald green
        Color buttonHover = new Color(16, 185, 129);          // Emerald hover

        setTitle("Login");
        setSize(1000, 600);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Custom background với gradient
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gradient = new GradientPaint(0, 0, backgroundColor, getWidth(), getHeight(), backgroundEnd);
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        backgroundPanel.setLayout(new GridBagLayout());

        // Panel chính với bo góc
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Bóng đổ
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(5, 5, getWidth() - 5, getHeight() - 5, 25, 25);

                // Background panel
                g2d.setColor(panelColor);
                g2d.fillRoundRect(0, 0, getWidth() - 5, getHeight() - 5, 25, 25);
            }
        };
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setPreferredSize(new Dimension(400, 300));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 20, 15, 20);

        // Title
        JLabel titleLabel = new JLabel("Welcome Back");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textColor);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);

        JLabel subtitleLabel = new JLabel("Please sign in to your account");
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        subtitleLabel.setForeground(labelColor);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);

        // Label Username với style đẹp
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setForeground(textColor);
        usernameLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));

        // TextField với bo góc
        usernameField = new JTextField(20) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Background với bo góc
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Border
                g2d.setColor(hasFocus() ? accentColor : new Color(209, 213, 219));
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 15, 15);

                super.paintComponent(g);
            }
        };
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        usernameField.setBackground(Color.WHITE);
        usernameField.setForeground(textColor);
        usernameField.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
        usernameField.setPreferredSize(new Dimension(300, 50));
        usernameField.setOpaque(false);

        // Button với bo góc và gradient
        JButton loginButton = new JButton("Sign In") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Button background với gradient
                Color startColor = getModel().isPressed() ? buttonHover.darker() :
                        (getModel().isRollover() ? buttonHover : buttonColor);
                Color endColor = startColor.darker();

                GradientPaint gradient = new GradientPaint(0, 0, startColor, 0, getHeight(), endColor);
                g2d.setPaint(gradient);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                // Button text
                g2d.setColor(getForeground());
                g2d.setFont(getFont());
                FontMetrics fm = g2d.getFontMetrics();
                String text = getText();
                int textWidth = fm.stringWidth(text);
                int textHeight = fm.getAscent();
                g2d.drawString(text,
                        (getWidth() - textWidth) / 2,
                        (getHeight() + textHeight) / 2 - 2);
            }
        };
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        loginButton.setForeground(Color.WHITE);
        loginButton.setBorderPainted(false);
        loginButton.setFocusPainted(false);
        loginButton.setContentAreaFilled(false);
        loginButton.setPreferredSize(new Dimension(300, 50));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hiệu ứng hover cho button
        loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                loginButton.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                loginButton.repaint();
            }
        });

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

        // Layout components
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 20, 25, 20);
        mainPanel.add(subtitleLabel, gbc);

        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 20, 5, 20);
        mainPanel.add(usernameLabel, gbc);

        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(0, 20, 20, 20);
        mainPanel.add(usernameField, gbc);

        gbc.gridy = 4;
        gbc.insets = new Insets(10, 20, 20, 20);
        mainPanel.add(loginButton, gbc);

        // Thêm panel vào background
        backgroundPanel.add(mainPanel);
        add(backgroundPanel, BorderLayout.CENTER);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
}