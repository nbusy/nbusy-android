package titan.client;

public class Message {
    private final String from;
    private final String to;
    private final String time;
    private final String message;

    public Message(String from, String to, String time, String message) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }
}
