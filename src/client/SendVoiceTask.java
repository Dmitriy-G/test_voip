package client;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.RecursiveTask;

public class SendVoiceTask extends RecursiveTask<Void> {

    private NetworkHelper networkHelper;
    private ByteArrayOutputStream byteArrayOutputStream;

    public SendVoiceTask(NetworkHelper networkHelper, ByteArrayOutputStream byteArrayOutputStream) {
        this.networkHelper = networkHelper;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    @Override
    protected Void compute() {
        while (true) {
            try {
                Thread.sleep(30000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            networkHelper.sendData(byteArrayOutputStream);
            byteArrayOutputStream.reset();
        }
    }
}
