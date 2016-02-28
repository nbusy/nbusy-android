package com.nbusy.app.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Chats {

    public static List<Chat> ITEMS = new ArrayList<>();
    public static Map<String, Chat> ITEM_MAP = new HashMap<>();

    static {
        // Add sample items.
        addItem(new Chat("1", "Teoman Soygul", "My last message", "123456", Arrays.asList(new Message(UUID.randomUUID().toString(), "1", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                new Message(UUID.randomUUID().toString(), "1", "User ID: " + "1", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser))));

        addItem(new Chat("2", "Chuck Norris", "This is my last-first message!", "9876543", Arrays.asList(new Message(UUID.randomUUID().toString(), "2", "Teoman Soygul", null, true, "Lorem ip sum my message...", new Date(), Message.Status.DeliveredToUser),
                new Message(UUID.randomUUID().toString(), "2", "User ID: " + "2", null, false, "Test test.", new Date(), Message.Status.DeliveredToUser))));
    }

    private static void addItem(Chat item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.peerName, item);
    }
}