package server.task;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.concurrent.RecursiveTask;

public class ReadNewInputDataTask extends RecursiveTask<ByteBuffer> {

    private final SelectionKey key;

    public ReadNewInputDataTask(SelectionKey key) {
        this.key = key;
    }

    @Override
    protected ByteBuffer compute() {
        // data is available for read
        // buffer for reading
        ByteBuffer data = null;
        try {
            int bufferSize = 20000;
            ByteBuffer buffer = ByteBuffer.allocate(bufferSize);
            SocketChannel clientChannel = (SocketChannel) key.channel();
            int bytesRead;
            if (key.isReadable()) {
                // the channel is non blocking so keep it open till the
                // count is >=0
                if ((bytesRead = clientChannel.read(buffer)) > 0) {
                    buffer.flip();
                    data = buffer;
                    buffer.clear();
                }
                // send result of the command for client
                buffer.clear();
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
        return data;
    }
}
