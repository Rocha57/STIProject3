import java.io.Serializable;
import java.security.Key;

public class Message implements Serializable {
    private Key symmetric;
    private byte[] encryptedData;

    public int getSharekey() {
        return sharekey;
    }

    public void setSharekey(int sharekey) {
        this.sharekey = sharekey;
    }

    private int sharekey;

    public Key getKeyToShare() {
        return keyToShare;
    }

    public void setKeyToShare(Key keyToShare) {
        this.keyToShare = keyToShare;
    }

    private Key keyToShare;


    public Message(byte[] encryptedData, Key symmetric) {
        this.encryptedData = encryptedData;
        this.symmetric = symmetric;
        this.sharekey = 0;
    }

    public Message(Key keyToShare)
    {
        this.sharekey = 1;
        this.keyToShare = keyToShare;
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
