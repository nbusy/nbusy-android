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

    void enqueueMessages(EnqueueMessagesCallback cb, Message... msgs);

    interface EnqueueMessagesCallback {
        void messagesEnqueued(Message... msgs);
    }

    void dequeueMessages(DequeueMessagesCallback cb, Message... msgs);

    interface DequeueMessagesCallback {
        void messagesDequeued(Message... msg);
    }
}
