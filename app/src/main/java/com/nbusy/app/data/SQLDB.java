package com.nbusy.app.data;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLDB extends SQLiteOpenHelper {

    private static final String TAG = SQLDB.class.getSimpleName();
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "nbusy";
    private SQLiteDatabase db;

    public SQLDB(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        this.db = db;

        // todo: https://github.com/android/platform_development/blob/master/samples/NotePad/src/com/example/android/notepad/NotePadProvider.java
//        db.execSQL("CREATE TABLE " + NotePad.Notes.TABLE_NAME + " ("
//                + NotePad.Notes._ID + " INTEGER PRIMARY KEY,"
//                + NotePad.Notes.COLUMN_NAME_TITLE + " TEXT,"
//                + NotePad.Notes.COLUMN_NAME_NOTE + " TEXT,"
//                + NotePad.Notes.COLUMN_NAME_CREATE_DATE + " INTEGER,"
//                + NotePad.Notes.COLUMN_NAME_MODIFICATION_DATE + " INTEGER"
//                + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
