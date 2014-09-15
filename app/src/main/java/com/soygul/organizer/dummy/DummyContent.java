package com.soygul.organizer.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static List<DummyItem> ITEMS = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();

    static {
        // Add sample items.
        addItem(new DummyItem("Teoman", "My last message", "123456"));
    }

    private static void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.name, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public String name;
        public String message;
        public String sent;

        public DummyItem(String name, String message, String sent) {
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
