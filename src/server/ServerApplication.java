package server;

import server.service.ConnectionService;
import server.service.ClientService;
import server.service.TransportService;

import java.io.IOException;
import java.nio.channels.Selector;

public class ServerApplication {
    public static void main(String[] args) {
        try {
            String serverName = args[0];
            int port = Integer.parseInt(args[1]);
            Selector selector = Selector.open();
            TransportService transportService = new TransportService();
            ClientService clientService = new ClientService(transportService);
            ConnectionService connectionService = new ConnectionService(
                    selector,
                    clientService,
                    serverName,
                    port
            );
            connectionService.connect();
            connectionService.listenNewClientsCommand();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
