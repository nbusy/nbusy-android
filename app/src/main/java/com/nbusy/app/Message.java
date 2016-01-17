package com.nbusy.app;

/**
 * A message within a chat.
 */
class Message {
    final String from; // sender of this message
    final String body; // message text
    final String sent; // message sent date/time
    final boolean owner; // message by current user
    boolean delivered = true;

    Message(String from, String body, String sent, boolean owner) {
        this.from = from;
        this.body = body;
        this.sent = sent;
        this.owner = owner;
    }
}
