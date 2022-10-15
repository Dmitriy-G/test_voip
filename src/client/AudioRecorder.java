package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.TargetDataLine;
import java.io.ByteArrayOutputStream;

public class AudioRecorder {

    private AudioFormat audioFormat;
    private TargetDataLine targetDataLine;
    private Boolean stopCapture;
    private ByteArrayOutputStream byteArrayOutputStream;

    public AudioRecorder(AudioFormat audioFormat, TargetDataLine targetDataLine, Boolean stopCapture, ByteArrayOutputStream byteArrayOutputStream) {
        this.audioFormat = audioFormat;
        this.targetDataLine = targetDataLine;
        this.stopCapture = stopCapture;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public void record() {
        try {
            targetDataLine.open(audioFormat);
            targetDataLine.start();

            Thread captureThread = new Thread(new CaptureThread());
            captureThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class CaptureThread extends Thread {

        byte[] tempBuffer = new byte[10000];

        public void run() {
            stopCapture = false;
            try {
                while (!stopCapture) {
                    int cnt = targetDataLine.read(tempBuffer, 0, tempBuffer.length);
                    if (cnt > 0) {
                        byteArrayOutputStream.write(tempBuffer, 0, cnt);
                    }
                }
                //byteArrayOutputStream.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
