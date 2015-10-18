package com.nbusy.app;

/**
 * A message within a chat.
 */
final class Message {
    public final String from; // sender of this message
    public final String body; // message text
    public final String sent; // message sent date/time

    Message(String from, String body, String sent) {
        this.from = from;
        this.body = body;
        this.sent = sent;
    }
}
