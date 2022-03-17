package client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;

public class ClientApplication {
    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        String clientGuid = UUID.randomUUID().toString();
        try {
            System.out.println("Connect to " + serverName + " port " + port);
            Socket client = new Socket(serverName, port);

            System.out.println("Connect to " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            InputStream inFromServer = client.getInputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            DataInputStream in = new DataInputStream(inFromServer);
            register(out, clientGuid);

            //New thread for async listen event bus (new message from server)
            new Thread(new InputDataThread(in)).start();

            //Scanner in the loop for receive user commands via command line
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String[] commandArgs = scanner.nextLine().split(" ");
                String commandType = commandArgs[0];
                switch (commandType) {
                    case "message" -> {
                        String target = commandArgs[1];
                        String text = commandArgs[2];
                        doChat(out, clientGuid, target, text);
                    }
                    case "users" -> {
                        requestUsers(in, out, clientGuid);
                    }
                    case "exit" -> {
                        out.writeUTF("Client " + clientGuid + " disconnected");
                        client.close();
                        return;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Initial new message event
    public static void doChat(DataOutputStream outputStream, String userGuid, String destination, String message) throws IOException {
        outputStream.writeUTF("message|" + userGuid + "|" + destination + "|" + message);
    }

    //Initial new user event
    public static void register(DataOutputStream outputStream, String userGuid) throws IOException {
        System.out.println("Registration event send");
        outputStream.writeUTF("register|" + userGuid);
    }

    //Request users list
    @Deprecated
    public static List<String> requestUsers(DataInputStream inputStream, DataOutputStream outputStream, String userGuid) throws IOException {
        System.out.println("Users list was requested");
        outputStream.writeUTF("users|" + userGuid);
        String id = "";
        while (true) {
            String[] serverResponse = inputStream.readUTF().split("\\|");
            id = serverResponse[0];
            if (id.equals("usersList")) {
                String data = serverResponse[1];
                System.out.println("User list received " + data);
                return Arrays.asList(data.split(",").clone());
            } else {
                System.out.println("Waiting user list...");
            }
        }
    }
}
