package com.nbusy.app.data;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.util.HashMap;
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

    public Chat setMessageStatus(Message message, Message.Status newStatus) {
        Message newMessage = message.setStatus(newStatus);
        return upsertMessages(newMessage).iterator().next();
    }

    public synchronized Set<Chat> upsertMessages(Message... msgs) {
        ListMultimap<Chat, Message> chatMap = ArrayListMultimap.create();
        for (Message msg : msgs) {
            chatMap.put(getChat(msg.chatId), msg);
        }
        for (Chat chat : chatMap.keySet()) {
            chats.set(chatIDtoIndex.get(chat.id), chat.upsertMessages(chatMap.get(chat)));
        }

        return chatMap.keySet();
    }
}
