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
        boolean registered = false;
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

            while (true) {
                if (selector.select() == 0) {
                    continue;
                }
                Iterator<SelectionKey> registerIterator = selector.selectedKeys().iterator();

                while (registerIterator.hasNext()) {
                    SelectionKey key = registerIterator.next();
                    registerIterator.remove();
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(100);
                        channel.read(buffer);
                        buffer.flip();
                        clientGuid = String.valueOf(Charset.defaultCharset().decode(
                                buffer));
                        System.out.println(clientGuid);
                    }
                }
                break;
            }

            //Scanner in the loop for receive user commands via command line
            Scanner scanner = new Scanner(System.in);
            while (true) {
                // read command and send it to server
                if (!registered) {
                    System.out.println("Please write username");
                    String username = scanner.nextLine();
                    String command = clientGuid + " " + "REGISTER" + " " + "System" + " " + username;
                    CharBuffer  c = CharBuffer.wrap(command);
                    ByteBuffer b = StandardCharsets.ISO_8859_1.encode(c);
                    channel.write(b);
                    registered = true;
                    System.out.println("Success registration as " + username);
                    continue;
                }

                String line = scanner.nextLine();
                String command = clientGuid + " " +  line;
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
