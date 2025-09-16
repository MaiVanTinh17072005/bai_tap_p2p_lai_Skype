package org.example.server;

import org.example.model.Message;

import java.io.*;
import java.net.*;
import java.util.*;

public class ChatServer {
    private static Map<String, ObjectOutputStream> clients = new HashMap<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(12345);
        System.out.println("Server started on port 12345...");

        while (true) {
            Socket socket = serverSocket.accept();
            new Thread(() -> handleClient(socket)).start();
        }
    }

    private static void handleClient(Socket socket) {
        try (
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())
        ) {
            String username = null;
            while (true) {
                Message msg = (Message) in.readObject();
                switch (msg.getType()) {
                    case "LOGIN":
                        username = msg.getFrom();
                        clients.put(username, out);
                        broadcastUserList();
                        break;
                    case "LOGOUT":
                        clients.remove(msg.getFrom());
                        broadcastUserList();
                        return;
                    case "MESSAGE":
                        ObjectOutputStream targetOut = clients.get(msg.getTo());
                        if (targetOut != null) {
                            targetOut.writeObject(msg);
                            targetOut.flush();
                        }
                        break;
                }
            }
        } catch (Exception e) {
            System.out.println("Client disconnected.");
        }
    }

    private static void broadcastUserList() throws IOException {
        String users = String.join(",", clients.keySet());
        Message msg = new Message("USER_LIST", "server", null, users);
        for (ObjectOutputStream out : clients.values()) {
            out.writeObject(msg);
            out.flush();
        }
    }
}
