package com.nbusy.app.data.sqldb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.nbusy.app.Config;
import com.nbusy.app.InstanceManager;

public class SQLDBHelper extends SQLiteOpenHelper {

    private static final String TAG = SQLDBHelper.class.getSimpleName();
    private static final int DATABASE_VERSION = 1; // increment this whenever schema changes
    private static final String DATABASE_NAME = "nbusy.db";
    private final Config config;

    public SQLDBHelper(Context context, Config config) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.config = config;
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        if (config.env == Config.Env.TEST) {
            db.execSQL(SQLTables.SQL_DELETE_ENTRIES);
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQLTables.SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // if migration script does not work, start over
        db.execSQL(SQLTables.SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // we don't support downgrade so call base which throws an exception
        super.onDowngrade(db, oldVersion, newVersion);
    }
}
