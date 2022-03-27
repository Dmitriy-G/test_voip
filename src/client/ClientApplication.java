package client;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class ClientApplication {
    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);
        String clientGuid = "";
        try {
            SocketChannel channel = SocketChannel.open();
            Selector selector = Selector.open();
            channel.configureBlocking(false);
            channel.connect(new InetSocketAddress(serverName, port));
            channel.register(selector, SelectionKey.OP_READ);
            while (!channel.finishConnect()) {
                System.out.println("still connecting");
            }
            System.out.println("Connect to " + channel.getRemoteAddress());
            //register(out, clientGuid);

            //New thread for async listen event bus (new message from server)
            //new Thread(new InputDataThread(in)).start();

            //TODO: before send any commands client must waiting for it's guid
            clientGuid = "1";

            //Scanner in the loop for receive user commands via command line
            Scanner scanner = new Scanner(System.in);
            while (true) {
                // read command and send it to server
                String command = clientGuid + " " +  scanner.nextLine();
                CharBuffer  c = CharBuffer.wrap(command);
                ByteBuffer b = StandardCharsets.ISO_8859_1.encode(c);
                channel.write(b);
                System.out.println("Command " + command + " was send");

                // read response from server with result of this command
                // TODO: select() can block! But we need wait server response for testing it
                // TODO: need listener
                if (selector.select() == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        channel.read(buffer);
                        buffer.flip();
                        System.out.println(Charset.defaultCharset().decode(
                                buffer));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
