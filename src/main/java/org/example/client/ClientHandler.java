package org.example.client;

import org.example.model.Message;
import org.example.gui.ChatFrame;

import java.io.ObjectInputStream;
import java.net.Socket;

public class ClientHandler extends Thread {
    private Socket socket;
    private ChatFrame chatFrame;
    private boolean running = true;

    public ClientHandler(Socket socket, ChatFrame chatFrame) {
        this.socket = socket;
        this.chatFrame = chatFrame;
    }

    @Override
    public void run() {
        try (ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {
            while (running) {
                Message msg = (Message) in.readObject();
                switch (msg.getType()) {
                    case "USER_LIST":
                        chatFrame.updateUserList(msg.getContent().split(","));
                        break;
                    case "MESSAGE":
                        chatFrame.receiveMessage(msg.getFrom(), msg.getContent());
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("ClientHandler stopped.");
        }
    }

    public void stopHandler() {
        running = false;
        try { socket.close(); } catch (Exception ignored) {}
    }
}
