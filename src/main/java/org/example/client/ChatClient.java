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

    public ChatClient(String username) {
        this.username = username;
    }

    public void connect() throws Exception {
        socket = new Socket("localhost", 12345);
        out = new ObjectOutputStream(socket.getOutputStream());

        // gửi login
        send(new Message("LOGIN", username, null, null));

        chatFrame = new ChatFrame(this);
        new ClientHandler(socket, chatFrame).start();
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String to, String content) {
        send(new Message("MESSAGE", username, to, content));
    }

    public void logout() {
        send(new Message("LOGOUT", username, null, null));
        try { socket.close(); } catch (Exception ignored) {}
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
