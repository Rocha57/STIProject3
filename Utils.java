import javax.crypto.*;
import java.security.*;

public class Utils {

    public KeyGenerator keyGen;
    public Cipher cipher;

    public Utils() throws NoSuchAlgorithmException, NoSuchPaddingException {
        this.keyGen = KeyGenerator.getInstance("AES");
        this.keyGen.init(128);
        this.cipher = Cipher.getInstance("AES");  // Transformation of the algorithm
    }

    public SecretKey generateKey() throws NoSuchAlgorithmException {
        return this.keyGen.generateKey();
    }

    public byte[] encryptMessage(byte[] plainBytes, SecretKey key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = this.cipher.doFinal(plainBytes);
        return cipherBytes;
    }

    public String decryptMessage(byte[] cipherBytes, SecretKey key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] plainBytes = this.cipher.doFinal(cipherBytes);
        return new String(plainBytes);
    }


    public KeyPair kPGGen(int keysize) throws NoSuchAlgorithmException {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(keysize);
        KeyPair kp = kpg.genKeyPair();
        return kp;
    }

    public byte[] wrapKey(SecretKey symmetric, PublicKey publicKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException {
        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.WRAP_MODE, publicKey);
        byte[] wrapped = cipher1.wrap(symmetric);
        return wrapped;
    }

    public SecretKey unwrapKey(byte[] symmetric, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException {
        Cipher cipher1 = Cipher.getInstance("RSA");
        cipher1.init(Cipher.UNWRAP_MODE, privateKey);
        SecretKey wrapped = (SecretKey) cipher1.unwrap(symmetric,"AES", Cipher.SECRET_KEY);
        return wrapped;
    }

}