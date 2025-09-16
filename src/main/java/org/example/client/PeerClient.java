package org.example.client;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class PeerClient {
    public static void send(String ipPort, String msg) {
        try {
            String[] parts = ipPort.split(":");
            String ip = parts[0];
            int port = Integer.parseInt(parts[1]);

            Socket socket = new Socket(ip, port);
            PrintWriter out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            out.println(msg);
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
