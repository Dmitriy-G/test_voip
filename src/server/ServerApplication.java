package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerApplication {
    private static final int SERVER_PORT = 50066;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(SERVER_PORT);
            System.out.println("Waiting client on port " +
                    serverSocket.getLocalPort() + "...");
            while (true) {
                Socket server = serverSocket.accept();
                System.out.println("Connect to " + server.getRemoteSocketAddress());
                DataInputStream in = new DataInputStream(server.getInputStream());
                DataOutputStream out = new DataOutputStream(server.getOutputStream());
                String[] clientData = in.readUTF().split("\\|");
                String newClientGuid = clientData[1];
                UserStreams streams = new UserStreams(in, out);
                SpikeStorage.clients.put(newClientGuid, streams);
                System.out.println("User " + newClientGuid + " was registered");
                //out.writeUTF("Success connect to " + server.getLocalSocketAddress());
                new Thread(new ClientThread(newClientGuid, streams)).start();
            }

            /*System.out.println(in.readUTF());
            DataOutputStream out = new DataOutputStream(server.getOutputStream());
            out.writeUTF("Success connect to " + server.getLocalSocketAddress());
            server.close();*/
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
