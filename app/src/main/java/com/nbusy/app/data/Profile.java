package com.nbusy.app.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User profile including user information and chats.
 */
public class Profile {
    private static final String TAG = Profile.class.getSimpleName();
    private final Map<String, Integer> chatIDtoIndex = new HashMap<>(); // chat ID -> chat[index]

    public final String userId;
    public final List<Chat> chats;

    public Profile(String userId, List<Chat> chats) {
        this.userId = userId;
        this.chats = chats;
        for (int i = 0; i < chats.size(); i++) {
            chatIDtoIndex.put(chats.get(i).id, i);
        }
    }
}
