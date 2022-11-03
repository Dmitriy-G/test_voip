package client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class NetworkHelper {

    private String serverName;
    private Integer port;

    private SocketChannel channel;
    private Selector selector;

    public NetworkHelper(String serverName, Integer port) {
        this.serverName = serverName;
        this.port = port;
    }

    public String connect() {
        String clientGuid = "";

        try {
            channel = SocketChannel.open();
            selector = Selector.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(serverName, port));
            channel.register(selector, SelectionKey.OP_READ);
            while (!channel.finishConnect()) {
                System.out.println("still connecting");
            }
            System.out.println("Connect to " + channel.getRemoteAddress());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return clientGuid;
    }

    public void sendData(ByteArrayOutputStream byteArrayOutputStream) {
        ByteBuffer b = ByteBuffer.wrap(byteArrayOutputStream.toByteArray());
        try {
            channel.write(b);
        } catch (IOException e) {
            e.printStackTrace();
        }
        b.clear();
        //System.out.println("Data was send in thread " + Thread.currentThread().getName());
    }

    public SocketChannel getChannel() {
        return channel;
    }

    public Selector getSelector() {
        return selector;
    }
}
