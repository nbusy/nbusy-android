package com.nbusy.app.data.sqldb;

import android.provider.BaseColumns;

public class UserProfileTable implements BaseColumns {
    public static final String TABLE_NAME = "user_profile";
    public static final String USER_ID = "user_id";
    public static final String JWT_TOKEN = "jwt_token";
    public static final String NAME = "name";
    public static final String EMAIL = "email";
    public static final String PICTURE = "picture";
    public static final String CHATS = "chats";
}
