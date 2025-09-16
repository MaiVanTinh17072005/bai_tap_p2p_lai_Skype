package org.example.model;

import java.io.Serializable;

public class Message implements Serializable {
    private String type;   // "LOGIN", "LOGOUT", "MESSAGE", "USER_LIST"
    private String from;
    private String to;
    private String content;

    public Message(String type, String from, String to, String content) {
        this.type = type;
        this.from = from;
        this.to = to;
        this.content = content;
    }

    public String getType() { return type; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getContent() { return content; }
}
