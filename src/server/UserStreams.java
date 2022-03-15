package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;

public class UserStreams {
    private DataInputStream in;
    private DataOutputStream out;

    public UserStreams(DataInputStream in, DataOutputStream out) {
        this.in = in;
        this.out = out;
    }

    public DataInputStream getIn() {
        return in;
    }

    public DataOutputStream getOut() {
        return out;
    }
}
