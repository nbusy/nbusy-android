package com.nbusy.app;

/**
 * A message within a chat.
 */
final class Message {
    final String from; // sender of this message
    final String body; // message text
    final String sent; // message sent date/time
    final boolean owner; // message by current user

    Message(String from, String body, String sent, boolean owner) {
        this.from = from;
        this.body = body;
        this.sent = sent;
        this.owner = owner;
    }
}
