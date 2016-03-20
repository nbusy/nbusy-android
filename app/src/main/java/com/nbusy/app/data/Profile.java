package com.nbusy.app.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public synchronized Chat getChat(String chatId) {
        if (chatId == null || chatId.isEmpty()) {
            throw new IllegalArgumentException("chatId cannot be null or empty");
        }

        return chats.get(chatIDtoIndex.get(chatId));
    }

    public synchronized Chat.ChatAndNewMessages addNewOutgoingMessages(String chatId, String... msgs) {
        Chat.ChatAndNewMessages chatAndMsgs = getChat(chatId).addNewOutgoingMessages(msgs);
        chats.set(chatIDtoIndex.get(chatId), chatAndMsgs.chat);
        return chatAndMsgs;
    }

    public synchronized List<Chat> addMessages(Message... msgs) {
        return null;
    }

    public synchronized List<Chat> updateMessages(Message... msgs) {
        return null;
    }

    private synchronized Map<Integer, Chat> getAffectedChats(Message... msgs) {
        Map<Integer, Chat> chatMap = new HashMap<>();
        for (Message msg : msgs) {
            int chatId = chatIDtoIndex.get(msg.chatId);
            chatMap.put(chatId, chats.get(chatId));
        }
        return chatMap;
    }
}
