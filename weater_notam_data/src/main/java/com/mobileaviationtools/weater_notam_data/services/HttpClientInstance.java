package com.mobileaviationtools.weater_notam_data.services;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class HttpClientInstance {
    public HttpClientInstance()
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        this.client = builder.build();
    }

    private static HttpClientInstance instance;
    private OkHttpClient client;

    public static OkHttpClient getClient()
    {
        if (instance == null)
        {
            instance = new HttpClientInstance();
        }

        return instance.client;
    }
}
