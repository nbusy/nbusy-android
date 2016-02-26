package com.nbusy.app;

import com.google.common.collect.ImmutableList;

/**
 * One-to-one chat.
 */
public final class Chat {
    public final String id; // unique chat id
    public final String peerName; // peer name
    public final String lastMessage; // last message in conversation
    public final String sent; // last message sent date/time
    public final ImmutableList<Message> messages; // list of messages in this chat

    public Chat(String id, String peerName, String lastMessage, String sent) {
        this(id, peerName, lastMessage, sent, ImmutableList.<Message>of());
    }

    public Chat(String id, String peerName, String lastMessage, String sent, ImmutableList<Message> messages) {
        this.id = id;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.sent = sent;
        this.messages = messages;
    }
}
