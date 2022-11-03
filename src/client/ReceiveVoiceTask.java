package client;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.concurrent.RecursiveTask;

public class ReceiveVoiceTask extends RecursiveTask<Boolean> {
    private DatagramSocket socket;
    private AudioPlayer audioPlayer;

    public ReceiveVoiceTask(DatagramSocket socket, AudioPlayer audioPlayer) {
        this.socket = socket;
        this.audioPlayer = audioPlayer;
    }

    @Override
    protected Boolean compute() {
        while (true) {
            byte[] tempBuffer = new byte[10000];
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
