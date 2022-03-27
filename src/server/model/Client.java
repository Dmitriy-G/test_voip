package server.model;

import java.net.Socket;

public class Client {
    private String guid;
    private String username;
    private Socket socket;

    public Client(String guid, String username, Socket socket) {
        this.guid = guid;
        this.username = username;
        this.socket = socket;
    }

    public String getGuid() {
        return guid;
    }

    public String getUsername() {
        return username;
    }

    public Socket getSocket() {
        return socket;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
