package server.utils;

import server.SpikeStorage;
import server.model.ClientMessage;
import server.model.Client;

import java.util.Collection;

//param 0 - source guid
//param 1 - command type (like send or call)
//param 2 - command target (username or System)
//param 3 - command body (like a message or some options)
public class Helper {
    private Helper() {
    }

    public static ClientMessage commandResolver(String inputCommand) {
        String[] params = inputCommand.split(" ");
        String authorGuid = params[0];
        String commandType = params[1];
        switch (commandType) {
            case "message": {
                String target = nameToGuid(params[2]);
                String body = params[3];
                return new ClientMessage(
                        authorGuid,
                        target,
                        authorGuid,
                        body
                );
            }
        }

        //Fix potential npe
        return null;
    }

    private static String nameToGuid(String name) {
        Collection<Client> clients = SpikeStorage.users.values();
        return clients.stream().filter(e -> e.getUsername().equals(name)).findFirst().orElseThrow().getGuid();
    }

    //TODO: add validation
    private static String guidToName(String guid) {
        Client client = SpikeStorage.users.get(guid);
        if (client == null) {
            //TODO: implement design without exceptions
            throw new IllegalArgumentException();
        }
        return client.getUsername();
    }
}
