import java.io.Serializable;
import java.security.Key;
import java.security.PublicKey;

public class Message implements Serializable {
    private byte[] symmetric;
    private byte[] encryptedData;
    private PublicKey keyToShare;
    private int sharekey;

    public int getSharekey() {
        return sharekey;
    }

    public void setSharekey(int sharekey) {
        this.sharekey = sharekey;
    }

    public PublicKey getKeyToShare() {
        return keyToShare;
    }

    public void setKeyToShare(PublicKey keyToShare) {
        this.keyToShare = keyToShare;
    }


    public Message(byte[] encryptedData, byte[] symmetric) {
        this.encryptedData = encryptedData;
        this.symmetric = symmetric;
        this.sharekey = 0;
    }

    public Message(PublicKey keyToShare)
    {
        this.sharekey = 1;
        this.keyToShare = keyToShare;
    }

    public byte[] getSymmetric() {
        return symmetric;
    }

    public void setEncryptedData(byte[] encryptedData) {
        this.encryptedData = encryptedData;
    }

    public byte[] getEncryptedData() {
        return encryptedData;
    }
}
