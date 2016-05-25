package com.nbusy.app.data;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User profile including user information and chats.
 */
public final class Profile {

    // todo: user sorted map by last message time
    // todo: use a map that does not accept dupe keys or values
    private final HashMap<String, Chat> chats = new HashMap<>(); // chat ID -> chat

    public final String ID;
    public final String JWTToken;
    public final String Name;
    public final String Email;
    public byte[] Picture;

    public Profile(String id, String jwtToken, String email,  String name, List<Chat> chats) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("id cannot be null or empty");
        }
        if (jwtToken == null || jwtToken.isEmpty()) {
            throw new IllegalArgumentException("jwtToken cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("email cannot be null or empty");
        }
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("name cannot be null or empty");
        }
        if (chats == null) {
            throw new IllegalArgumentException("chats cannot be null");
        }

        this.ID = id;
        this.JWTToken = jwtToken;
        this.Email = email;
        this.Name = name;

        for (Chat chat : chats) {
            this.chats.put(chat.id, chat);
        }
    }

    public synchronized Optional<Chat> getChat(String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }

        if (!chats.containsKey(chatId)) {
            return Optional.absent();
        }

        return Optional.of(chats.get(chatId));
    }

    public synchronized Collection<Chat> getChats() {
        return this.chats.values();
    }

    public synchronized Optional<Chat.ChatAndNewMessages> addNewOutgoingMessages(String chatId, String... msgs) {
        Optional<Chat> chat = getChat(chatId);
        if (!chat.isPresent()) {
            return Optional.absent();
        }

        Chat.ChatAndNewMessages chatAndMsgs = chat.get().addNewOutgoingMessages(msgs);
        updateChat(chatAndMsgs.chat);
        return Optional.of(chatAndMsgs);
    }

    public Set<Chat> setMessageStatuses(Message.Status newStatus, Message... msgs) {
        Set<Message> updatedMsgs = new HashSet<>();
        for (Message msg : msgs) {
            updatedMsgs.add(msg.setStatus(newStatus));
        }

        return upsertMessages(updatedMsgs);
    }

    public synchronized Set<Chat> upsertMessages(Set<Message> msgs) {
        return upsertMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public synchronized Set<Chat> upsertMessages(List<Message> msgs) {
        return upsertMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public synchronized Set<Chat> upsertMessages(Message... msgs) {
        Set<Chat> upsertedChats = new HashSet<>();
        ListMultimap<String, Message> chatIDToMessages = ArrayListMultimap.create();
        for (Message msg : msgs) {
            chatIDToMessages.put(msg.chatId, msg);
        }
        for (String chatId : chatIDToMessages.keySet()) {
            Optional<Chat> chatOpt = getChat(chatId);
            if (!chatOpt.isPresent()) {
                continue;
            }

            Chat chat = chatOpt.get().upsertMessages(chatIDToMessages.get(chatId));
            updateChat(chat);
            upsertedChats.add(chat);
        }

        return upsertedChats;
    }

    private synchronized void updateChat(Chat chat) {
        if (chat == null) {
            throw new IllegalArgumentException("chat cannot be null");
        }
        if (chat.id == null || chat.id.isEmpty()) {
            throw new IllegalArgumentException("chat.id cannot be null or empty");
        }

        this.chats.put(chat.id, chat);
    }
}
