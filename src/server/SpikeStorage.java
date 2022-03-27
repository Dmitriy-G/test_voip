package server;

import server.model.Client;

import java.util.HashMap;
import java.util.Map;

public class SpikeStorage {
    public static Map<String, Client> users = new HashMap<>();

    static {
        String system = "System";
        Client systemUser = new Client(
                system,
                system,
                null
        );
        users.put(system, systemUser);
    }
}
