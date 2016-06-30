package com.nbusy.app.data.sqldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLDBHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLDBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1; // increment this whenever schema changes
    private static final String DATABASE_NAME = "nbusy.db";

    public SQLDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void dropDB(SQLiteDatabase db) {
        db.execSQL(SQLTables.DROP_PROFILE_TABLE);
        db.execSQL(SQLTables.CREATE_PROFILE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLTables.CREATE_PROFILE_TABLE);
        Log.i(TAG, "created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // if migration script does not work, start over
        dropDB(db);
        Log.i(TAG, "upgraded");
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // we don't support downgrade so call base which throws an exception
        super.onDowngrade(db, oldVersion, newVersion);
        Log.i(TAG, "downgraded");
    }
}
