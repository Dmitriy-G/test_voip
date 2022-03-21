package server.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.concurrent.RecursiveTask;

public class ReadNewInputDataTask extends RecursiveTask<CharBuffer> {

    private final SelectionKey key;

    public ReadNewInputDataTask(SelectionKey key) {
        this.key = key;
    }

    @Override
    protected CharBuffer compute() {
        // data is available for read
        // buffer for reading
        CharBuffer inputCommand = null;
        try {
            ByteBuffer buffer = ByteBuffer.allocate(100);
            SocketChannel clientChannel = (SocketChannel) key.channel();
            int bytesRead;
            if (key.isReadable()) {
                // the channel is non blocking so keep it open till the
                // count is >=0
                if ((bytesRead = clientChannel.read(buffer)) > 0) {
                    buffer.flip();
                    inputCommand = Charset.defaultCharset().decode(
                            buffer);
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
        } catch (
                IOException e) {
            e.printStackTrace();
        }
        return inputCommand;
    }
}
