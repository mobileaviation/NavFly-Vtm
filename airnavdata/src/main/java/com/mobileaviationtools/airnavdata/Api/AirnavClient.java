package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.os.AsyncTask;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AirnavClient {
    public AirnavClient(Context context)
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(10);
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.mobileaviationtools.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        this.context = context;
    }

    private Retrofit retrofit;
    private Context context;

    public void StartDownload(){
        NavaidAPIDataSource navaidAPIDataSource = new NavaidAPIDataSource(context, retrofit);
        navaidAPIDataSource.loadNavaids(11117);

        AirportsAPIDataSource airportsAPIDataSource= new AirportsAPIDataSource(context, retrofit);
        airportsAPIDataSource.loadAirports(54523);
    }
}
