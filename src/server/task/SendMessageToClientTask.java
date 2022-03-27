package server.task;

import server.SpikeStorage;
import server.model.ClientMessage;
import server.service.TransportService;

import java.io.IOException;
import java.net.Socket;
import java.nio.CharBuffer;
import java.util.concurrent.RecursiveTask;

public class SendMessageToClientTask extends RecursiveTask<Boolean>  {
    private TransportService transportService;
    private ClientMessage clientMessage;

    public SendMessageToClientTask(TransportService transportService, ClientMessage clientMessage) {
        this.transportService = transportService;
        this.clientMessage = clientMessage;
    }

    @Override
    protected Boolean compute() {
        System.out.println(clientMessage.getBody());
        //TODO: need resolve specific connection target for send this message
        Socket socket = SpikeStorage.users.get("1").getSocket();
        try {
            transportService.sendCharBufferData(socket, clientMessage.getBody());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
