package client;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class ClientApplication extends JFrame {
    private Boolean stopCapture = false;
    private AudioPlayer audioPlayer;

    public static void main(String[] args) {

        try {
            new ClientApplication();
        } catch (LineUnavailableException exception) {
            exception.printStackTrace();
        }
    }

    public ClientApplication() throws LineUnavailableException {
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


            AudioRecorder audioRecorder = new AudioRecorder(audioFormat, targetDataLine, stopCapture, socket, this.audioPlayer);

            ReceiveVoiceTask receiveVoiceTask = new ReceiveVoiceTask(socket, audioPlayer);
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
