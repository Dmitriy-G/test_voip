package server.model;

import java.nio.ByteBuffer;

public class ClientMessage {
    private String from;
    private String to;
    private String owner;
    private ByteBuffer body;

    public ClientMessage(String from, String to, String owner, ByteBuffer body) {
        this.from = from;
        this.to = to;
        this.owner = owner;
        this.body = body;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getOwner() {
        return owner;
    }

    public ByteBuffer getBody() {
        return body;
    }
}
