package com.nbusy.app.worker.eventbus;

import com.google.common.collect.ImmutableSet;
import com.nbusy.app.data.Chat;

import java.util.Set;

public class ChatsUpdatedEvent {
    public final Set<Chat> chats;

    public ChatsUpdatedEvent(Chat... chats) {
        this(ImmutableSet.copyOf(chats));
    }

    public ChatsUpdatedEvent(Set<Chat> chats) {
        if (chats == null || chats.isEmpty()) {
            throw new IllegalArgumentException("chats cannot be null or empty");
        }

        this.chats = chats;
    }
}
