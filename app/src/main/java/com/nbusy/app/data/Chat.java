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

     synchronized ChatAndNewMessages addNewOutgoingMessages(String... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("message list be null or empty");
        }

        List<Message> newMsgs = new ArrayList<>();
        for (String msg : msgs) {
            newMsgs.add(Message.newOutgoingMessage(id, peerName, msg));
        }

        return new ChatAndNewMessages(addMessages(newMsgs), ImmutableSet.copyOf(newMsgs));
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

     synchronized Chat addMessages(List<Message> msgs) {
        return addMessages(msgs.toArray(new Message[msgs.size()]));
    }

     synchronized Chat addMessages(Message... msgs) {
        if (msgs == null || msgs.length == 0) {
            throw new IllegalArgumentException("message list cannot be null or empty");
        }

        // only add messages that belongs to this chat
        // only add new messages and don't allow duplicates
        Set<Message> thisMsgs = new HashSet<>();
        for (Message msg : msgs) {
            if (!Objects.equals(msg.chatId, this.id)) {
                continue;
            }

            boolean dupe = false;
            for (Message m : this.messages) {
                if (Objects.equals(m.id, msg.id)) {
                    dupe = true;
                    break;
                }
            }

            if (!dupe) {
                thisMsgs.add(msg);
            }
        }

        return new Chat(
                id,
                peerName,
                lastMessage,
                lastMessageSent,
                ImmutableSet.<Message>builder().addAll(this.messages).addAll(thisMsgs).build());
    }

    synchronized Chat updateMessages(Message... msgs) {
        ImmutableSet.Builder<Message> msgBuilder = ImmutableSet.builder();
        for (Message thisMsg : this.messages) {
            boolean updated = false;
            for (Message newMsg : msgs) {
                if (Objects.equals(thisMsg.id, newMsg.id)) {
                    updated = true;
                    msgBuilder.add(newMsg);
                }
            }
            if (!updated) {
                msgBuilder.add(thisMsg);
            }
        }

        return new Chat(id, peerName, lastMessage, lastMessageSent, msgBuilder.build());
    }
}
