package server.service;

import server.utils.Helper;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TransportService {
    public void sendCharBufferData(Socket socket, String data) throws IOException {
        SocketChannel channel = socket.getChannel();
        // write to the stream
        try {
            Helper.logger.info("Send data to " + socket.getRemoteSocketAddress());
            channel.write(Charset.defaultCharset()
                    .encode(data));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendBufferData(Socket socket, ByteBuffer data) throws IOException {
        SocketChannel channel = socket.getChannel();
        // write to the stream
        try {
            Helper.logger.info("Send data to " + socket.getRemoteSocketAddress());
            channel.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
