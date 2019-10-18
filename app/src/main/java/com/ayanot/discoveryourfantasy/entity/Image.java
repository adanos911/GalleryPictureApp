package com.ayanot.discoveryourfantasy.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Image implements Parcelable {
    private String name;
    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel source) {
            return new Image(source.readString(), source.readString(), source.readString(), source.readString());
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    private String preview;
    private String href;
    private String path;

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(preview);
        dest.writeString(href);
        dest.writeString(path);
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
}
