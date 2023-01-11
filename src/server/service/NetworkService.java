package server.service;

import server.model.User;
import server.utils.ServerSecurityHelper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.Map;

public class NetworkService {
    private DatagramSocket socket;

    public NetworkService(DatagramSocket socket) {
        this.socket = socket;
    }

    public void sendPacketToClients(DatagramPacket packet, Map<SocketAddress, User> users) {
        SocketAddress sourceSocketAddress = packet.getSocketAddress();
        byte[] soundRecordBuffer = new byte[256];
        System.arraycopy(packet.getData(), 0, soundRecordBuffer, 0, 256);
        byte[] decryptedData = ServerSecurityHelper.decryptRSAMessage(soundRecordBuffer);

        for (Map.Entry<SocketAddress, User> entry: users.entrySet()) {
            SocketAddress address = entry.getKey();
            User user = entry.getValue();
            //if (!address.equals(sourceSocketAddress)) {
                packet.setData(ServerSecurityHelper.encryptRSAMessage(decryptedData, user.getPublicKey()));
                packet.setSocketAddress(address);
                sendPacketToClient(packet);
            //}
        }
    }

    private void sendPacketToClient(DatagramPacket packet) {
        try {
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
