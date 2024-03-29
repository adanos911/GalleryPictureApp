package com.ayanot.discoveryourfantasy.helpUtil;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * <h3>Класс, дял работы с bitmap изображениями</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class BitmapHelper {

    public static byte[] getBytesArray(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        byte[] bytes = null;
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            bitmap.compress(compressFormat, 100, outputStream);
            bytes = outputStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Bitmap getBitmap(byte[] byteArray) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
    }
}
