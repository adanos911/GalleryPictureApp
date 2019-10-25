package com.ayanot.discoveryourfantasy.dataBase.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import com.ayanot.discoveryourfantasy.entity.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
    }

    public DatabaseAdapter open() {
        database = databaseHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHelper.close();
    }

    public void refresh() {
        databaseHelper.onUpgrade(database, 1, 2);
    }

    public List<Image> getImages() {
        List<Image> images = new ArrayList<>();
        Cursor cursor = getAllEntries();
        if (cursor.moveToFirst()) {
            do {
                images.add(getImageIntoCursor(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return images;
    }

    public long getCount() {
        return DatabaseUtils.queryNumEntries(database, DatabaseHelper.TABLE);
    }

    public Image getImage(long id) {
        Image image = null;
        String query = String.format("select * from %s where %s=?",
                DatabaseHelper.TABLE, DatabaseHelper.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            image = getImageIntoCursor(cursor);
        }
        cursor.close();
        return image;
    }

    public long insert(Image image) {
        return database.insert(DatabaseHelper.TABLE, null, getContentValues(image));
    }

    public long delete(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return database.delete(DatabaseHelper.TABLE, whereClause, whereArgs);
    }

    public long update(Image image) {
        String whereClause = DatabaseHelper.COLUMN_ID + "=" + String.valueOf(image.getId());
        return database.update(DatabaseHelper.TABLE, getContentValues(image), whereClause, null);
    }

    private Cursor getAllEntries() {
        String[] columns = new String[]{DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_NAME,
                DatabaseHelper.COLUMN_PREVIEW, DatabaseHelper.COLUMN_HREF, DatabaseHelper.COLUMN_PATH,
                DatabaseHelper.COLUMN_IMAGE_BLOB};
        return database.query(DatabaseHelper.TABLE, columns,
                null, null, null, null, null);
    }

    private Image getImageIntoCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NAME));
        String preview = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PREVIEW));
        String href = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_HREF));
        String path = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PATH));
        byte[] blob = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_IMAGE_BLOB));
        return new Image(id, name, preview, href, path, blob);
    }

    private ContentValues getContentValues(Image image) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COLUMN_NAME, image.getName());
        cv.put(DatabaseHelper.COLUMN_PREVIEW, image.getPreview());
        cv.put(DatabaseHelper.COLUMN_HREF, image.getHref());
        cv.put(DatabaseHelper.COLUMN_PATH, image.getPath());
        cv.put(DatabaseHelper.COLUMN_IMAGE_BLOB, image.getBitmap());
        return cv;
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                return outputStream.toByteArray();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }


}
