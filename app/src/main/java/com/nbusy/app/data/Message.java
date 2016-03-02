package com.nbusy.app.data;

import java.util.Date;
import java.util.UUID;

/**
 * A message within a chat.
 */
public class Message {
    public final String id; // unique message ID
    public final String chatId; // ID of chat this message belongs to
    public final String from; // sender of this message
    public final String to; // receiver of this message
    public final boolean owner; // if sender is also the owner of the message
    public final String body; // message text
    public final Date sent; // message sent date/time
    public final Status status; // delivery status

    Message(String id, String chatId, String from, String to, boolean owner, String body, Date sent, Status status) {
        this.id = id;
        this.chatId = chatId;
        this.from = from;
        this.to = to;
        this.owner = owner;
        this.body = body;
        this.sent = sent;
        this.status = status;
    }

    public static Message NewOutgoingMessage(String chatId, String to, String body) {
        return new Message(UUID.randomUUID().toString(), chatId, null, to, true, body, new Date(), Status.NEW);
    }

    public Message setStatus(Status status) {
        return new Message(id, chatId, from, to, owner, body, sent, status);
    }

    public enum Status {
        NEW,
        SENT_TO_SERVER,
        DELIVERED_TO_USER
    }
}
