package com.nbusy.app.data.composite;

import com.google.common.collect.ImmutableSet;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.Message;

import java.util.Set;

public class ChatsAndMessages {
    public final ImmutableSet<Chat> chats;
    public final ImmutableSet<Message> messages;

    public ChatsAndMessages(Set<Chat> chats, Set<Message> messages) {
        this(ImmutableSet.copyOf(chats), ImmutableSet.copyOf(messages));
    }

    public ChatsAndMessages(ImmutableSet<Chat> chats, ImmutableSet<Message> messages) {
        if (chats == null || chats.size() == 0) {
            throw new IllegalArgumentException("chats cannot be null or empty");
        }
        if (messages == null || messages.size() == 0) {
            throw new IllegalArgumentException("messages cannot be null or empty");
        }

        this.chats = chats;
        this.messages = messages;
    }
}
