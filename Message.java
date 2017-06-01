import java.io.Serializable;
import java.security.Key;

public class Message implements Serializable {
    private String data;
    private Key symmetric;
    private byte[] encryptedData;


    public Message(String data, Key symmetric) {
        this.data = data;
        this.symmetric = symmetric;
    }

    public String getData() {
        return data;
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
