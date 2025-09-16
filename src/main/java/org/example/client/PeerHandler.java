package org.example.client;

import java.io.*;
import java.net.*;

public class PeerHandler implements Runnable {
    private Socket socket;

    public PeerHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            String msg;
            while ((msg = in.readLine()) != null) {
                System.out.println("[P2P] " + msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
