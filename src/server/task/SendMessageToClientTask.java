package server.task;

import server.service.TransportService;

import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.util.concurrent.RecursiveTask;

public class SendMessageToClientTask extends RecursiveTask<Boolean> {
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
        long start = System.currentTimeMillis();
        System.out.println("Start large task in tread " + Thread.currentThread().getName());
        // wait(10000);
        System.out.println("Finish large task in thread " + Thread.currentThread().getName() + " for " + (System.currentTimeMillis() - start) + "ms");
        transportService.sendCharBufferData(channel, inputCommand);
        return true;
    }
}
