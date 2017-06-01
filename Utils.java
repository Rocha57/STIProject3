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

    public Key generateKey() throws NoSuchAlgorithmException {
        return this.keyGen.generateKey();
    }

    public byte[] encryptMessage(byte[] plainBytes, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        this.cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = this.cipher.doFinal(plainBytes);
        return cipherBytes;
    }

    public String decryptMessage(byte[] cipherBytes, Key key) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
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

    }