package com.nbusy.app;

/**
 * One-to-one chat.
 */
final class Chat {
    public final String peerName; // peer name
    public final String lastMessage; // last message in conversation
    public final String sent; // last message sent date/time

    public Chat(String peerName, String lastMessage, String sent) {
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.sent = sent;
    }
}
