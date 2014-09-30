package com.nbusy.app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Messages {

    public static List<Message> ITEMS = new ArrayList<>();
    public static Map<String, Message> ITEM_MAP = new HashMap<>();

    static {
        // Add sample items.
        addItem(new Message("Teoman", "My last message", "123456"));
        addItem(new Message("Chuck", "This is my last-first message!", "9876543"));
    }

    private static void addItem(Message item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    public static class Message {
        public String name;
        public String message;
        public String sent;

        public Message(String name, String message, String sent) {
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
