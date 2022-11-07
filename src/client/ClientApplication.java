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

public class ClientApplication extends JFrame {
    private Boolean stopCapture = false;
    private AudioPlayer audioPlayer;

    public static void main(String[] args) {
        try {
            String host = args[0];
            Integer port = Integer.parseInt(args[1]);
            new ClientApplication(host, port);
        } catch (LineUnavailableException exception) {
            exception.printStackTrace();
        }
    }

    public ClientApplication(String host, Integer port) throws LineUnavailableException {
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

            int bufferSize = 10000;
            AudioRecorder audioRecorder = new AudioRecorder(audioFormat, targetDataLine, stopCapture, socket, host, port, bufferSize);

            ReceiveVoiceTask receiveVoiceTask = new ReceiveVoiceTask(socket, audioPlayer, bufferSize);
            receiveVoiceTask.fork();

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
