package org.example.client;

import org.example.gui.ChatFrame;
import org.example.model.Message;
import org.example.model.User;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ClientHandler extends Thread {
    private ChatClient client;
    private Socket socket;
    private ChatFrame chatFrame;
    private ObjectInputStream in;
    private boolean running = true;

    public ClientHandler(ChatClient client, Socket socket, ChatFrame chatFrame) {
        this.client = client;
        this.socket = socket;
        this.chatFrame = chatFrame;
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Message msg = (Message) in.readObject();
                switch (msg.getType()) {
                    case "MESSAGE":
                        String from = msg.getFrom();
                        if (!from.equals(client.getUsername())) { // Bỏ qua nếu từ chính mình (tránh duplicate)
                            chatFrame.receiveMessage(from, msg.getContent());
                        }
                        break;
                    case "USER_LIST":
                        Map<String, User> peers = new HashMap<>();
                        String[] users = msg.getContent().split(";");
                        for (String u : users) {
                            if (!u.isEmpty()) {
                                String[] parts = u.split("=");
                                if (parts.length == 2) {
                                    String[] ipPort = parts[1].split(":");
                                    peers.put(parts[0], new User(parts[0], ipPort[0], Integer.parseInt(ipPort[1])));
                                }
                            }
                        }
                        client.updatePeers(peers);
                        break;
                }
            } catch (Exception e) {
                if (running) {
                    System.out.println("Mất kết nối server → switch P2P");
                    client.switchToP2PMode();
                    running = false;
                }
            }
        }
    }

    public void stopHandler() {
        running = false;
        try { in.close(); } catch (Exception ignored) {}
    }
}