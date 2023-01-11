package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.DatagramSocket;
import java.util.concurrent.ThreadLocalRandom;

public class ClientApplication extends JFrame {
    private Boolean stopCapture = false;
    private AudioPlayer audioPlayer;

    public static void main(String[] args) {
        try {
            String host = args[0];
            Integer port = Integer.parseInt(args[1]);
            //String username = args[2];
            String username = String.valueOf(ThreadLocalRandom.current().nextInt(1, 1000));
            new ClientApplication(host, port, username);
        } catch (LineUnavailableException exception) {
            exception.printStackTrace();
        }
    }

    public ClientApplication(String host, Integer port, String username) throws LineUnavailableException {
        AudioFormat audioFormat = Utils.getAudioFormat();

        DataLine.Info targetDataLineInfo = new DataLine.Info(
                TargetDataLine.class,
                audioFormat);
        TargetDataLine targetDataLine = (TargetDataLine)
                AudioSystem.getLine(
                        targetDataLineInfo);

        DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);

        this.audioPlayer = new AudioPlayer(audioFormat, sourceDataLine);


        try {
            //0 - is random port
            DatagramSocket socket = new DatagramSocket(0);

            int outputBufferSize = 244;
            AudioRecorder audioRecorder = new AudioRecorder(audioFormat, targetDataLine, stopCapture, socket, host, port, outputBufferSize);

            ReceivePacketsTask receivePacketsTask = new ReceivePacketsTask(socket, host, port, audioPlayer, outputBufferSize);
            receivePacketsTask.fork();

            ClientSecurityHelper.sendInitialPackage(socket, host, port, username);

            audioRecorder.record();

            getContentPane().setLayout(
                    new FlowLayout());
            setTitle("Test VOIP");
            setDefaultCloseOperation(
                    EXIT_ON_CLOSE);
            setSize(450, 150);
            setVisible(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
