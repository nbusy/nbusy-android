package com.nbusy.app.data.sqldb;

import android.provider.BaseColumns;

public final class SQLTables {

    private SQLTables() {
    }

    private static final String COMMA_SEP = ",";

    private static final String TEXT_TYPE = " TEXT";
    private static final String BLOB_TYPE = " BLOB";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String REAL_TYPE = " REAL";

    public static final String CREATE_PROFILE_TABLE =
            "CREATE TABLE " + ProfileTable.TABLE_NAME + " (" +
                    ProfileTable._ID + " TEXT PRIMARY KEY," +
                    ProfileTable.JWT_TOKEN + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.NAME + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.EMAIL + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.PICTURE + BLOB_TYPE +
                    ")";

    public static final String CREATE_CHAT_TABLE =
            "CREATE TABLE " + ChatTable.TABLE_NAME + " (" +
                    ChatTable._ID + " TEXT PRIMARY KEY," +
                    ChatTable.PEER_NAME + TEXT_TYPE + COMMA_SEP +
                    ChatTable.LAST_MESSAGE + TEXT_TYPE + COMMA_SEP +
                    ChatTable.LAST_MESSAGE_SENT + INTEGER_TYPE +
                    ")";

    public static final String CREATE_MESSAGE_TABLE =
            "CREATE TABLE " + MessageTable.TABLE_NAME + " (" +
                    MessageTable._ID + " TEXT PRIMARY KEY," +
                    MessageTable.CHAT_ID + TEXT_TYPE + COMMA_SEP +
                    MessageTable.FROM + TEXT_TYPE + COMMA_SEP +
                    MessageTable.BODY + TEXT_TYPE + COMMA_SEP +
                    MessageTable.SENT + TEXT_TYPE + COMMA_SEP +
                    MessageTable.STATUS + TEXT_TYPE + /*COMMA_SEP +
                    "FOREIGN KEY(" + MessageTable.CHAT_ID + ") REFERENCES " + ChatTable.TABLE_NAME + "(" + ChatTable._ID + ")" +*/ // todo: can't use this yet: http://stackoverflow.com/questions/13311727/android-sqlite-insert-or-update#comment47866111_13342175
                    ")";

    public static final String DROP_PROFILE_TABLE = "DROP TABLE IF EXISTS " + ProfileTable.TABLE_NAME;
    public static final String DROP_CHAT_TABLE = "DROP TABLE IF EXISTS " + ChatTable.TABLE_NAME;
    public static final String DROP_MESSAGE_TABLE = "DROP TABLE IF EXISTS " + MessageTable.TABLE_NAME;

    public static abstract class ProfileTable implements BaseColumns {
        public static final String TABLE_NAME = "user_profile";
        public static final String JWT_TOKEN = "jwt_token";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PICTURE = "picture";
    }

    public static abstract class ChatTable implements BaseColumns {
        public static final String TABLE_NAME = "chat";
        public static final String PEER_NAME = "peer_name";
        public static final String LAST_MESSAGE = "last_message";
        public static final String LAST_MESSAGE_SENT = "last_message_sent";
    }

    public static abstract class MessageTable implements BaseColumns {
        public static final String TABLE_NAME = "message";
        public static final String CHAT_ID = "chat_id";
        public static final String FROM = "_from";
        public static final String BODY = "body";
        public static final String SENT = "sent";
        public static final String STATUS = "status";
    }
}
