package com.nbusy.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Chats {

    public static List<Chat> ITEMS = new ArrayList<>();
    public static Map<String, Chat> ITEM_MAP = new HashMap<>();

    static {
        // Add sample items.
        addItem(new Chat("1", "Teoman Soygul", "My last message", "123456"));
        addItem(new Chat("2", "Chuck Norris", "This is my last-first message!", "9876543"));
    }

    private static void addItem(Chat item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.peerName, item);
    }
}