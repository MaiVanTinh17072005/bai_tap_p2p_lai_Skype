package org.example.client;

import org.example.gui.ChatFrame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class PeerListener implements Runnable {
    private ChatFrame chatFrame;

    public PeerListener(ChatFrame chatFrame) {
        this.chatFrame = chatFrame;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(5000)) {
            System.out.println("[P2P] Listening on port 5000...");
            while (true) {
                Socket socket = serverSocket.accept();
                new Thread(() -> handlePeer(socket)).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handlePeer(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                chatFrame.receiveMessage("Peer", msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
