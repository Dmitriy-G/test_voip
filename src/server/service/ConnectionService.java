package server.service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ConnectionService {

    private final Selector selector;
    private final ClientService clientService;
    private final String serverName;
    private final int port;
    private final static String clientChannel = "clientChannel";
    private final static String serverChannel = "serverChannel";
    private final static String channelType = "channelType";

    public ConnectionService(Selector selector, ClientService clientService, String serverName, int port) {
        this.selector = selector;
        this.clientService = clientService;
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
                        newClientInputConnection(key);
                    } else {
                        newDataFromClientReceived(key);
                    }

                    // once a key is handled, it needs to be removed
                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void newClientInputConnection(SelectionKey key) throws IOException {
        System.out.println("New client was connected");
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
    }

    private void newDataFromClientReceived(SelectionKey key) throws IOException {
        // TODO: must working with new data in new thread for non blocking (thread pool)
        // data is available for read
        // buffer for reading
        ByteBuffer buffer = ByteBuffer.allocate(100);
        SocketChannel clientChannel = (SocketChannel) key.channel();
        int bytesRead;
        CharBuffer inputCommand = null;
        if (key.isReadable()) {
            // the channel is non blocking so keep it open till the
            // count is >=0
            if ((bytesRead = clientChannel.read(buffer)) > 0) {
                buffer.flip();
                inputCommand = Charset.defaultCharset().decode(
                        buffer);
                clientService.acceptInputData(clientChannel, inputCommand);
                buffer.clear();
            }
            // send result of the command for client
            //buffer.clear();

            if (bytesRead < 0) {
                // the key is automatically invalidated once the
                // channel is closed
                clientChannel.close();
            }
        }
    }
}
