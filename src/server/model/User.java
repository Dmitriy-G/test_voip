package server.model;

import java.net.SocketAddress;

public class User {
    private String guid;
    private String username;
    private SocketAddress socketAddress;

    public User(String guid, String username, SocketAddress socketAddress) {
        this.guid = guid;
        this.username = username;
        this.socketAddress = socketAddress;
    }
}
