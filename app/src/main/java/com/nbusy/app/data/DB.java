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

    void upsertMessages(UpsertMessagesCallback cb, Message... msgs);

    interface UpsertMessagesCallback {
        void messagesUpserted();
    }
}
