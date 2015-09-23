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
        addItem(new Chat("Teoman", "My last message", "123456"));
        addItem(new Chat("Chuck", "This is my last-first message!", "9876543"));
    }

    private static void addItem(Chat item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    public static class Chat {
        public String name; // peer name
        public String message; // last message in conversation
        public String sent; // last message sent date/time

        public Chat(String name, String message, String sent) {
            this.name = name;
            this.message = message;
            this.sent = sent;
        }

        @Override
        public String toString() {
            return name + " " + sent;
        }
    }
}