package server.model;

//Client events for event bus
public enum ClientEvent {
    //User was sent message to other user (or group of users)
    SEND_MESSAGE,
    //User was registered
    REGISTER,
    //User will be disconnect from the server
    DISCONNECT
}
