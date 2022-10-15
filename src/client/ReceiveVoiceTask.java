package client;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.concurrent.RecursiveTask;

public class ReceiveVoiceTask extends RecursiveTask<Void> {
    private AudioPlayer audioPlayer;
    private Selector selector;
    private SocketChannel channel;

    public ReceiveVoiceTask(AudioPlayer audioPlayer, Selector selector, SocketChannel channel) {
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

                System.out.println("Received data from: " + channel.socket().getRemoteSocketAddress());

                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();

                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    if (key.isReadable()) {
                        ByteBuffer buffer = ByteBuffer.allocate(20000);

                        if (channel.read(buffer) > 0) {
                            buffer.flip();
                            audioPlayer.play(buffer);
                        }
                        // send result of the command for client
                        buffer.clear();
                    }

                    iterator.remove();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
