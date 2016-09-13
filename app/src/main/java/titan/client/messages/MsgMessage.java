package titan.client.messages;

import java.util.Date;

/**
 * Single message in a chat.
 */
public class MsgMessage {
    public final String from;
    public final String to;
    public final Date time;
    public final String message;

    public MsgMessage(String from, String to, Date time, String message) {
        if (time == null) {
            throw new IllegalArgumentException("time cannot be null");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message cannot be null or empty");
        }

        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }
}
