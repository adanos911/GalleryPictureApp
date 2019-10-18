package com.ayanot.discoveryourfantasy;

import com.squareup.okhttp.OkHttpClient;
import com.yandex.disk.rest.Credentials;
import com.yandex.disk.rest.exceptions.NetworkIOException;
import com.yandex.disk.rest.exceptions.ServerIOException;
import com.yandex.disk.rest.json.Link;
import com.yandex.disk.rest.retrofit.CloudApi;

import java.lang.reflect.Field;

public class RestClient extends com.yandex.disk.rest.RestClient {
    private CloudApi cloudApi;

    public RestClient(Credentials credentials) {
        super(credentials);
        setCloudApi();
    }

    public RestClient(Credentials credentials, OkHttpClient client) {
        super(credentials, client);
        setCloudApi();
    }

    public RestClient(Credentials credentials, OkHttpClient client, String serverUrl) {
        super(credentials, client, serverUrl);
        setCloudApi();
    }

    public Link getDownloadLink(final String path) throws NetworkIOException, ServerIOException {
        return cloudApi.getDownloadLink(path);
    }

    private void setCloudApi() {
        try {
            Field cloudApiField = com.yandex.disk.rest.RestClient.class.getDeclaredField("cloudApi");
            cloudApiField.setAccessible(true);
            this.cloudApi = (CloudApi) cloudApiField.get(this);
            cloudApiField.setAccessible(false);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
