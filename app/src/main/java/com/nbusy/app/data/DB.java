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

    void saveMessages(SaveMessagesCallback cb, Message... msgs);

    interface SaveMessagesCallback {
        void messagesSaved();
    }

    void updateMessages(UpdateMessagesCallback cb, Message... msgs);

    interface UpdateMessagesCallback {
        void messagesUpdated();
    }
}
