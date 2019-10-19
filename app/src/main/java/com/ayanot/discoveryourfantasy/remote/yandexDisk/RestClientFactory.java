package com.ayanot.discoveryourfantasy.remote.yandexDisk;


public class RestClientFactory {

    public static RestClient getInstance(final Credentials credentials) {
        return new RestClient(credentials);
    }
}
