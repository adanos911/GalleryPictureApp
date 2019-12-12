package com.ayanot.discoveryourfantasy.remote.yandexDisk;


/**
 * <h3>Класс, создающий клиента для взаимодействия с yandex disk
 * {@link com.ayanot.discoveryourfantasy.MainActivity#REST_CLIENT}</h3>
 *
 * @author ivan
 * @version 0.0.1
 */
public class RestClientFactory {

    public static RestClient getInstance(final Credentials credentials) {
        return new RestClient(credentials);
    }
}
