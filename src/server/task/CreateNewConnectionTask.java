package server.task;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class CreateNewConnectionTask extends RecursiveTask<Boolean> {

    private final Selector selector;
    private final SelectionKey key;
    private final String channelType;
    private final static String clientChannel = "clientChannel";

    public CreateNewConnectionTask(Selector selector, SelectionKey key, String channelType) {
        this.selector = selector;
        this.key = key;
        this.channelType = channelType;
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
            System.out.println("New client was connected in thread " + Thread.currentThread().getName());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
