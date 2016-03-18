package com.nbusy.app.data;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * One-to-one chat.
 */
public final class Chat {
    public final String id; // unique chat id
    public final String peerName; // peer name
    public final String lastMessage; // last message in conversation
    public final Date lastMessageSent; // last message sent time
    public final ImmutableSet<Message> messages; // list of messages in this chat

    public Chat(String id, String peerName, String lastMessage, Date lastMessageSent, ImmutableSet<Message> messages) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (peerName == null || peerName.isEmpty()) {
            throw new IllegalArgumentException("peerName cannot be null or empty");
        }
        if (lastMessage == null || lastMessage.isEmpty()) {
            throw new IllegalArgumentException("lastMessage cannot be null or empty");
        }
        if (lastMessageSent == null) {
            throw new IllegalArgumentException("lastMessageSent cannot be null");
        }
        if (messages == null) {
            throw new IllegalArgumentException("messages cannot be null");
        }

        this.id = id;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.lastMessageSent = lastMessageSent;
        this.messages = messages;
    }

    public Chat(String id, String peerName, String lastMessage, Date lastMessageSent) {
        this(id, peerName, lastMessage, lastMessageSent, ImmutableSet.<Message>of());
    }

    public synchronized ImmutableSet<Message> addNewOutgoingMessage(String... messages) {
        if (messages == null || messages.length == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        List<Message> msgs = new ArrayList<>();
        for (String message : messages) {
            msgs.add(Message.newOutgoingMessage(id, peerName, message));
        }

        return ImmutableSet.copyOf(msgs);
    }

//    public synchronized void addMessages(List<Message> msgs) {
//        if (msgs == null || msgs.size() == 0) {
//            throw new IllegalArgumentException("message list cannot be null or empty");
//        }
//
//        for (Message msg : msgs) {
//            // don't re-add duplicates
//            if (getMessageLocation(msg) != 0) {
//                continue;
//            }
//
//            messageIDtoIndex.put(msg.id, messages.size());
//            messages.add(msg);
//        }
//    }
//
//    public synchronized int updateMessage(Message msg) {
//        if (msg == null) {
//            throw new IllegalArgumentException("message cannot be null");
//        }
//
//        // only update if message belongs to this chat
//        int index = getMessageLocation(msg);
//        if (index == 0) {
//            return 0;
//        }
//
//        messages.set(index, msg);
//        return index;
//    }
//
//    public synchronized int getMessageLocation(Message msg) {
//        if (msg == null) {
//            throw new IllegalArgumentException("message cannot be null");
//        }
//
//        if (!Objects.equals(msg.chatId, id)) {
//            return 0;
//        }
//
//        if (messageIDtoIndex.containsKey(msg.id)) {
//            return messageIDtoIndex.get(msg.id);
//        } else {
//            return 0;
//        }
//    }
}
