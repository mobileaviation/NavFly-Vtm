package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.StatisticsEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Models.Statistics;

import java.io.File;

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

        setClientDownloadStatus();
    }

    private Retrofit retrofit;
    private Context context;
    private DataDownloadStatusEvent clientStatus;
    private DataDownloadStatusEvent responseStatus;

    public void SetProgressStatus(DataDownloadStatusEvent progressStatus)
    {
        this.responseStatus = progressStatus;
    }

    private String TAG = "AirnavClient";

    public void StartDownload()
    {
        StatisticsAPIDataSource statisticsAPIDataSource = new StatisticsAPIDataSource(context, retrofit);
        statisticsAPIDataSource.SetStatusEvent(new StatisticsEvent() {
            @Override
            public void OnFinished(Statistics statistics) {
                if (responseStatus!= null) responseStatus.OnStatistics(statistics);

                // TODO clear databases

                retrieveDatabases(statistics);
            }

            @Override
            public void OnError(String message) {
                Log.i(TAG, "OnError: " + message);
            }
        });

        statisticsAPIDataSource.GetStatistics();
    }

    private void retrieveDatabases(Statistics statistics){
        NavaidAPIDataSource navaidAPIDataSource = new NavaidAPIDataSource(context, retrofit);
        navaidAPIDataSource.SetStatusEvent(clientStatus);
        navaidAPIDataSource.loadNavaids(statistics.NavaidsCount);

        AirportsAPIDataSource airportsAPIDataSource= new AirportsAPIDataSource(context, retrofit);
        airportsAPIDataSource.SetStatusEvent(clientStatus);
        airportsAPIDataSource.loadAirports(statistics.AirportsCount);

        CountriesAPIDataSource countriesAPIDataSource= new CountriesAPIDataSource(context, retrofit);
        countriesAPIDataSource.SetStatusEvent(clientStatus);
        countriesAPIDataSource.loadcountries(statistics.CountriesCount);

        RegionsAPIDataSource regionsAPIDataSource= new RegionsAPIDataSource(context, retrofit);
        regionsAPIDataSource.SetStatusEvent(clientStatus);
        regionsAPIDataSource.loadRegions(statistics.RegionsCount);

        FirsAPIDataSource firsAPIDataSource= new FirsAPIDataSource(context, retrofit);
        firsAPIDataSource.SetStatusEvent(clientStatus);
        firsAPIDataSource.loadfirs(statistics.FirsCount);

        FixesAPIDataSource fixesAPIDataSource= new FixesAPIDataSource(context, retrofit);
        fixesAPIDataSource.SetStatusEvent(clientStatus);
        fixesAPIDataSource.loadfixes(statistics.FixesCount);

        AirspaceAPIDataSource airspaceAPIDataSource= new AirspaceAPIDataSource(context, retrofit);
        airspaceAPIDataSource.SetStatusEvent(clientStatus);
        airspaceAPIDataSource.loadAirspaces(statistics.AirspacesCount);

        MBTilesAPIDataSource mbTilesAPIDataSource= new MBTilesAPIDataSource(context, retrofit);
        mbTilesAPIDataSource.SetStatusEvent(clientStatus);
        mbTilesAPIDataSource.loadTiles(statistics.MBTilesCount);

        CitiesAPIDataSource citiesAPIDataSource = new CitiesAPIDataSource(context, retrofit);
        citiesAPIDataSource.SetStatusEvent(clientStatus);
        citiesAPIDataSource.loadcities(statistics.CitiesCount);
    }

    private void setClientDownloadStatus()
    {
        clientStatus = new DataDownloadStatusEvent() {
            @Override
            public void onProgress(Integer count, Integer downloaded, TableType tableType) {

                float percentage = ((float)downloaded * 100) / (float)count;
                Log.i(TAG, "onProgress: " + tableType.toString() + " : " + Math.round(percentage) + "%");
                if (responseStatus!= null) responseStatus.onProgress(count, downloaded, tableType);
            }

            @Override
            public void OnFinished(TableType tableType) {
                Log.i(TAG, "OnFinished: " + tableType.toString());
                if (responseStatus!= null) responseStatus.OnFinished(tableType);
            }

            @Override
            public void OnError(String message, TableType tableType) {
                Log.i(TAG, "OnError: " + message + " : " + tableType.toString());
                if (responseStatus!= null) responseStatus.OnError(message, tableType);
            }

            @Override
            public void OnStatistics(Statistics statistics)
            {

            }
        };
    }

    public static void deleteDatabaseFile(Context context, String databaseName) {
        File databases = new File(context.getApplicationInfo().dataDir + "/databases");
        File db = new File(databases, databaseName);
        if (db.delete())
            System.out.println("Database deleted");
        else
            System.out.println("Failed to delete database");

        File journal = new File(databases, databaseName + "-journal");
        if (journal.exists()) {
            if (journal.delete())
                System.out.println("Database journal deleted");
            else
                System.out.println("Failed to delete database journal");
        }
    }
}
