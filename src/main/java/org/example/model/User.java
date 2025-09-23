package org.example.model;

import java.io.Serializable;

public class User implements Serializable {
    private String username;
    private String ip;
    private int port; // port cho P2P (fixed 12346)

    public User(String username, String ip, int port) {
        this.username = username;
        this.ip = ip;
        this.port = port;
    }

    // Getters
    public String getUsername() { return username; }
    public String getIp() { return ip; }
    public int getPort() { return port; }
}