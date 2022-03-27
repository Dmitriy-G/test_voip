package server.utils;

import server.SpikeStorage;
import server.model.ClientEvent;
import server.model.ClientMessage;
import server.model.Client;
import server.model.Command;

import java.util.Collection;

public class Helper {
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
        }

        //Fix potential npe
        return null;
    }

    private static String nameToGuid(String name) {
        Collection<Client> clients = SpikeStorage.users.values();
        return clients.stream().filter(e -> e.getUsername().equals(name)).findFirst().orElseThrow().getGuid();
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
