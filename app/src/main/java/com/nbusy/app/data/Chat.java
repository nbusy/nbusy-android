package com.nbusy.app.data;

import com.google.common.collect.ImmutableSet;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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

    ChatAndNewMessages addNewOutgoingMessages(String... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("message list be null or empty");
        }

        List<Message> newMsgs = new ArrayList<>();
        for (String msg : msgs) {
            newMsgs.add(Message.newOutgoingMessage(id, peerName, msg));
        }

        return new ChatAndNewMessages(upsertMessages(newMsgs), ImmutableSet.copyOf(newMsgs));
    }

    public final class ChatAndNewMessages {
        public final Chat chat;
        public final ImmutableSet<Message> messages;

        public ChatAndNewMessages(Chat chat, ImmutableSet<Message> messages) {
            if (chat == null) {
                throw new IllegalArgumentException("chat cannot be null");
            }
            if (messages == null || messages.size() == 0) {
                throw new IllegalArgumentException("messages cannot be null or empty");
            }

            this.chat = chat;
            this.messages = messages;
        }
    }

    Chat upsertMessages(List<Message> msgs) {
        return upsertMessages(msgs.toArray(new Message[msgs.size()]));
    }

    Chat upsertMessages(Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("message list cannot be null or empty");
        }

        // only add or update a message if it belongs to this chat
        // we're using a set not to allow duplicates
        Set<Message> uniqueMsgs = new HashSet<>();
        for (Message msg : msgs) {
            if (!Objects.equals(msg.chatId, this.id)) {
                continue;
            }
            uniqueMsgs.add(msg);
        }

        // replace the modified messages while preserving message list order
        Set<Message> updatedMsgs = new HashSet<>();
        ImmutableSet.Builder<Message> msgBuilder = ImmutableSet.builder();
        for (Message thisMsg : this.messages) {
            boolean updated = false;
            for (Message newMsg : uniqueMsgs) {
                if (Objects.equals(thisMsg.id, newMsg.id)) {
                    updated = true;
                    msgBuilder.add(newMsg);
                    updatedMsgs.add(newMsg);
                }
            }
            if (!updated) {
                msgBuilder.add(thisMsg);
            }
        }

        // append the new messages to the end of the list
        uniqueMsgs.removeAll(updatedMsgs);
        msgBuilder.addAll(uniqueMsgs);

        return new Chat(id, peerName, lastMessage, lastMessageSent, msgBuilder.build()); // todo: sort message by date here
    }
}
