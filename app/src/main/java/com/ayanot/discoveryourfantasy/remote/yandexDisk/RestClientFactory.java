package com.ayanot.discoveryourfantasy.remote.yandexDisk;


import com.ayanot.discoveryourfantasy.RestClient;

public class RestClientFactory {

    public static com.ayanot.discoveryourfantasy.RestClient getInstance(final Credentials credentials) {
        return new RestClient(credentials);
    }
}
