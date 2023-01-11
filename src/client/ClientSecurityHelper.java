package client;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

//TODO: Merge server and client RSA helpers and add as a library
public class ClientSecurityHelper {

    private static final KeyPair keyPair;
    private static final int PUBLIC_KEY_PREFIX_SIZE = 50;
    private static final int PUBLIC_KEY_PREFIX_NUMBER = -2;
    private static final byte[] PUBLIC_KEY_PREFIX = new byte[PUBLIC_KEY_PREFIX_SIZE];
    public static PublicKey SERVER_PUBLIC_KEY;
    public static boolean IS_ACKNOWLEDGE_PROCESS_FINISHED = false;

    static  {
        KeyPairGenerator generator = null;
        try {
            generator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(generator).initialize(2048);
        keyPair = generator.generateKeyPair();
        Arrays.fill(PUBLIC_KEY_PREFIX, (byte) PUBLIC_KEY_PREFIX_NUMBER);
    }

    public static void sendInitialPackage(DatagramSocket socket, String host, int port, String username) {
        byte[] usernameBytes = username.getBytes(StandardCharsets.UTF_8);
        try {
            DatagramPacket packet = new DatagramPacket(usernameBytes, usernameBytes.length, InetAddress.getByName(host), port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendPublicKey(DatagramSocket socket, String host, int port) {
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] tempBuffer = new byte[PUBLIC_KEY_PREFIX_SIZE + publicKey.length];
        System.arraycopy(PUBLIC_KEY_PREFIX, 0, tempBuffer, 0, PUBLIC_KEY_PREFIX_SIZE);
        System.arraycopy(publicKey, 0, tempBuffer, PUBLIC_KEY_PREFIX_SIZE, publicKey.length);

        try {
            DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length, InetAddress.getByName(host), port);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isRSAPublicKey(DatagramPacket packet) {
        byte[] data = packet.getData();
        for (int i = 0; i < PUBLIC_KEY_PREFIX_SIZE; i++) {
            if (data[i] != PUBLIC_KEY_PREFIX_NUMBER) {
                return false;
            }
        }
        return true;
    }

    public static PublicKey excludePublicKey(DatagramPacket packet) {
        byte[] data = packet.getData();
        int dataSize = data.length - PUBLIC_KEY_PREFIX_SIZE;
        byte[] encodedPubKey = new byte[dataSize];
        System.arraycopy(data, PUBLIC_KEY_PREFIX_SIZE, encodedPubKey, 0, dataSize);

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedPubKey);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error while public key deserialization");
    }

    public static byte[] decryptMessage(byte[] encryptedMessage) {
        Cipher encryptCipher;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            return encryptCipher.doFinal(encryptedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error while data decrypted");
    }

    public static byte[] encryptMessage(byte[] decryptedMessage) {
        Cipher encryptCipher;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            if (SERVER_PUBLIC_KEY == null) {
                throw new RuntimeException("Server public key is null");
            }
            encryptCipher.init(Cipher.ENCRYPT_MODE, SERVER_PUBLIC_KEY);
            return encryptCipher.doFinal(decryptedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error while data encrypted");
    }
}
