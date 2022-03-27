package server.task;

import server.SpikeStorage;
import server.model.Client;
import server.model.ClientMessage;
import server.service.TransportService;
import server.utils.Helper;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.RecursiveTask;

public class CreateNewConnectionTask extends RecursiveTask<Boolean> {

    private final Selector selector;
    private final SelectionKey key;
    private final String channelType;
    private final TransportService transportService;
    private final static String clientChannel = "clientChannel";

    public CreateNewConnectionTask(Selector selector, SelectionKey key, String channelType, TransportService transportService) {
        this.selector = selector;
        this.key = key;
        this.channelType = channelType;
        this.transportService = transportService;
    }

    @Override
    protected Boolean compute() {
        try {
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key
                    .channel();
            // this channel will return null if no client is connected.
            SocketChannel clientSocketChannel = serverSocketChannel
                    .accept();

            if (clientSocketChannel != null) {
                // set the client connection to be non blocking
                clientSocketChannel.configureBlocking(false);
                SelectionKey clientKey = clientSocketChannel.register(
                        selector, SelectionKey.OP_READ,
                        SelectionKey.OP_WRITE);
                Map<String, String> clientProperties = new HashMap<>();
                clientProperties.put(channelType, clientChannel);
                clientKey.attach(clientProperties);
            }
            //TODO: server must received from client his username
            //TODO: server must generated guid for client and send it in first message
            String guid = UUID.randomUUID().toString();
            Client client = new Client(
                    guid,
                    "",
                    clientSocketChannel.socket()
            );
            SpikeStorage.users.put(guid, client);
            Helper.logger.info("New client was connected in thread " + Thread.currentThread().getName());

            ClientMessage clientMessage = new ClientMessage(
                    "System",
                    guid,
                    "System",
                    guid
            );

            SendMessageToClientTask sendMessageToClientTask = new SendMessageToClientTask(transportService, clientMessage);
            sendMessageToClientTask.fork();
        } catch (IOException e) {
            //TODO: implement exceptions handler
            e.printStackTrace();
        }
        return true;
    }
}
