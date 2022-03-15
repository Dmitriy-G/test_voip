package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.stream.Collectors;

public class ClientThread implements Runnable {
    private String clientGuid;
    private UserStreams streams;

    public ClientThread(String clientGuid, UserStreams streams) {
        this.clientGuid = clientGuid;
        this.streams = streams;
    }

    @Override
    public void run() {
        try {
            System.out.println("[" + Thread.currentThread().getId() + "]" + "Connection created for client " + this.clientGuid);
            DataInputStream in = streams.getIn();
            DataOutputStream out = streams.getOut();
            while (true) {
                String[] clientData = in.readUTF().split("\\|");
                String command = clientData[0];
                String clientId = clientData[1];
                switch (command) {
                    case "message" -> {
                        String target = clientData[2];
                        String message = clientData[3];
                        sendMessage(target, message);
                    }
                    case "users" -> {
                        String userList = SpikeStorage.clients.keySet().stream().filter( e -> !e.equals(clientId)).collect(Collectors.joining(","));
                        if (userList.length() == 0) {
                            System.out.println("Users list is empty");
                            break;
                        }
                        System.out.println("Users list " + userList + " was sent to " + clientId);
                        out.writeUTF("usersList|" + userList);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(String target, String text) {
        UserStreams targetUserStreams = SpikeStorage.clients.get(target);
        if (targetUserStreams == null) {
            System.out.println("Target " + target + " is incorrect");
        }
        DataOutputStream targetOutputStream = targetUserStreams.getOut();
        try {
            targetOutputStream.writeUTF("inputMessage|" + clientGuid + "|" + text);
            System.out.println("Message sent to " + target);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
