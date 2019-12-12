package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * <h3>Класс, создающий полномочия, для подключения к yandex disk
 * Для создания {@link Credentials#Credentials(String user, String token)}
 * необходим корректный логин user(обычно это почта)
 * {@link com.ayanot.discoveryourfantasy.MainActivity#USER_NAME}
 * и OAuth токен token для доступа к yandex disk
 * {@link com.ayanot.discoveryourfantasy.MainActivity#TOKEN}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class Credentials extends com.yandex.disk.rest.Credentials implements Parcelable {

    public static final Parcelable.Creator<Credentials> CREATOR =
            new Parcelable.Creator<Credentials>() {
                @Override
                public Credentials createFromParcel(Parcel source) {
                    return new Credentials(source.readString(), source.readString());
                }

                @Override
                public Credentials[] newArray(int size) {
                    return new Credentials[size];
                }
            };

    public Credentials(String user, String token) {
        super(user, token);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(user);
        dest.writeString(token);
    }
}
