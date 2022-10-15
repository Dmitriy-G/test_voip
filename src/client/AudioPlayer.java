package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class AudioPlayer {

    private AudioFormat audioFormat;
    private SourceDataLine sourceDataLine;
    private ByteArrayOutputStream byteArrayOutputStream;

    public AudioPlayer(AudioFormat audioFormat, SourceDataLine sourceDataLine, ByteArrayOutputStream byteArrayOutputStream) {
        this.audioFormat = audioFormat;
        this.sourceDataLine = sourceDataLine;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    public void play(ByteBuffer byteBuffer) {
        try {
            byte audioData[] = byteBuffer.array();

            InputStream byteArrayInputStream = new ByteArrayInputStream(audioData);
            AudioInputStream audioInputStream = new AudioInputStream(byteArrayInputStream, audioFormat, audioData.length / audioFormat.getFrameSize());

            sourceDataLine.open(audioFormat);
            sourceDataLine.start();

            Thread playThread = new Thread(new PlayThread(audioInputStream));
            playThread.start();
        } catch (Exception e) {
            System.out.println(e);
            System.exit(0);
        }
    }

    class PlayThread extends Thread {
        private AudioInputStream audioInputStream;

        byte[] tempBuffer = new byte[10000];

        public PlayThread(AudioInputStream audioInputStream) {
            this.audioInputStream = audioInputStream;
        }

        public void run() {
            try {
                int cnt;

                while ((cnt = audioInputStream.read(tempBuffer, 0, tempBuffer.length)) != -1) {
                    if (cnt > 0) {
                        sourceDataLine.write(tempBuffer, 0, cnt);
                    }
                }
                sourceDataLine.drain();
                sourceDataLine.close();
            } catch (Exception e) {
                System.out.println(e);
                System.exit(0);
            }
        }
    }
}
