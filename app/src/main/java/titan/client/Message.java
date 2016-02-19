package titan.client;

import java.util.Date;

/**
 * Single message in a chat.
 */
public class Message {
    private final String from;
    private final String to;
    private final Date time;
    private final String message;

    public Message(String from, String to, Date time, String message) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }
}
