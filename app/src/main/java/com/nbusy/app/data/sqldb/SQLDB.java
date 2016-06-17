package com.nbusy.app.data.sqldb;

import android.content.ContentValues;
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
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SQLTables.ProfileTable._ID, userProfile.id);
        values.put(SQLTables.ProfileTable.JWT_TOKEN, userProfile.jwttoken);
        values.put(SQLTables.ProfileTable.NAME, userProfile.name);
        values.put(SQLTables.ProfileTable.EMAIL, userProfile.email);
        values.put(SQLTables.ProfileTable.PICTURE, userProfile.picture);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(SQLTables.ProfileTable.TABLE_NAME, null, values);
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
