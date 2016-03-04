package titan.client.messages;

public class EchoMessage {
    public final String message;

    public EchoMessage(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message cannot be null or empty");
        }

        this.message = message;
    }
}
