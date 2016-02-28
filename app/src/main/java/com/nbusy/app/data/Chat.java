package com.nbusy.app.data;

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
    private static final String TAG = Chat.class.getSimpleName();
    private final Map<String, Integer> messageIDtoIndex = new HashMap<>(); // message ID -> messages[index]

    public final String id; // unique chat id
    public final String peerName; // peer name
    public final String lastMessage; // last message in conversation
    public final String sent; // last message sent date/time
    public final List<Message> messages; // list of messages in this chat

    public Chat(String id, String peerName, String lastMessage, String sent, List<Message> messages) {
        this.id = id;
        this.peerName = peerName;
        this.lastMessage = lastMessage;
        this.sent = sent;
        this.messages = messages;
    }

    public Message addMessage(String message) {
        Message msg = new Message(UUID.randomUUID().toString(), id, "Me", null, true, message, new Date(), Message.Status.New);
        messageIDtoIndex.put(msg.id, messages.size());
        messages.add(msg);
        return msg;
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
