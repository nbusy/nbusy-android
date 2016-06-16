package com.nbusy.app.data.sqldb;

import android.provider.BaseColumns;

public final class SQLTables {

    private SQLTables() {}

    public static abstract class ProfileTable implements BaseColumns {
        public static final String TABLE_NAME = "user_profile";
        public static final String USER_ID = "user_id";
        public static final String JWT_TOKEN = "jwt_token";
        public static final String NAME = "name";
        public static final String EMAIL = "email";
        public static final String PICTURE = "picture";
        public static final String CHATS = "chats";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ProfileTable.TABLE_NAME + " (" +
                    ProfileTable._ID + " INTEGER PRIMARY KEY," +
                    ProfileTable.USER_ID + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.JWT_TOKEN + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.NAME + TEXT_TYPE + COMMA_SEP +
                    ProfileTable.EMAIL + TEXT_TYPE + COMMA_SEP +
//                    ProfileTable.PICTURE + IMAGE_TYPE + COMMA_SEP +
//                    ProfileTable.CHATS + REF_TYPE + COMMA_SEP +
            " )";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ProfileTable.TABLE_NAME;
}
