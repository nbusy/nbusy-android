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
import com.nbusy.app.data.callbacks.DropDBCallback;
import com.nbusy.app.data.callbacks.GetChatMessagesCallback;
import com.nbusy.app.data.callbacks.GetPictureCallback;
import com.nbusy.app.data.callbacks.GetProfileCallback;
import com.nbusy.app.data.callbacks.SeedDBCallback;
import com.nbusy.app.data.callbacks.UpsertChatsCallback;
import com.nbusy.app.data.callbacks.UpsertMessagesCallback;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SQLDB implements DB {

    // todo: do all sql operations in a single background thread and call cb
    // todo: call db.close() when background service stops and create new instance if closed == true

    private static final String EQ_SEL = " = ?";

    private final SQLDBHelper sqldbHelper;
    private final SQLiteDatabase db;

    public SQLDB(Context context) {
        sqldbHelper = new SQLDBHelper(context);
        // todo: call this in a background thread as upgrade might take a long while.. also it might fail on full disk
        db = sqldbHelper.getWritableDatabase();
    }

    private List<Message> getChatMessages(String selection, String[] selectionArgs) {
        String[] projection = {
                SQLTables.MessageTable._ID,
                SQLTables.MessageTable.CHAT_ID,
                SQLTables.MessageTable.FROM,
                SQLTables.MessageTable.BODY,
                SQLTables.MessageTable.SENT,
                SQLTables.MessageTable.STATUS
        };

        ArrayList<Message> msgs = new ArrayList<>();
        try (Cursor c = db.query(
                SQLTables.MessageTable.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        )) {
            while (c.moveToNext()) {
                String from = c.getString(c.getColumnIndexOrThrow(SQLTables.MessageTable.FROM));
                boolean owner = from == null || Objects.equals(from, "");
                msgs.add(new Message(
                        c.getString(c.getColumnIndexOrThrow(SQLTables.MessageTable._ID)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.MessageTable.CHAT_ID)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.MessageTable.FROM)),
                        null,
                        owner,
                        c.getString(c.getColumnIndexOrThrow(SQLTables.MessageTable.BODY)),
                        new Date(c.getLong(c.getColumnIndexOrThrow(SQLTables.MessageTable.SENT))),
                        Message.Status.valueOf(c.getString(c.getColumnIndexOrThrow(SQLTables.MessageTable.STATUS)))));
            }
        }

        return msgs;
    }

    /*********************
     * DB Implementation *
     *********************/

    @Override
    public void dropDB(final DropDBCallback cb) {
        sqldbHelper.dropDB(db);
        cb.success();
    }

    @Override
    public void seedDB(final SeedDBCallback cb) {
        createProfile(SeedData.profile, new CreateProfileCallback() {
            @Override
            public void success() {
                upsertChats(new UpsertChatsCallback() {
                    @Override
                    public void success() {
                        cb.success();
                    }

                    @Override
                    public void error() {
                        cb.error();
                    }
                }, SeedData.chatsArray);
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
        long newRowId = db.insertWithOnConflict(SQLTables.ProfileTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        if (newRowId != -1) {
            cb.success();
        } else {
            cb.error();
        }
    }

    @Override
    public void getProfile(GetProfileCallback cb) {
        // retrieve profile fields, return null if there is no user profile
        String[] profileProjection = {
                SQLTables.ProfileTable._ID,
                SQLTables.ProfileTable.JWT_TOKEN,
                SQLTables.ProfileTable.NAME,
                SQLTables.ProfileTable.EMAIL
        };

        UserProfile profile = null;
        try (Cursor c = db.query(
                SQLTables.ProfileTable.TABLE_NAME, // The table to query
                profileProjection, // The columns to return
                null, // The columns for the WHERE clause
                null, // The values for the WHERE clause
                null, // don't group the rows
                null, // don't filter by row groups
                null // The sort order
        )) {
            if (c.moveToFirst()) {
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

        // retrieve chats with their fields, return empty list if there are not chats
        String[] chatsProjection = {
                SQLTables.ChatTable._ID,
                SQLTables.ChatTable.PEER_NAME,
                SQLTables.ChatTable.LAST_MESSAGE,
                SQLTables.ChatTable.LAST_MESSAGE_SENT
        };

        ArrayList<Chat> chats = new ArrayList<>();
        try (Cursor c = db.query(
                SQLTables.ChatTable.TABLE_NAME,
                chatsProjection,
                null,
                null,
                null,
                null,
                null
        )) {
            while (c.moveToNext()) {
                chats.add(new Chat(
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ChatTable._ID)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ChatTable.PEER_NAME)),
                        c.getString(c.getColumnIndexOrThrow(SQLTables.ChatTable.LAST_MESSAGE)),
                        new Date(c.getLong(c.getColumnIndexOrThrow(SQLTables.ChatTable.LAST_MESSAGE_SENT)))
                ));
            }
        }

        profile.upsertChats(chats);
        cb.profileRetrieved(profile);
    }

    @Override
    public void getPicture(GetPictureCallback cb) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void upsertChats(UpsertChatsCallback cb, Chat... chats) {
        for (Chat chat : chats) {
            ContentValues values = new ContentValues();
            values.put(SQLTables.ChatTable._ID, chat.id);
            values.put(SQLTables.ChatTable.PEER_NAME, chat.peerName);
            values.put(SQLTables.ChatTable.LAST_MESSAGE, chat.lastMessage);
            values.put(SQLTables.ChatTable.LAST_MESSAGE_SENT, chat.lastMessageSent.getTime());

            long newRowId = db.insertWithOnConflict(SQLTables.ChatTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (newRowId == -1) {
                cb.error();
                return;
            }
        }

        cb.success();
    }

    @Override
    public void getChatMessages(String chatId, GetChatMessagesCallback cb) {
        cb.chatMessagesRetrieved(getChatMessages(SQLTables.MessageTable.CHAT_ID + EQ_SEL, new String[]{chatId}));
    }

    @Override
    public void getQueuedMessages(GetChatMessagesCallback cb) {
        cb.chatMessagesRetrieved(getChatMessages(SQLTables.MessageTable.STATUS + EQ_SEL, new String[]{Message.Status.NEW.toString()}));
    }

    @Override
    public void upsertMessages(UpsertMessagesCallback cb, Message... msgs) {
        for (Message msg : msgs) {
            ContentValues values = new ContentValues();
            values.put(SQLTables.MessageTable._ID, msg.id);
            values.put(SQLTables.MessageTable.CHAT_ID, msg.chatId);
            values.put(SQLTables.MessageTable.FROM, msg.from);
            values.put(SQLTables.MessageTable.BODY, msg.body);
            values.put(SQLTables.MessageTable.SENT, msg.sent.getTime());
            values.put(SQLTables.MessageTable.STATUS, msg.status.toString());

            long newRowId = db.insertWithOnConflict(SQLTables.MessageTable.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            if (newRowId == -1) {
                cb.error();
                return;
            }
        }

        cb.success();
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
