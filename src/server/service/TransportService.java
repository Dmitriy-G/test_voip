package server.service;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class TransportService {
    public void sendCharBufferData(SocketChannel clientChannel, CharBuffer data) {
        CharBuffer writeBuffer = CharBuffer.wrap(data);
        while (writeBuffer.hasRemaining()) {
            try {
                clientChannel.write(Charset.defaultCharset()
                        .encode(writeBuffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
