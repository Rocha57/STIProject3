import java.io.Serializable;

public class Message implements Serializable {
    private String data;


    public Message(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }
}
