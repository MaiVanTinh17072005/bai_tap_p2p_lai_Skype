package org.example.server;

import org.example.model.Message;
import org.example.model.User;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class ChatServer {
    private static final int PORT = 12345;
    private Map<String, ObjectOutputStream> clients = new HashMap<>();
    private Map<String, User> users = new HashMap<>();

    public void start() {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server running on port " + PORT);
            while (true) {
                Socket socket = serverSocket.accept();
                new ClientThread(socket).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ClientThread extends Thread {
        private Socket socket;

        public ClientThread(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                while (true) {
                    Message msg = (Message) in.readObject();
                    switch (msg.getType()) {
                        case "LOGIN":
                            String username = msg.getFrom();
                            String[] ipPort = msg.getContent().split(":");
                            String ip = ipPort[0];
                            int p2pPort = Integer.parseInt(ipPort[1]);
                            users.put(username, new User(username, ip, p2pPort));
                            clients.put(username, out);
                            broadcastUserList();
                            break;
                        case "LOGOUT":
                            users.remove(msg.getFrom());
                            clients.remove(msg.getFrom());
                            broadcastUserList(); // Broadcast danh sách mới sau khi logout
                            socket.close();
                            return;
                        case "MESSAGE":
                            ObjectOutputStream targetOut = clients.get(msg.getTo());
                            if (targetOut != null && !msg.getFrom().equals(msg.getTo())) {
                                targetOut.writeObject(msg);
                                targetOut.flush();
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void broadcastUserList() {
        StringBuilder content = new StringBuilder();
        for (Map.Entry<String, User> entry : users.entrySet()) {
            User u = entry.getValue();
            content.append(u.getUsername()).append("=").append(u.getIp()).append(":").append(u.getPort()).append(";");
        }
        Message listMsg = new Message("USER_LIST", null, null, content.toString());
        for (ObjectOutputStream out : clients.values()) {
            try {
                out.writeObject(listMsg);
                out.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new ChatServer().start();
    }
}