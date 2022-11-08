package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class AudioRecorder {

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private Boolean stopCapture;
    private DatagramSocket socket;
    private String host;
    private Integer port;
    private Integer bufferSize;

    public AudioRecorder(AudioFormat audioFormat, TargetDataLine targetDataLine, Boolean stopCapture, DatagramSocket socket, String host, Integer port, Integer bufferSize) {
        this.audioFormat = audioFormat;
        this.targetDataLine = targetDataLine;
        this.stopCapture = stopCapture;
        this.socket = socket;
        this.host = host;
        this.port = port;
        this.bufferSize = bufferSize;
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
        byte[] tempBuffer = new byte[bufferSize];
        public void run() {
            stopCapture = false;
            try {
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length, InetAddress.getByName(host), port);
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
