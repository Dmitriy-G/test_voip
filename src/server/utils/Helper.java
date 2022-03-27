package server.utils;

import server.SpikeStorage;
import server.model.ClientEvent;
import server.model.ClientMessage;
import server.model.Client;
import server.model.Command;

import java.util.Collection;
import java.util.logging.Logger;

public class Helper {

    public final static Logger logger = Logger.getLogger("Main logger");

    private Helper() {
    }

    public static ClientMessage commandResolver(String inputCommand) {
        String[] params = inputCommand.split(" ");
        Command command = createCommandFromClientData(params);
        switch (command.getEventType()) {
            case SEND_MESSAGE: {
                return new ClientMessage(
                        command.getSourceGuid(),
                        command.getTargetGuid(),
                        command.getSourceGuid(),
                        command.getBody()
                );
            }
            case REGISTER: {
                String guid = command.getSourceGuid();
                Client client = SpikeStorage.users.get(guid);
                client.setUsername(command.getBody());
                SpikeStorage.users.put(guid, client);

                command.setBody(guid);
                return new ClientMessage(
                        command.getSourceGuid(),
                        command.getTargetGuid(),
                        command.getSourceGuid(),
                        command.getBody()
                );
            }
        }

        //Fix potential npe
        return null;
    }

    private static String nameToGuid(String name) {
        Collection<Client> clients = SpikeStorage.users.values();
        Client client = clients.stream().filter(e -> e.getUsername().equals(name)).findFirst().orElse(null);
        if (client == null) {
            System.err.println("Client with username " + name + " not found!");
            return "";
        }
        return client.getGuid();
    }

    private static Command createCommandFromClientData(String[] data) {
        return new Command(
                data[0],
                ClientEvent.valueOf(data[1]),
                nameToGuid(data[2]),
                data[3]
        );
    }
}
