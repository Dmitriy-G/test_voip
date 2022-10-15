package server.service;

import server.model.ClientMessage;
import server.task.CreateNewConnectionTask;
import server.task.ReadNewInputDataTask;
import server.task.SendMessageToClientTask;
import server.utils.Helper;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectionService {

    private final Selector selector;
    private final TransportService transportService;
    private final String serverName;
    private final int port;
    private final static String serverChannel = "serverChannel";
    private final static String channelType = "channelType";

    public ConnectionService(Selector selector, TransportService transportService, String serverName, int port) {
        this.selector = selector;
        this.transportService = transportService;
        this.serverName = serverName;
        this.port = port;
    }

    public ServerSocketChannel connect() {
        ServerSocketChannel channel = null;
        try {
            channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(serverName, port));
            channel.configureBlocking(false);

            Helper.logger.info("Waiting for new connection on " + channel.getLocalAddress());
            // new connection has been accepted
            SelectionKey socketServerSelectionKey = channel.register(selector,
                    SelectionKey.OP_ACCEPT);

            Map<String, String> properties = new HashMap<>();
            properties.put(channelType, serverChannel);
            socketServerSelectionKey.attach(properties);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return channel;
    }

    public void listenNewClientsCommand() {
        while (true) {
            // check if client connected
            try {
                if (selector.select() == 0)
                    continue;
                // the select method returns with a list of selected keys
                Set<SelectionKey> selectedKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectedKeys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (((Map<String, String>) key.attachment()).get(channelType).equals(serverChannel)) {

                        CreateNewConnectionTask createNewConnectionTask = new CreateNewConnectionTask(
                                selector,
                                key,
                                channelType,
                                transportService
                        );
                        createNewConnectionTask.fork();
                        //join for waiting end this task. For prevent multiple running
                        boolean isConnected = createNewConnectionTask.join();
                        if (!isConnected)
                            Helper.logger.warning("New client don't connections");
                    } else {
                        ReadNewInputDataTask readNewDataTask = new ReadNewInputDataTask(key);
                        readNewDataTask.fork();
                        ByteBuffer inputCommand = readNewDataTask.join();
                        if (inputCommand == null) {
                            continue;
                        }
                        //ClientMessage clientMessage = Helper.commandResolver(inputCommand.toString());
                        ClientMessage clientMessage = new ClientMessage("", "", "", inputCommand);

                        SendMessageToClientTask sendMessageToClientTask = new SendMessageToClientTask(transportService, clientMessage);
                        sendMessageToClientTask.fork();
                    }
                    // once a key is handled, it needs to be removed
                    iterator.remove();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
