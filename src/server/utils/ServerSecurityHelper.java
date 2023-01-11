package server.utils;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

//TODO: Merge server and client RSA helpers and add as a library
public class ServerSecurityHelper {

    private static final KeyPair keyPair;
    private static final int KEYS_PREFIX_SIZE = 50;
    private static final int PUBLIC_KEY_PREFIX_NUMBER = -2;
    private static final byte[] PUBLIC_KEY_PREFIX = new byte[KEYS_PREFIX_SIZE];

    static  {
        //Generate RSA key
        KeyPairGenerator generatorRSA = null;
        try {
            generatorRSA = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(generatorRSA).initialize(2048);
        keyPair = generatorRSA.generateKeyPair();

        /*Cipher encryptCipher;
        Cipher decryptCipher;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
            byte[] enc = encryptCipher.doFinal("test".getBytes(StandardCharsets.UTF_8));

            decryptCipher = Cipher.getInstance("RSA");
            decryptCipher.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
            byte[] dec = decryptCipher.doFinal(enc);
            System.out.println();

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }*/

        Arrays.fill(PUBLIC_KEY_PREFIX, (byte) PUBLIC_KEY_PREFIX_NUMBER);

        //Generate AES key
        KeyGenerator generatorAES = null;
        try {
            generatorAES = KeyGenerator.getInstance("AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(generatorAES).init(128);
        Arrays.fill(PUBLIC_KEY_PREFIX, (byte) PUBLIC_KEY_PREFIX_NUMBER);
    }

    public static boolean isClientPublicKey(DatagramPacket packet) {
        byte[] data = packet.getData();
        for (int i = 0; i < KEYS_PREFIX_SIZE; i++) {
            if (data[i] != PUBLIC_KEY_PREFIX_NUMBER) {
                return false;
            }
        }
        return true;
    }

    public static void sendPublicKey(DatagramSocket socket, SocketAddress socketAddress) {
        byte[] publicKey = keyPair.getPublic().getEncoded();
        byte[] tempBuffer = new byte[KEYS_PREFIX_SIZE + publicKey.length];
        System.arraycopy(PUBLIC_KEY_PREFIX, 0, tempBuffer, 0, KEYS_PREFIX_SIZE);
        System.arraycopy(publicKey, 0, tempBuffer, KEYS_PREFIX_SIZE, publicKey.length);

        try {
            DatagramPacket packet = new DatagramPacket(tempBuffer, tempBuffer.length, socketAddress);
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PublicKey excludePublicKey(DatagramPacket packet) {
        byte[] data = packet.getData();
        int dataSize = data.length - KEYS_PREFIX_SIZE;
        byte[] encodedPubKey = new byte[dataSize];
        System.arraycopy(data, KEYS_PREFIX_SIZE, encodedPubKey, 0, dataSize);

        X509EncodedKeySpec pubKeySpec = new X509EncodedKeySpec(encodedPubKey);
        KeyFactory keyFactory;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(pubKeySpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        throw new RuntimeException("Error while RSA key deserialization");
    }

    public static byte[] decryptRSAMessage(byte[] encryptedMessage) {
        Cipher decryptCipher;
        try {
            decryptCipher = Cipher.getInstance("RSA");
            PrivateKey privateKey = keyPair.getPrivate();
            if (privateKey == null) {
                throw new RuntimeException("Server private key is null");
            }
            decryptCipher.init(Cipher.DECRYPT_MODE, privateKey);
            return decryptCipher.doFinal(encryptedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error while data decrypted");
    }

    public static byte[] encryptRSAMessage(byte[] decryptedMessage, PublicKey publicKey) {
        Cipher encryptCipher;
        try {
            encryptCipher = Cipher.getInstance("RSA");
            if (publicKey == null) {
                throw new RuntimeException("Server public key is null");
            }
            encryptCipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return encryptCipher.doFinal(decryptedMessage);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        throw new RuntimeException("Error while data encrypted");
    }
}
