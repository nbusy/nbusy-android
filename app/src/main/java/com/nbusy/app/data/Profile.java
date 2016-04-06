package com.nbusy.app.data;

import com.google.common.base.Optional;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * User profile including user information and chats.
 */
public class Profile {
    private final Map<String, Integer> chatIDtoIndex = new HashMap<>(); // chat ID -> chat[index]

    public final String userId;
    public final List<Chat> chats; // todo: use a map that does not accept dupes

    public Profile(String userId, List<Chat> chats) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("userId cannot be null or empty");
        }
        if (chats == null) {
            throw new IllegalArgumentException("chats cannot be null");
        }

        this.userId = userId;
        this.chats = chats;
        for (int i = 0; i < chats.size(); i++) {
            chatIDtoIndex.put(chats.get(i).id, i);
        }
    }

    public synchronized Optional<Chat> getChat(String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }

        if (!chatIDtoIndex.containsKey(chatId)) {
            return Optional.absent();
        }

        return Optional.of(chats.get(chatIDtoIndex.get(chatId)));
    }

    public synchronized Optional<Chat.ChatAndNewMessages> addNewOutgoingMessages(String chatId, String... msgs) {
        Optional<Chat> chat = getChat(chatId);
        if (!chat.isPresent()) {
            return Optional.absent();
        }

        Chat.ChatAndNewMessages chatAndMsgs = chat.get().addNewOutgoingMessages(msgs);
        setChat(chatId, chatAndMsgs.chat);
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
            setChat(chatId, chat);
            upsertedChats.add(chat);
        }

        return upsertedChats;
    }

    private synchronized void setChat(String chatId, Chat chat) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }
        if (chat == null) {
            throw new IllegalArgumentException("chat cannot be null");
        }

        chats.set(chatIDtoIndex.get(chatId), chat);
    }
}
