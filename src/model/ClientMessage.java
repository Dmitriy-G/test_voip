package model;

public class ClientMessage {
    private String from;
    private String to;
    private String owner;
    private String body;

    public ClientMessage(String from, String to, String owner, String body) {
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

    public String getBody() {
        return body;
    }
}
