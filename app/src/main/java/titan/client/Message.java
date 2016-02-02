package titan.client;

public class Message {
    private final String to;
    private final String body;

    public Message(String to, String body) {
        this.to = to;
        this.body = body;
    }
}
