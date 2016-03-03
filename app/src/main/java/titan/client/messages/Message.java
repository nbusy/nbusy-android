package titan.client.messages;

import java.util.Date;

/**
 * Single message in a chat.
 */
public class Message {
    public final String from;
    public final String to;
    public final Date time;
    public final String message;

    public Message(String from, String to, Date time, String message) {
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }
}
