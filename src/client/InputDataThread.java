package client;

import java.io.DataInputStream;
import java.io.IOException;

public class InputDataThread implements Runnable {
    private DataInputStream in;

    public InputDataThread(DataInputStream in) {
        this.in = in;
    }

    @Override
    public void run() {
        System.out.println("Processing input data started");
        try {
            while (true) {
                String[] serverData = in.readUTF().split("\\|");
                String command = serverData[0];
                String clientId = serverData[1];
                switch (command) {
                    case "inputMessage": {
                        String text = serverData[2];
                        System.out.println("\u001B[32m" + clientId + ": " + text + "\u001B[0m");
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
