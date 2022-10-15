package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.RecursiveTask;

public class ReceiveAndPlayVoiceTask extends RecursiveTask<Void> {
    private AudioPlayer audioPlayer;
    private Selector selector;
    private SocketChannel channel;

    public ReceiveAndPlayVoiceTask(AudioPlayer audioPlayer, Selector selector, SocketChannel channel) {
        this.audioPlayer = audioPlayer;
        this.selector = selector;
        this.channel = channel;
    }

    @Override
    protected Void compute() {
        while (true) {
            try {
                if (selector.select() == 0) {
                    continue;
                }
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                int bytesRead;

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(10000);

                        if ((bytesRead = channel.read(buffer)) > 0) {
                            buffer.flip();
                            audioPlayer.play(buffer);
                        }
                        // send result of the command for client
                        buffer.clear();
                        System.out.println(bytesRead);
                        /*if (bytesRead < 0) {
                            // the key is automatically invalidated once the
                            // channel is closed
                            client.close();
                        }*/
                    }

                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
