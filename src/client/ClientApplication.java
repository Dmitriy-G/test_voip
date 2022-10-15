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

public class ClientApplication extends JFrame {
    private Boolean stopCapture = false;
    private NetworkHelper networkHelper;
    private AudioPlayer audioPlayer;

    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            new ClientApplication(serverName, port);
        } catch (LineUnavailableException exception) {
            exception.printStackTrace();
        }
    }

    public ClientApplication(String serverName, Integer port) throws LineUnavailableException {
        this.networkHelper = new NetworkHelper(serverName, port);
        this.networkHelper.connect();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        AudioFormat audioFormat = Utils.getAudioFormat();

        DataLine.Info targetDataLineInfo = new DataLine.Info(
                TargetDataLine.class,
                audioFormat);
        TargetDataLine targetDataLine = (TargetDataLine)
                AudioSystem.getLine(
                        targetDataLineInfo);

        DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);

        AudioRecorder audioRecorder = new AudioRecorder(audioFormat, targetDataLine, stopCapture, byteArrayOutputStream);
        this.audioPlayer = new AudioPlayer(audioFormat, sourceDataLine);

        final JButton captureBtn = new JButton("Start");
        final JButton stopBtn = new JButton("Stop");

        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);

        captureBtn.addActionListener(e -> {
                    captureBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    audioRecorder.record();

//                    SendVoiceTask sendVoiceTask = new SendVoiceTask(networkHelper, byteArrayOutputStream);
//                    sendVoiceTask.fork();

                    ReceiveVoiceTask receiveVoiceTask = new ReceiveVoiceTask(audioPlayer, networkHelper.getSelector(), networkHelper.getChannel());
                    receiveVoiceTask.fork();
                }
        );
        getContentPane().add(captureBtn);

        stopBtn.addActionListener(e -> {
                    captureBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                    audioRecorder.stopCapture();
                    networkHelper.sendData(byteArrayOutputStream);
                    byteArrayOutputStream.reset();
                }
        );
        getContentPane().add(stopBtn);

        getContentPane().setLayout(
                new FlowLayout());
        setTitle("Test VOIP");
        setDefaultCloseOperation(
                EXIT_ON_CLOSE);
        setSize(450, 150);
        setVisible(true);
    }
}
