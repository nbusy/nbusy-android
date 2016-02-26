package com.nbusy.app;

import java.util.Date;

/**
 * A message within a chat.
 */
class Message {
    final String id; // unique message ID
    final String chatId; // ID of chat this message belongs to
    final String from; // sender of this message
    final String to; // receiver of this message
    final String body; // message text
    final Date sent; // message sent date/time
    final boolean owner; // message by current user

    // todo: make this class immutable (basically below two) to make change tracking easier

    boolean sentToServer;
    boolean delivered;

    Message(String id, String chatId, String from, String to, String body, Date sent, boolean owner) {
        this.id = id;
        this.chatId = chatId;
        this.from = from;
        this.to = to;
        this.body = body;
        this.sent = sent;
        this.owner = owner;
    }
}
