package com.example.launcher.downloader.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.launcher.downloader.Models.Media;
import com.example.launcher.downloader.MyAdapter;

import java.util.ArrayList;
import java.util.List;

public class MediaDA {

    SQLiteOpenHelper sqLiteOpenHelper;
    SQLiteDatabase sqLiteDatabase;
    String column_names[] = new String[]{"id", MyHelper.TYPE, MyHelper.IMAGE, MyHelper.VIDEO, MyHelper.CAPTION};

    public MediaDA(Context context) {
        this.sqLiteOpenHelper = new MyHelper(context, MyHelper.DB_NAME, null, 1);

    }

    public void addMedia(Media media) {
        ContentValues values = new ContentValues();
        values.put(MyHelper.TYPE, media.getType());
        values.put(MyHelper.CAPTION, media.getCaption());
        values.put(MyHelper.IMAGE, media.getImage());
        values.put(MyHelper.VIDEO, media.getVideo());
        sqLiteDatabase.insert(MyHelper.MEDIA_TABLE_NAME, null, values);

    }

    public ArrayList<Media> getALLMedias() {
        ArrayList<Media> list = new ArrayList<>();
        Cursor cursor = sqLiteDatabase.query(MyHelper.MEDIA_TABLE_NAME, column_names, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            Media media;
            do {
                media = new Media();
                media.setId(cursor.getInt(cursor.getColumnIndex("id")));
                media.setImage(cursor.getString(cursor.getColumnIndex(MyHelper.IMAGE)));
                media.setVideo(cursor.getString(cursor.getColumnIndex(MyHelper.VIDEO)));
                media.setCaption(cursor.getString(cursor.getColumnIndex(MyHelper.CAPTION)));
                media.setType(cursor.getString(cursor.getColumnIndex(MyHelper.TYPE)));
                list.add(media);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public Media getMediaById(int id) {
        Cursor cursor = sqLiteDatabase.query(MyHelper.MEDIA_TABLE_NAME, column_names, "id=?", new String[]{String.valueOf(id)}, null, null, null);
        Media media = new Media();
        if (cursor.moveToFirst()) {
            media.setImage(cursor.getString(cursor.getColumnIndex(MyHelper.IMAGE)));
            media.setVideo(cursor.getString(cursor.getColumnIndex(MyHelper.VIDEO)));
            media.setCaption(cursor.getString(cursor.getColumnIndex(MyHelper.CAPTION)));
            media.setType(cursor.getString(cursor.getColumnIndex(MyHelper.TYPE)));
        }

        cursor.close();
        return media;
    }

    public void deleteMedia(int id) {
        try {
            sqLiteDatabase.delete(MyHelper.MEDIA_TABLE_NAME, "id=?", new String[]{String.valueOf(id)});
        } catch (Exception ignored) {

        }

    }

    public void open() {
        sqLiteDatabase = sqLiteOpenHelper.getWritableDatabase();
    }

    public void close() {
        sqLiteDatabase.close();
    }

}
