package server.utils;

import java.net.SocketAddress;
import java.util.HashMap;
import java.util.Map;

public class UserHelper {
    private static Map<SocketAddress, String> users = new HashMap<>();

    private UserHelper() {
    }

    public static boolean isUserNew(SocketAddress socketAddress) {
        return  users.get(socketAddress) == null;
    }

    public static void addNewUser(SocketAddress socketAddress) {
        System.out.printf("Client %s was connected%n", socketAddress);
        users.put(socketAddress, "");
    }

    public static Map<SocketAddress, String> getUsers() {
       return users;
    }

    public static void removeUser(SocketAddress socketAddress) {
        users.remove(socketAddress);
    }
}
