package com.ayanot.discoveryourfantasy.dataBase.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "images";
    private static final int SCHEMA = 1;
    static final String TABLE = "cache";

    static final String COLUMN_ID = "_id";
    static final String COLUMN_NAME = "name";
    static final String COLUMN_PREVIEW = "preview";
    static final String COLUMN_HREF = "href";
    static final String COLUMN_PATH = "path";
    static final String COLUMN_IMAGE_BLOB = "image";

    DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT, "
                + COLUMN_PREVIEW + " TEXT, "
                + COLUMN_HREF + " TEXT, "
                + COLUMN_PATH + " TEXT, "
                + COLUMN_IMAGE_BLOB + " BLOB);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
