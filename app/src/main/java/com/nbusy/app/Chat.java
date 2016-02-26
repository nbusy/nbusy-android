package com.nbusy.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One-to-one chat.
 */
public final class Chat {
    private static final String TAG = ChatDetailFragment.class.getSimpleName();
    private final Map<String, Integer> messageIDtoIndex; // message ID -> messages[index]

    public final String id; // unique chat id
    public final String peerName; // peer name
    public final String lastMessage; // last message in conversation
    public final String sent; // last message sent date/time
    public final List<Message> messages; // list of messages in this chat

    public Chat(String id, String peerName, String lastMessage, String sent) {
        this(id, peerName, lastMessage, sent, new ArrayList<Message>());
    }

    public Chat(String id, String peerName, String lastMessage, String sent, List<Message> messages) {
        this.id = id;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.sent = sent;
        this.messages = messages;
        messageIDtoIndex = new HashMap<>();
    }
}
