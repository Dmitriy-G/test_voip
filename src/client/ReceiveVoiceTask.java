package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveTask;

public class ReceiveVoiceTask extends RecursiveTask<Boolean> {
    private DatagramSocket socket;
    private AudioPlayer audioPlayer;
    private Integer bufferSize;

    public ReceiveVoiceTask(DatagramSocket socket, AudioPlayer audioPlayer, Integer bufferSize) {
        this.socket = socket;
        this.audioPlayer = audioPlayer;
        this.bufferSize = bufferSize;
    }

    @Override
    protected Boolean compute() {
        while (true) {
            byte[] tempBuffer = new byte[bufferSize];
            try {
                DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length);
                socket.receive(packet);
                audioPlayer.play(ByteBuffer.wrap(tempBuffer));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
