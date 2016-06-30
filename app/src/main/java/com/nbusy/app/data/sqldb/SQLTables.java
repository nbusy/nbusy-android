package com.nbusy.app.data.sqldb;

import android.provider.BaseColumns;

public final class SQLTables {

    private SQLTables() {
    }

    private static final String COMMA_SEP = ",";

    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String INTEGER_TYPE = " INTEGER";

    public static final String CREATE_PROFILE_TABLE =
            "CREATE TABLE " + ProfileTable.TABLE_NAME + " (" +
                    ProfileTable._ID + " TEXT PRIMARY KEY," +
                    ProfileTable.JWT_TOKEN + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.NAME + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.EMAIL + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.PICTURE + BLOB_TYPE +
                    ")";

    public static final String CREATE_CHATS_TABLE =
            "CREATE TABLE " + ChatsTable.TABLE_NAME + " (" +
                    ChatsTable._ID + " TEXT PRIMARY KEY," +
                    ChatsTable.PEER_NAME + TEXT_TYPE + COMMA_SEP +
                    ChatsTable.LAST_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    ChatsTable.LAST_MESSAGE_SENT + INTEGER_TYPE +
                    ")";

    public static final String CREATE_MESSAGES_TABLE =
            "CREATE TABLE " + MessagesTable.TABLE_NAME + " (" +
                    MessagesTable._ID + " TEXT PRIMARY KEY," +
                    MessagesTable.CHAT_ID + TEXT_TYPE + COMMA_SEP +
                    MessagesTable.FROM + TEXT_TYPE + COMMA_SEP +
                    MessagesTable.BODY + TEXT_TYPE + COMMA_SEP +
                    MessagesTable.SENT + INTEGER_TYPE + COMMA_SEP +
                    MessagesTable.STATUS + TEXT_TYPE + COMMA_SEP +
                    "FOREIGN KEY(" + MessagesTable.CHAT_ID + ") REFERENCES " + ChatsTable.TABLE_NAME + "(" + ChatsTable._ID + ")" +
                    ")";

    public static final String DROP_PROFILE_TABLE = "DROP TABLE IF EXISTS " + ProfileTable.TABLE_NAME;
    public static final String DROP_CHATS_TABLE = "DROP TABLE IF EXISTS " + ChatsTable.TABLE_NAME;
    public static final String DROP_MESSAGES_TABLE = "DROP TABLE IF EXISTS " + MessagesTable.TABLE_NAME;

    public static abstract class ProfileTable implements BaseColumns {
        public static final String TABLE_NAME = "user_profile";
        public static final String JWT_TOKEN = "jwt_token";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PICTURE = "picture";
    }

    public static abstract class ChatsTable implements BaseColumns {
        public static final String TABLE_NAME = "chats";
        public static final String PEER_NAME = "peer_name";
        public static final String LAST_MESSAGE = "last_message";
        public static final String LAST_MESSAGE_SENT = "last_message_sent";
    }

    public static abstract class MessagesTable implements BaseColumns {
        public static final String TABLE_NAME = "messages";
        public static final String CHAT_ID = "chat_id";
        public static final String FROM = "[from]";
        public static final String BODY = "body";
        public static final String SENT = "sent";
        public static final String STATUS = "status";
    }
}
