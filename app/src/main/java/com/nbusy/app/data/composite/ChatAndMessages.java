package com.nbusy.app.data.composite;

import com.google.common.collect.ImmutableSet;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.Message;

import java.util.List;

public class ChatAndMessages {
    public final Chat chat;
    public final ImmutableSet<Message> messages;

    public ChatAndMessages(Chat chat, List<Message> messages) {
        this(chat, ImmutableSet.copyOf(messages));
    }

    public ChatAndMessages(Chat chat, ImmutableSet<Message> messages) {
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
