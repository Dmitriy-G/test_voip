package server.service;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Set;

public class NetworkService {
    private DatagramSocket socket;

    public NetworkService(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendPacketToClients(DatagramPacket packet, Set<SocketAddress> sockets) {
        SocketAddress sourceSocketAddress = packet.getSocketAddress();
        sockets.stream()
                .filter(socketAddress -> !socketAddress.equals(sourceSocketAddress))
                .forEach(socketAddress -> {
                    packet.setSocketAddress(socketAddress);
                    sendPacketToClient(packet);
                });
    }

    private void sendPacketToClient(DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
