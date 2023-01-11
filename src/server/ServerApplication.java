package server;

import server.service.NetworkService;
import server.utils.ServerSecurityHelper;
import server.utils.UserHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;

public class ServerApplication {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(3333);
            NetworkService networkService = new NetworkService(socket);
            while (true) {
                byte[] buffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                SocketAddress socketAddress = packet.getSocketAddress();
                System.out.println("Receive datagram from: " + socketAddress);

                boolean isNewUser = UserHelper.isUserNew(socketAddress);

                if (isNewUser) {
                    String username = new String(packet.getData(), StandardCharsets.UTF_8).replace("\u0000", "");
                    UserHelper.addNewUser(socketAddress, username);
                    ServerSecurityHelper.sendPublicKey(socket, socketAddress);
                    continue;
                }

                boolean isClientPublicKey = ServerSecurityHelper.isClientPublicKey(packet);

                if (isClientPublicKey) {
                    //UserHelper.saveRSAKey(socketAddress, packet);
                    UserHelper.startUserSession(socketAddress, packet);
                    continue;
                }

               /* boolean isClientAESKey = ServerSecurityHelper.isClientAESKey(packet);

                if (isClientAESKey) {
                    UserHelper.startUserSession(socketAddress, packet);
                    continue;
                }*/

                //System.out.println("Send datagram to: " + socket);

                networkService.sendPacketToClients(packet, UserHelper.getUsers());
                //socket.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
