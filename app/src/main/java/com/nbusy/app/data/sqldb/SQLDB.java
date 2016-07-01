package com.nbusy.app.data.sqldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.google.common.base.Optional;
import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetPictureCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertChatsCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

import java.util.ArrayList;

public class SQLDB implements DB {

    // todo: do all sql operations in a single background thread and call cb
    // todo: call db.close() when background service stops and create new instance if closed == true

    private final SQLDBHelper sqldbHelper;
    private final SQLiteDatabase db;

    public SQLDB(Context context) {
        sqldbHelper = new SQLDBHelper(context);
        // todo: call this in a background thread as upgrade might take a long while.. also it might fail on full disk
        db = sqldbHelper.getWritableDatabase();
    }

    @Override
    public void dropDB(final DropDBCallback cb) {
        sqldbHelper.dropDB(db);
        cb.success();
    }

    @Override
    public void seedDB(final SeedDBCallback cb) {
        UserProfile profile = new UserProfile(
                "id-1234",
                "token-12jg4ec",
                "mail-chuck@nbusy.com",
                "name-chuck norris",
                new byte[]{0, 2, 3},
                new ArrayList<Chat>());

        createProfile(profile, new CreateProfileCallback() {
            @Override
            public void success() {
                cb.success();
            }

            @Override
            public void error() {
                cb.error();
            }
        });
    }

    @Override
    public void createProfile(UserProfile userProfile, CreateProfileCallback cb) {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(SQLTables.ProfileTable._ID, userProfile.id);
        values.put(SQLTables.ProfileTable.JWT_TOKEN, userProfile.jwtToken);
        values.put(SQLTables.ProfileTable.NAME, userProfile.name);
        values.put(SQLTables.ProfileTable.EMAIL, userProfile.email);

        Optional<byte[]> picture = userProfile.getPicture();
        if (picture.isPresent()) {
            values.put(SQLTables.ProfileTable.PICTURE, picture.get());
        }

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(SQLTables.ProfileTable.TABLE_NAME, null, values);
        if (newRowId != -1) {
            cb.success();
        } else {
            cb.error();
        }
    }

    @Override
    public void getProfile(GetProfileCallback cb) {
        String[] projection = {
                SQLTables.ProfileTable._ID,
                SQLTables.ProfileTable.JWT_TOKEN,
                SQLTables.ProfileTable.NAME,
                SQLTables.ProfileTable.EMAIL
        };

        UserProfile profile = null;
        try (Cursor c = db.query(
                SQLTables.ProfileTable.TABLE_NAME, // The table to query
                projection, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        )) {
            if (c.getCount() > 0) {
                c.moveToFirst();
                profile = new UserProfile(
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable._ID)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable.JWT_TOKEN)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable.EMAIL)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable.NAME)),
                        null,
                        new ArrayList<Chat>());
            }
        }

        if (profile == null) {
            cb.error();
            return;
        }

        cb.profileRetrieved(profile);
    }

    @Override
    public void getPicture(GetPictureCallback cb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void upsertChats(UpsertChatsCallback cb, Chat... chats) {
        cb.callback();
    }

    @Override
    public void getChatMessages(String chatId, GetChatMessagesCallback cb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void getQueuedMessages(GetChatMessagesCallback cb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void upsertMessages(UpsertMessagesCallback cb, Message... msgs) {
        throw new UnsupportedOperationException();

//// New value for one column
//        ContentValues values = new ContentValues();
//        values.put(FeedEntry.COLUMN_NAME_TITLE, title);
//
//// Which row to update, based on the ID
//        String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
//        String[] selectionArgs = { String.valueOf(rowId) };
//
//        int count = db.update(
//                FeedReaderDbHelper.FeedEntry.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs);
    }

    private void deleteMessages() {
        throw new UnsupportedOperationException();

//        // Define 'where' part of query.
//        String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
//// Specify arguments in placeholder order.
//        String[] selectionArgs = { String.valueOf(rowId) };
//// Issue SQL statement.
//        db.delete(table_name, selection, selectionArgs);

    }
}
