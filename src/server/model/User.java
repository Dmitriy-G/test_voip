package server.model;

import javax.crypto.SecretKey;
import java.security.PublicKey;

public class User {
    private String name;
    private boolean isActiveSession;
    //RSA
    private PublicKey publicKey;

    public User(String name, boolean isActiveSession, PublicKey publicKey) {
        this.name = name;
        this.isActiveSession = isActiveSession;
        this.publicKey = publicKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActiveSession() {
        return isActiveSession;
    }

    public void setActiveSession(boolean activeSession) {
        isActiveSession = activeSession;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
