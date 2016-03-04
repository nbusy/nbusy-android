package titan.client.messages;

import java.util.Date;

/**
 * Single message in a chat.
 */
public class Message {
    public final String chatId;
    public final String from;
    public final String to;
    public final Date time;
    public final String message;

    public Message(String chatId, String from, String to, Date time, String message) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }
        if (time == null) {
            throw new IllegalArgumentException("time cannot be null");
        }
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("message cannot be null or empty");
        }

        this.chatId = chatId;
        this.from = from;
        this.to = to;
        this.time = time;
        this.message = message;
    }
}
