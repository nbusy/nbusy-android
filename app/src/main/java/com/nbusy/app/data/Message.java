package com.nbusy.app.data;

import java.util.Date;
import java.util.UUID;

/**
 * A message within a chat.
 */
public final class Message {
    public final String id; // unique message ID
    public final String chatId; // ID of chat this message belongs to
    public final String from; // sender of this message
    public final String to; // receiver of this message
    public final boolean owner; // if sender is also the owner of the message
    public final String body; // message text
    public final Date sent; // message sent date/time
    public final Status status; // delivery status

    Message(String id, String chatId, String from, String to, boolean owner, String body, Date sent, Status status) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("body cannot be null or empty");
        }
        if (sent == null) {
            throw new IllegalArgumentException("sent cannot be null");
        }
        if (status == null) {
            throw new IllegalArgumentException("status cannot be null");
        }

        this.id = id;
        this.chatId = chatId;
        this.from = from;
        this.to = to;
        this.owner = owner;
        this.body = body;
        this.sent = sent;
        this.status = status;
    }

    public static Message newOutgoingMessage(String chatId, String to, String body) {
        return new Message(UUID.randomUUID().toString(), chatId, null, to, true, body, new Date(), Status.NEW);
    }

    public static Message newIncomingMessage(String chatId, String from, Date sent, String body) {
        return new Message(UUID.randomUUID().toString(), chatId, from, null, false, body, sent, Status.RECEIVED);
    }

    Message setStatus(Status status) {
        return new Message(id, chatId, from, to, owner, body, sent, status);
    }

    public enum Status {
        NEW,
        SENT_TO_SERVER,
        DELIVERED_TO_USER,
        RECEIVED
    }
}
