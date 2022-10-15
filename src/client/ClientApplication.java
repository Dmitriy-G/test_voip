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
import java.nio.ByteBuffer;

public class ClientApplication extends JFrame {
    private boolean stopCapture = false;
    private NetworkHelper networkHelper;
    private AudioPlayer audioPlayer;

    public static void main(String[] args) {
        String serverName = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            ClientApplication client = new ClientApplication(serverName, port);
            ReceiveAndPlayVoiceTask receiveAndPlayVoiceTask = new ReceiveAndPlayVoiceTask(client.audioPlayer, client.networkHelper.getSelector(), client.networkHelper.getChannel());
            receiveAndPlayVoiceTask.fork();
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
        this.audioPlayer = new AudioPlayer(audioFormat, sourceDataLine, byteArrayOutputStream);

        final JButton captureBtn = new JButton("Capture");
        final JButton stopBtn = new JButton("Stop");
        final JButton sendBtn = new JButton("Send");

        captureBtn.setEnabled(true);
        stopBtn.setEnabled(false);
        sendBtn.setEnabled(false);

        captureBtn.addActionListener(e -> {
                    captureBtn.setEnabled(false);
                    stopBtn.setEnabled(true);
                    sendBtn.setEnabled(false);
                    audioRecorder.record();
                }
        );
        getContentPane().add(captureBtn);

        stopBtn.addActionListener(e -> {
                    captureBtn.setEnabled(true);
                    stopBtn.setEnabled(false);
                    sendBtn.setEnabled(true);
                    stopCapture = true;
                }
        );
        getContentPane().add(stopBtn);

        sendBtn.addActionListener(
                e -> {
                    networkHelper.sendData(byteArrayOutputStream);
                    audioPlayer.play(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
                }
        );
        getContentPane().add(sendBtn);

        getContentPane().setLayout(
                new FlowLayout());
        setTitle("Test VOIP");
        setDefaultCloseOperation(
                EXIT_ON_CLOSE);
        setSize(450, 150);
        setVisible(true);
    }
}
