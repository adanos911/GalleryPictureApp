package com.ayanot.discoveryourfantasy.dataBase.cache;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.ayanot.discoveryourfantasy.entity.Image;

/**
 * <h3>Класс, создающий БД, реализует паттер сингелтон
 * Создает БД для хранения кеша изображения
 * Используется библиотека {@link androidx.room}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
@Database(entities = {Image.class}, version = 1)
public abstract class ImageDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "images_db";
    private static ImageDatabase instance;

    public static synchronized ImageDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), ImageDatabase.class,
                    DATABASE_NAME)
                    .enableMultiInstanceInvalidation()
                    .build();
        }
        return instance;
    }

    public abstract ImageDao imageDao();
}
