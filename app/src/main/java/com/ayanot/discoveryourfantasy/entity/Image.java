package com.ayanot.discoveryourfantasy.entity;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Image implements Parcelable {

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source.readInt(), source.readString(), source.readString(),
                    source.readString(), source.readString(), Bitmap.CREATOR.createFromParcel(source));
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    private String name;
    private String preview;
    private String href;
    private String path;
    private long id;
    private Bitmap bitmap;

    public Image(String name, String preview) {
        this(name, preview, "", "");
    }

    public Image(String name, String preview, String href) {
        this(name, preview, href, "");
    }

    public Image(String name, String preview, String href, String path) {
        this.name = name;
        this.preview = preview;
        this.href = href;
        this.path = path;
    }

    public Image(long id, String name, String preview, String href, String path, Bitmap bitmap) {
        this.id = id;
        this.name = name;
        this.preview = preview;
        this.href = href;
        this.path = path;
        this.bitmap = bitmap;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeString(name);
        dest.writeString(preview);
        dest.writeString(href);
        dest.writeString(path);
        dest.writeValue(bitmap);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getHref() {
        return href;
    }
    public void setHref(String href) {
        this.href = href;
    }
    public String getPreview() {
        return preview;
    }
    public void setPreview(String preview) {
        this.preview = preview;
    }
    public String getPath() {
        return path;
    }
    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @NonNull
    @Override
    public String toString() {
        return "name = " + name + "\n"
                + "id = " + id + "\n"
                + "preview = " + preview + "\n"
                + "href = " + href + "\n"
                + "path = " + path + "\n"
                + "bitmap = " + bitmap.toString();
    }
}
