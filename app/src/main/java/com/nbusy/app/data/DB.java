package com.nbusy.app.data;

import java.util.List;

public interface DB {
    void getProfile(GetProfileCallback cb);

    interface GetProfileCallback {
        void profileRetrieved(Profile userProfile);
    }

    void getChatMessages(String chatId, GetChatMessagesCallback cb);

    interface GetChatMessagesCallback {
        void chatMessagesRetrieved(List<Message> msgs);
    }

    void enqueueMessage(Message msg, EnqueueMessageCallback cb);

    interface EnqueueMessageCallback {
        void messageEnqueued(Message msg);
    }

    void dequeueMessage(Message msg, DequeueMessageCallback cb);

    interface DequeueMessageCallback {
        void messageDequeued(Message msg);
    }
}
