package server;

import server.service.NetworkService;
import server.utils.UserHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;

public class ServerApplication {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(8080);
            NetworkService networkService = new NetworkService(socket);
            while (true) {
                byte[] buffer = new byte[100000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                SocketAddress socketAddress = packet.getSocketAddress();
                System.out.println("Receive datagram from: " + socketAddress);

                boolean isNewUser = UserHelper.isUserNew(socketAddress);

                if (isNewUser) {
                    UserHelper.addNewUser(socketAddress);
                }

                //System.out.println("Send datagram to: " + socket);

                networkService.sendPacketToClients(packet, UserHelper.getUsers().keySet());
                //socket.send(packet);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
