package server.utils;

import server.model.User;

import javax.crypto.SecretKey;
import java.net.DatagramPacket;
import java.net.SocketAddress;
import java.security.PublicKey;
import java.util.HashMap;
import java.util.Map;

public class UserHelper {
    private static Map<SocketAddress, User> users = new HashMap<>();

    private UserHelper() {
    }

    public static boolean isUserNew(SocketAddress socketAddress) {
        //TODO: implement validation by username
        return users.get(socketAddress) == null;
    }

    public static void addNewUser(SocketAddress socketAddress, String username) {
        System.out.printf("Client %s was connected with username %s %n", socketAddress, username);
        User user = new User(username, false, null);
        users.put(socketAddress, user);
    }

    public static void saveRSAKey(SocketAddress socketAddress, DatagramPacket packet) {
        User user = users.get(socketAddress);
        if (user == null) {
            throw new RuntimeException(String.format("User with socket %s is null. Failed session initialization", socketAddress));
        }
        PublicKey publicKey = ServerSecurityHelper.excludePublicKey(packet);
        user.setPublicKey(publicKey);
    }

    public static void startUserSession(SocketAddress socketAddress, DatagramPacket packet) {
        User user = users.get(socketAddress);
        if (user == null) {
            throw new RuntimeException(String.format("User with socket %s is null. Failed session initialization", socketAddress));
        }
        /*SecretKey secretKey = ServerSecurityHelper.excludeSecretKey(packet);
        user.setSecretKey(secretKey);*/
        PublicKey publicKey = ServerSecurityHelper.excludePublicKey(packet);
        user.setPublicKey(publicKey);
        user.setActiveSession(true);
    }

    public static Map<SocketAddress, User> getUsers() {
       return users;
    }

    public static void removeUser(SocketAddress socketAddress) {
        users.remove(socketAddress);
    }
}
