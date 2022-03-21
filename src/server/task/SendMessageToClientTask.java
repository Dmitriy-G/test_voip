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
        transportService.sendCharBufferData(channel, CharBuffer.wrap("------------------"));
        //Simulate large task
        try {
            System.out.println("Start large task in tread " + Thread.currentThread().getName());
            Thread.sleep(10000);
            System.out.println("Finish large task in thread " + Thread.currentThread().getName());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transportService.sendCharBufferData(channel, inputCommand);
        return true;
    }
}
