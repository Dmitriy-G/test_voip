package server.task;

import server.service.TransportService;

import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.RecursiveTask;

public class SendMessageToClientTask extends RecursiveTask<Boolean>  {
    private TransportService transportService;
    private SocketChannel channel;
    private CharBuffer inputCommand;

    public SendMessageToClientTask(TransportService transportService, SocketChannel channel, CharBuffer inputCommand) {
        this.transportService = transportService;
        this.channel = channel;
        this.inputCommand = inputCommand;
    }

    @Override
    protected Boolean compute() {
        System.out.println(inputCommand);
        transportService.sendCharBufferData(channel, inputCommand);
        return true;
    }
}
