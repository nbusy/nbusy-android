package com.nbusy.app.data;

import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

public interface DB {
    void dropDB(DropDBCallback cb);

    void seedDB(SeedDBCallback cb);

    void createProfile(UserProfile userProfile, CreateProfileCallback cb);

    void getProfile(GetProfileCallback cb);

//    void getPicture(GetPictureCallback cb);

//    void upsertChats(UpsertChatsCallback cb, Chat... chats);

    void getChatMessages(String chatId, GetChatMessagesCallback cb);

    void getQueuedMessages(GetChatMessagesCallback cb);

    void upsertMessages(UpsertMessagesCallback cb, Message... msgs);
}
