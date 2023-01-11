package client;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.concurrent.RecursiveTask;

public class ReceivePacketsTask extends RecursiveTask<Boolean> {
    private DatagramSocket socket;
    private String host;
    private int port;
    private AudioPlayer audioPlayer;
    private Integer bufferSize;

    public ReceivePacketsTask(DatagramSocket socket, String host, int port, AudioPlayer audioPlayer, Integer bufferSize) {
        this.socket = socket;
        this.host = host;
        this.port = port;
        this.audioPlayer = audioPlayer;
        this.bufferSize = bufferSize;
    }

    @Override
    protected Boolean compute() {
        ArrayList<Byte> fullAudio = new ArrayList<>();
        while (true) {
            try {
                byte[] tempBuffer = new byte[1000];
                DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length);
                socket.receive(packet);

                if (ClientSecurityHelper.isRSAPublicKey(packet)) {
                    ClientSecurityHelper.SERVER_PUBLIC_KEY = ClientSecurityHelper.excludePublicKey(packet);
                    ClientSecurityHelper.sendPublicKey(socket, host, port);
                    ClientSecurityHelper.IS_ACKNOWLEDGE_PROCESS_FINISHED = true;
                } else {
                    byte[] soundRecordBuffer = new byte[256];
                    System.arraycopy(tempBuffer, 0, soundRecordBuffer, 0, 256);

                    for (byte b: tempBuffer) {
                        fullAudio.add(b);
                    }

                    if (fullAudio.size() > 100000) {
                        audioPlayer.play(ByteBuffer.wrap(ClientSecurityHelper.decryptMessage(soundRecordBuffer)));
                        fullAudio.clear();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
