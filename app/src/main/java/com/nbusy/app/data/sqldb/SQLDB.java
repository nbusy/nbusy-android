package com.nbusy.app.data.sqldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

public class SQLDB implements DB {

    private final SQLDBHelper sqldbHelper;
    private final SQLiteDatabase db;

    public SQLDB(Context context) {
        sqldbHelper = new SQLDBHelper(context);
        // todo: call this in a background thread as upgrade might take a long while.. also it might fail on full disk
        db = sqldbHelper.getWritableDatabase();
    }

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
