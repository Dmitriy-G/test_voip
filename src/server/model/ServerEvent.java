package server.model;

//Server events for event bus
public enum ServerEvent {
    //If new user connect to the server all client must see it
    CONNECT_USER,
    //If user left the server all client must see it
    DISCONNECT_USER,
    //If user send message, target clients must be get it
    SEND_MESSAGE
}
