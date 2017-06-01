import java.io.Serializable;
import java.security.Key;

public class Message implements Serializable {
    private Key symmetric;
    private byte[] encryptedData;


    public Message(byte[] encryptedData, Key symmetric) {
        this.encryptedData = encryptedData;
        this.symmetric = symmetric;
    }

    public Key getSymmetric() {
        return symmetric;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }
}
