package server.task;

import server.SpikeStorage;
import server.model.Client;
import server.model.ClientMessage;
import server.service.TransportService;

import java.io.IOException;
import java.net.Socket;
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
                transportService.sendBufferData(socket, clientMessage.getBody());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
