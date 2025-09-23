package org.example.client;

import org.example.gui.ChatFrame;
import org.example.model.Message;
import org.example.model.User;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ChatClient {
    private String username;
    private Socket socket;
    private ObjectOutputStream out;
    private ChatFrame chatFrame;
    private ClientHandler handler;
    private boolean serverMode = true;
    private Map<String, User> peers = new HashMap<>();
    private int p2pPort;

    public ChatClient(String username) {
        this.username = username;
        this.p2pPort = 49152 + new Random().nextInt(16383);
    }

    public void connect() throws Exception {
        try {
            socket = new Socket("localhost", 12345);
            out = new ObjectOutputStream(socket.getOutputStream());
            send(new Message("LOGIN", username, null, socket.getLocalAddress().getHostAddress() + ":" + p2pPort));

            chatFrame = new ChatFrame(this, serverMode);
            handler = new ClientHandler(this, socket, chatFrame);
            handler.start();

            // Truyền username vào PeerListener
            new Thread(new PeerListener(chatFrame, p2pPort, username)).start();
        } catch (IOException e) {
            switchToP2PMode();
        }
    }

    // Trong sendMessage P2P, đảm bảo chỉ append local ở PrivateChatFrame (không gọi receiveMessage)
    public void sendMessage(String to, String content) {
        if (serverMode) {
            send(new Message("MESSAGE", username, to, content));
            // Append local sau gửi (sẽ được gọi từ ChatFrame.appendLocalMessage nếu cần, nhưng ở đây để PrivateChatFrame xử lý)
        } else {
            User target = peers.get(to);
            if (target != null) {
                String ipPort = target.getIp() + ":" + target.getPort();
                PeerClient.send(ipPort, username + ":" + content);
                // Không append ở đây, để PrivateChatFrame xử lý local
            } else {
                System.out.println("Không tìm thấy user " + to + " trong P2P");
            }
        }
    }

    public void switchToP2PMode() {
        serverMode = false;
        System.out.println("⚠ Server offline → chuyển qua chế độ P2P");
        if (chatFrame != null) {
            chatFrame.updateMode(serverMode);
        } else {
            chatFrame = new ChatFrame(this, serverMode);
        }
    }

    public String getUsername() {
        return username;
    }

    public boolean isServerMode() {
        return serverMode;
    }

    public Map<String, User> getPeers() {
        return peers;
    }

    public void updatePeers(Map<String, User> newPeers) {
        this.peers = newPeers;
        chatFrame.updateFriendsList(newPeers.keySet());
    }



    public void logout() {
        if (serverMode) {
            send(new Message("LOGOUT", username, null, null));
            try { handler.stopHandler(); } catch (Exception ignored) {}
        }
        try { socket.close(); } catch (Exception ignored) {}
    }

    private void send(Message msg) {
        try {
            out.writeObject(msg);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
            if (serverMode) switchToP2PMode();
        }
    }
}