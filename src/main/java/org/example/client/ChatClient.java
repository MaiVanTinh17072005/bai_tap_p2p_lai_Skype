package org.example.client;

import org.example.model.Message;
import org.example.gui.ChatFrame;

import java.io.*;
import java.net.Socket;

public class ChatClient {
    private String username;
    private Socket socket;
    private ObjectOutputStream out;
    private ChatFrame chatFrame;
    private ClientHandler handler;
    private boolean serverMode = true; // mặc định có server

    public ChatClient(String username) {
        this.username = username;
    }

    public void connect() throws Exception {
        try {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            send(new Message("LOGIN", username, null, null));

            chatFrame = new ChatFrame(this, serverMode);
            handler = new ClientHandler(socket, chatFrame);
            handler.start();
        } catch (IOException e) {
            // server không chạy → P2P mode
            serverMode = false;
            System.out.println("⚠ Server offline → chuyển qua chế độ P2P");
            chatFrame = new ChatFrame(this, serverMode);

            // mở listener P2P
            new Thread(new PeerListener(chatFrame)).start();
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public void sendMessage(String to, String content) {
        if (serverMode) {
            send(new Message("MESSAGE", username, to, content));
        } else {
            PeerClient.send(to, username + ": " + content);
            chatFrame.receiveMessage(to, "Me: " + content);
        }
    }

    public void logout() {
        if (serverMode) {
            send(new Message("LOGOUT", username, null, null));
            try { if (handler != null) handler.stopHandler(); } catch (Exception ignored) {}
        }
        try { if (socket != null) socket.close(); } catch (Exception ignored) {}
    }

    private void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
