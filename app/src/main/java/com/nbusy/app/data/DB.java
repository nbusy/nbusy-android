package com.nbusy.app.data;

import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetPictureCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertChatsCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

import java.util.List;

public interface DB {
    void dropDB(DropDBCallback cb);

    void seedDB(SeedDBCallback cb);

    void close();

    boolean isOpen();

    void createProfile(UserProfile userProfile, CreateProfileCallback cb);

    void getProfile(GetProfileCallback cb);

    void getPicture(GetPictureCallback cb);

    void upsertChats(UpsertChatsCallback cb, Chat... chats);

    void upsertChats(UpsertChatsCallback cb, List<Chat> chats);

    void getChatMessages(String chatId, GetChatMessagesCallback cb);

    void getQueuedMessages(GetChatMessagesCallback cb);

    void upsertMessages(UpsertMessagesCallback cb, Message... msgs);

    void upsertMessages(UpsertMessagesCallback cb, List<Message> msgs);
}
