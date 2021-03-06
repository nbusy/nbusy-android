package com.nbusy.app.data;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.nbusy.app.data.composite.ChatAndMessages;
import com.nbusy.app.data.composite.ChatsAndMessages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * User profile including user information and chats.
 */
public final class UserProfile {

    // todo: user sorted map by last message time
    // todo: use a map that does not accept dupe keys or values
    private final HashMap<String, Chat> chats = new HashMap<>(); // chat ID -> chat

    public final String id;
    public final String jwtToken;
    public final String name;
    public final String email;
    private byte[] picture;

    public UserProfile(String id, String jwtToken, String email, String name, byte[] picture, List<Chat> chats) {
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

        this.id = id;
        this.jwtToken = jwtToken;
        this.email = email;
        this.name = name;
        this.picture = picture;

        upsertChats(chats);
    }

    public UserProfile(String id, String jwtToken, String email, String name, byte[] picture) {
        this(id, jwtToken, email, name, picture, new ArrayList<Chat>());
    }

    public synchronized Optional<byte[]> getPicture() {
        if (picture == null) {
            return Optional.absent();
        }

        return Optional.of(picture);
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

    public synchronized List<Chat> getChats() {
        return new ArrayList<>(this.chats.values());
    }

    public synchronized void upsertChats(List<Chat> chats) {
        for (Chat chat : chats) {
            this.chats.put(chat.id, chat);
        }
    }

    public synchronized Optional<ChatAndMessages> addNewOutgoingMessages(String chatId, String... msgs) {
        Optional<Chat> chat = getChat(chatId);
        if (!chat.isPresent()) {
            return Optional.absent();
        }

        ChatAndMessages chatAndMsgs = chat.get().addNewOutgoingMessages(msgs);
        updateChat(chatAndMsgs.chat);
        return Optional.of(chatAndMsgs);
    }

    public ChatsAndMessages setMessageStatuses(Message.Status newStatus, Message... msgs) {
        Set<Message> updatedMsgs = new LinkedHashSet<>();
        for (Message msg : msgs) {
            updatedMsgs.add(msg.setStatus(newStatus));
        }

        Set<Chat> chats = upsertMessages(updatedMsgs);

        return new ChatsAndMessages(chats, updatedMsgs);
    }

    public synchronized Set<Chat> upsertMessages(Set<Message> msgs) {
        return upsertMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public synchronized Set<Chat> upsertMessages(List<Message> msgs) {
        return upsertMessages(msgs.toArray(new Message[msgs.size()]));
    }

    public synchronized Set<Chat> upsertMessages(Message... msgs) {
        // todo: one of the maps/lists we use here breaks message ordering
        Set<Chat> upsertedChats = new LinkedHashSet<>();
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
