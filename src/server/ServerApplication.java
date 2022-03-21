package server;

import server.service.ConnectionService;
import server.service.TaskService;
import server.service.TransportService;

import java.io.IOException;
import java.nio.channels.Selector;
import java.util.concurrent.ForkJoinPool;

public class ServerApplication {
    public static void main(String[] args) {
        try {
            String serverName = args[0];
            int port = Integer.parseInt(args[1]);
            Selector selector = Selector.open();
            TransportService transportService = new TransportService();
            ForkJoinPool forkJoinPool = new ForkJoinPool();
            TaskService taskService = new TaskService(forkJoinPool);
            ConnectionService connectionService = new ConnectionService(
                    selector,
                    transportService,
                    taskService,
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
