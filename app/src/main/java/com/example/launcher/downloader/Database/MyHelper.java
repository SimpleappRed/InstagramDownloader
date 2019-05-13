package com.example.launcher.downloader.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "MY_DB";
    public static final String MEDIA_TABLE_NAME = "AppMedias";
    public static final String TYPE = "type";
    public static final String IMAGE = "image";
    public static final String VIDEO = "video";
    public static final String CAPTION = "caption";

    public MyHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, null, 4);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists " + MEDIA_TABLE_NAME +
                "( id INTEGER PRIMARY KEY AUTOINCREMENT," + TYPE + " TEXT , " + IMAGE + " TEXT, " +
                VIDEO + " TEXT, " + CAPTION + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + MEDIA_TABLE_NAME);
        onCreate(db);
    }
}
