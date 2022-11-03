package server;

import client.AudioPlayer;
import client.Utils;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerApplication {
    public static void main(String[] args) {
        try {
            DatagramSocket socket = new DatagramSocket(8080, InetAddress.getLocalHost());

            //Helper.logger.info("Waiting for new datagrams on " + socket.getInetAddress());
            // new connection has been accepted
            while (true) {
                byte[] buffer = new byte[100000];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);

                AudioFormat audioFormat = Utils.getAudioFormat();

                DataLine.Info sourceDataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
                SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(sourceDataLineInfo);

                socket.send(packet);
            }

        } catch (IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}
