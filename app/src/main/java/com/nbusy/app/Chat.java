package com.nbusy.app;

import com.google.common.collect.ImmutableList;

/**
 * One-to-one chat.
 */
final class Chat {
    final String id; // unique chat id
    final String peerName; // peer name
    final String lastMessage; // last message in conversation
    final String sent; // last message sent date/time
    final ImmutableList<Message> messages; // list of messages in this chat

    Chat(String id, String peerName, String lastMessage, String sent) {
        this(id, peerName, lastMessage, sent, ImmutableList.<Message>of());
    }

    Chat(String id, String peerName, String lastMessage, String sent, ImmutableList<Message> messages) {
        this.id = id;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.sent = sent;
        this.messages = messages;
    }
}
