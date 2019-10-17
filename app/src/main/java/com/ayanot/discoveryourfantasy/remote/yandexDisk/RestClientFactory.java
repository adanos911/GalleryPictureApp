package com.ayanot.discoveryourfantasy.remote.yandexDisk;

import com.yandex.disk.rest.RestClient;

public class RestClientFactory {

    public static RestClient getInstance(final Credentials credentials) {
        return new RestClient(credentials);
    }
}
