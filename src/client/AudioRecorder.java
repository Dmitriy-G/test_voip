package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

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
                    if (ClientSecurityHelper.IS_ACKNOWLEDGE_PROCESS_FINISHED) {
                        int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                        byte[] encryptedBytes = ClientSecurityHelper.encryptMessage(tempBuffer);
                        DatagramPacket packet = new DatagramPacket(encryptedBytes, encryptedBytes.length, InetAddress.getByName(host), port);
                        if (cnt > 0) {
                            socket.send(packet);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
