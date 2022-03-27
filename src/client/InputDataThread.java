package client;

import server.utils.Helper;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.logging.Logger;

public class InputDataThread implements Runnable {
    private DataInputStream in;

    public InputDataThread(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        Helper.logger.info("Processing input data started");
        //TODO: needed to subscribe to event bus here and listen for new message (in the loop?)
        try {
            while (true) {
                String[] serverData = in.readUTF().split("\\|");
                String command = serverData[0];
                String clientId = serverData[1];
                switch (command) {
                    case "inputMessage": {
                        String text = serverData[2];
                        Helper.logger.info("\u001B[32m" + clientId + ": " + text + "\u001B[0m");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
