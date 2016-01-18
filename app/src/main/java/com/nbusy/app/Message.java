package com.nbusy.app;

/**
 * A message within a chat.
 */
class Message {
    final String id; // unique message ID
    final String from; // sender of this message
    final String body; // message text
    final String sent; // message sent date/time
    final boolean owner; // message by current user
    boolean sentToServer;
    boolean delivered;

    Message(String id, String from, String body, String sent, boolean owner) {
        this.id = id;
        this.from = from;
        this.body = body;
        this.sent = sent;
        this.owner = owner;
    }
}
