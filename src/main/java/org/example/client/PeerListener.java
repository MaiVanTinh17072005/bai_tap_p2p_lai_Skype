package org.example.client;

import org.example.gui.ChatFrame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerListener implements Runnable {
    private ChatFrame chatFrame;
    private int port;
    private String myUsername; // Thêm để kiểm tra

    public PeerListener(ChatFrame chatFrame, int port, String myUsername) { // Thêm param username
        this.chatFrame = chatFrame;
        this.port = port;
        this.myUsername = myUsername;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("P2P listener running on port " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = in.readLine();
                String[] parts = msg.split(":", 2);
                if (parts.length == 2) {
                    String from = parts[0].trim();
                    String content = parts[1].trim();
                    if (!from.equals(myUsername)) { // Bỏ qua nếu từ chính mình
                        chatFrame.receiveMessage(from, content);
                    }
                }
                socket.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}