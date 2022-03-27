package server.service;

import java.io.IOException;
import java.net.Socket;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TransportService {
    public void sendCharBufferData(Socket socket, String data) throws IOException {
        SocketChannel channel = socket.getChannel();
        // write to the stream
        try {
            System.out.println("Send data to " + socket.getRemoteSocketAddress());
            channel.write(Charset.defaultCharset()
                    .encode(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
