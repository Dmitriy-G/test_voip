package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.ByteBuffer;

public class AudioRecorder {

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private Boolean stopCapture;
    private DatagramSocket socket;
    private AudioPlayer audioPlayer;

    public AudioRecorder(AudioFormat audioFormat, TargetDataLine targetDataLine, Boolean stopCapture, DatagramSocket socket, AudioPlayer audioPlayer) {
        this.audioFormat = audioFormat;
        this.targetDataLine = targetDataLine;
        this.stopCapture = stopCapture;
        this.socket = socket;
        this.audioPlayer = audioPlayer;
    }

    public void record() {
        try {
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class CaptureThread extends Thread {

        byte[] tempBuffer = new byte[10000];

        public void run() {
            stopCapture = false;
            try {
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length, InetAddress.getLocalHost(), 8080);
                    if (cnt > 0) {
                        socket.send(packet);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
