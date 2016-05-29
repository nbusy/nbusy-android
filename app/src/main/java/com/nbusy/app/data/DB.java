package com.nbusy.app.data;

import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

public interface DB {
    void createProfile(CreateProfileCallback cb);

    void getProfile(GetProfileCallback cb);

    void getChatMessages(String chatId, GetChatMessagesCallback cb);

    void getQueuedMessages(GetChatMessagesCallback cb);

    void upsertMessages(UpsertMessagesCallback cb, Message... msgs);
}
