package server.service;

import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;

public class ClientService {

    private final TransportService transportService;

    public ClientService(TransportService transportService) {
        this.transportService = transportService;
    }

    public void acceptInputData(SocketChannel channel, CharBuffer inputCommand) {
        System.out.println(inputCommand);
        transportService.sendCharBufferData(channel, CharBuffer.wrap("------------------"));
        //Simulate large task
        try {
            System.out.println("Start large task");
            Thread.sleep(10000);
            System.out.println("Finish large task");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        transportService.sendCharBufferData(channel, inputCommand);
    }
}
