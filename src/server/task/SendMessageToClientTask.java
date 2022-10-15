package server.task;

import server.SpikeStorage;
import server.model.Client;
import server.model.ClientMessage;
import server.service.TransportService;

import java.io.IOException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class SendMessageToClientTask extends RecursiveTask<Boolean>  {
    private final TransportService transportService;
    private final ClientMessage clientMessage;

    public SendMessageToClientTask(TransportService transportService, ClientMessage clientMessage) {
        this.transportService = transportService;
        this.clientMessage = clientMessage;
    }

    @Override
    protected Boolean compute() {
        //TODO: need resolve specific connection target for send this message
        for (Map.Entry<String, Client> entry : SpikeStorage.users.entrySet()) {
            Socket socket = entry.getValue().getSocket();
            try {
                if (socket != null) {
                    transportService.sendBufferData(socket, cloneData(clientMessage.getBody()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        clientMessage.getBody().clear();
        return true;
    }

    private ByteBuffer cloneData(ByteBuffer original) {
        ByteBuffer clone = ByteBuffer.allocate(original.capacity());
        original.rewind();//copy from the beginning
        clone.put(original);
        original.rewind();
        clone.flip();
        return clone;
    }
}
