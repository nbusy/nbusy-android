package com.nbusy.app.data.sqldb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nbusy.app.data.Chat;
import com.nbusy.app.data.DB;
import com.nbusy.app.data.Message;
import com.nbusy.app.data.UserProfile;
import com.nbusy.app.data.callbacks.CreateProfileCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

import java.util.ArrayList;

public class SQLDB implements DB {

    // todo: do all sequal operations in a single background thread and call cb

    private final SQLDBHelper sqldbHelper;
    private final SQLiteDatabase db;

    public SQLDB(Context context) {
        sqldbHelper = new SQLDBHelper(context);
        db = sqldbHelper.getWritableDatabase(); // todo: call this in a background thread as upgrade might take a long while.. also it might fail on full disk
    }

    @Override
    public void seedDB(SeedDBCallback cb) {
        sqldbHelper.seedDB(db);
        cb.success();
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
        String[] projection = {
                SQLTables.ProfileTable._ID,
                SQLTables.ProfileTable.JWT_TOKEN,
                SQLTables.ProfileTable.NAME,
                SQLTables.ProfileTable.EMAIL
        };

        Cursor c = db.query(
                SQLTables.ProfileTable.TABLE_NAME,  // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );

        if (c.getCount() <= 0) {
            cb.error();
            return;
        }

        c.moveToFirst();
        String id = c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable._ID));
        String jwtToken = c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable.JWT_TOKEN));
        String name = c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable.NAME));
        String email = c.getString(c.getColumnIndexOrThrow(SQLTables.ProfileTable.EMAIL));

        cb.profileRetrieved(new UserProfile(id, jwtToken, email, name, null, new ArrayList<Chat>()));
    }

    @Override
    public void getChatMessages(String chatId, GetChatMessagesCallback cb) {

    }

    @Override
    public void getQueuedMessages(GetChatMessagesCallback cb) {

    }

    @Override
    public void upsertMessages(UpsertMessagesCallback cb, Message... msgs) {
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
//        // Define 'where' part of query.
//        String selection = FeedEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
//// Specify arguments in placeholder order.
//        String[] selectionArgs = { String.valueOf(rowId) };
//// Issue SQL statement.
//        db.delete(table_name, selection, selectionArgs);

    }
}
