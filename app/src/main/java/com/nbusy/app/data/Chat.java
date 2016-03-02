package com.nbusy.app.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * One-to-one chat.
 */
public final class Chat {
    private final Map<String, Integer> messageIDtoIndex = new HashMap<>(); // message ID -> messages[index]

    public final String id; // unique chat id
    public final String peerName; // peer name
    public final String lastMessage; // last message in conversation
    public final Date sent; // last message sent time
    public final List<Message> messages = new ArrayList<>(); // list of messages in this chat

    public Chat(String id, String peerName, String lastMessage, Date sent) {
        this.id = id;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.sent = sent;
    }

    public Message addMessage(String message) {
        Message msg = Message.newOutgoingMessage(id, peerName, message);
        messageIDtoIndex.put(msg.id, messages.size());
        messages.add(msg);
        return msg;
    }

    public void addMessages(List<Message> msgs) {
        for (int i = 0; i < msgs.size(); i++) {
            messageIDtoIndex.put(msgs.get(i).id, messages.size() + i);
        }
        messages.addAll(msgs);
    }

    public int updateMessage(Message msg) {
        // only update if message belongs to this chat
        if (!Objects.equals(msg.chatId, id)) {
            return 0;
        }

        int index = messageIDtoIndex.get(msg.id);
        if (index == 0) {
            return 0;
        }

        messages.set(index, msg);
        return index;
    }
}
