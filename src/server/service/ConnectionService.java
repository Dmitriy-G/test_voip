package server.service;

import server.task.CreateNewConnectionTask;
import server.task.ReadNewInputDataTask;
import server.task.SendMessageToClientTask;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectionService {

    private final Selector selector;
    private final TransportService transportService;
    private final TaskService taskService;
    private final String serverName;
    private final int port;
    private final static String serverChannel = "serverChannel";
    private final static String channelType = "channelType";

    public ConnectionService(Selector selector, TransportService transportService, TaskService taskService, String serverName, int port) {
        this.selector = selector;
        this.transportService = transportService;
        this.taskService = taskService;
        this.serverName = serverName;
        this.port = port;
    }

    public ServerSocketChannel connect() {
        ServerSocketChannel channel = null;
        try {
            channel = ServerSocketChannel.open();
            channel.bind(new InetSocketAddress(serverName, port));
            channel.configureBlocking(false);

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
        //TODO: remove while true
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
                    if (((Map<String, String>) key.attachment()).get(channelType).equals(
                            serverChannel)) {
                        CreateNewConnectionTask createNewConnectionTask = new CreateNewConnectionTask(
                                selector,
                                key,
                                channelType
                        );
                        this.taskService.executeTask(createNewConnectionTask);
                        //join for waiting end this task. For prevent multiple running
                        //createNewConnectionTask.join();
                    } else {
                        ReadNewInputDataTask readNewDataTask = new ReadNewInputDataTask(key);
                        this.taskService.executeTask(readNewDataTask);
                        CharBuffer inputCommand = readNewDataTask.join();
                        SocketChannel channel = (SocketChannel) key.channel();
                        SendMessageToClientTask sendMessageToClientTask = new SendMessageToClientTask(transportService, channel, inputCommand);
                        this.taskService.executeTask(sendMessageToClientTask);
                    }

                    // once a key is handled, it needs to be removed
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
