package com.nbusy.app.data.sqldb;

import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

public class SQLDB implements DB {

    // todo: Because they can be long-running, be sure that you call SQLDBHelper.getWritableDatabase() or SQLDBHelper.getReadableDatabase()
    // in a background thread, such as with AsyncTask or IntentService.


    @Override
    public void createProfile(UserProfile userProfile, CreateProfileCallback cb) {

    }

    @Override
    public void getProfile(GetProfileCallback cb) {

    }

    @Override
    public void getChatMessages(String chatId, GetChatMessagesCallback cb) {

    }

    @Override
    public void getQueuedMessages(GetChatMessagesCallback cb) {

    }

    @Override
    public void upsertMessages(UpsertMessagesCallback cb, Message... msgs) {

    }
}
