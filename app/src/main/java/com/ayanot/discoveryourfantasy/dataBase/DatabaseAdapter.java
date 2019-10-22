package com.ayanot.discoveryourfantasy.dataBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ayanot.discoveryourfantasy.entity.Image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAdapter {

    private DatabaseHandler databaseHandler;
    private SQLiteDatabase database;

    public DatabaseAdapter(Context context) {
        databaseHandler = new DatabaseHandler(context.getApplicationContext());
    }

    public DatabaseAdapter open() {
        database = databaseHandler.getWritableDatabase();
        return this;
    }

    public void close() {
        databaseHandler.close();
    }

    public void refresh() {
        databaseHandler.onUpgrade(database, 1, 2);
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
        return DatabaseUtils.queryNumEntries(database, DatabaseHandler.TABLE);
    }

    public Image getImage(long id) {
        Image image = null;
        String query = String.format("select * from %s where %s=?", DatabaseHandler.TABLE, DatabaseHandler.COLUMN_ID);
        Cursor cursor = database.rawQuery(query, new String[]{String.valueOf(id)});
        if (cursor.moveToFirst()) {
            image = getImageIntoCursor(cursor);
        }
        cursor.close();
        return image;
    }

    public long insert(Image image) {
        return database.insert(DatabaseHandler.TABLE, null, getContentValues(image));
    }

    public long delete(long id) {
        String whereClause = "_id = ?";
        String[] whereArgs = new String[]{String.valueOf(id)};
        return database.delete(DatabaseHandler.TABLE, whereClause, whereArgs);
    }

    public long update(Image image) {
        String whereClause = DatabaseHandler.COLUMN_ID + "=" + String.valueOf(image.getId());
        return database.update(DatabaseHandler.TABLE, getContentValues(image), whereClause, null);
    }

    private Cursor getAllEntries() {
        String[] columns = new String[]{DatabaseHandler.COLUMN_ID, DatabaseHandler.COLUMN_NAME,
                DatabaseHandler.COLUMN_PREVIEW, DatabaseHandler.COLUMN_HREF, DatabaseHandler.COLUMN_PATH,
                DatabaseHandler.COLUMN_IMAGE_BLOB};
        return database.query(DatabaseHandler.TABLE, columns,
                null, null, null, null, null);
    }

    private Image getImageIntoCursor(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(DatabaseHandler.COLUMN_ID));
        String name = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_NAME));
        String preview = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_PREVIEW));
        String href = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_HREF));
        String path = cursor.getString(cursor.getColumnIndex(DatabaseHandler.COLUMN_PATH));
        byte[] blob = cursor.getBlob(cursor.getColumnIndex(DatabaseHandler.COLUMN_IMAGE_BLOB));
        return new Image(id, name, preview, href, path,
                BitmapFactory.decodeByteArray(blob, 0, blob.length));
    }

    private ContentValues getContentValues(Image image) {
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHandler.COLUMN_NAME, image.getName());
        cv.put(DatabaseHandler.COLUMN_PREVIEW, image.getPreview());
        cv.put(DatabaseHandler.COLUMN_HREF, image.getHref());
        cv.put(DatabaseHandler.COLUMN_PATH, image.getPath());
        cv.put(DatabaseHandler.COLUMN_IMAGE_BLOB, getBytesFromBitmap(image.getBitmap()));
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
