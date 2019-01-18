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

    public void StartDownload(final String continent)
    {
        StatisticsAPIDataSource statisticsAPIDataSource = new StatisticsAPIDataSource(context, retrofit);
        statisticsAPIDataSource.SetStatusEvent(new StatisticsEvent() {
            @Override
            public void OnFinished(Statistics statistics) {
                if (responseStatus!= null) responseStatus.OnStatistics(statistics);

                // TODO clear databases

                retrieveDatabases(statistics, continent);
            }

            @Override
            public void OnError(String message) {
                Log.i(TAG, "OnError: " + message);
                if (responseStatus != null) responseStatus.OnError(message, TableType.statistics);
            }
        });

        if (continent.length()==0)
            statisticsAPIDataSource.GetStatistics();
        else
            statisticsAPIDataSource.GetStatisticsByContinent(continent);
    }

    public void StartDownloadIndividualTable(TableType tableType, Statistics statistics, String continent)
    {
        retrieveDatabaseByType(statistics, continent, tableType);
    }

    private void retrieveNavaids(Statistics statistics, String continent)
    {
        NavaidAPIDataSource navaidAPIDataSource = new NavaidAPIDataSource(context, retrofit);
        navaidAPIDataSource.SetStatusEvent(clientStatus);
        if (continent.length()==0) {
            navaidAPIDataSource.loadNavaids(statistics.NavaidsCount);
        }
        else
        {
            navaidAPIDataSource.loadNavaidsByContinent(statistics.NavaidsCount, continent);
        }
    }

    private void retrieveAirports(Statistics statistics, String continent)
    {
        AirportsAPIDataSource airportsAPIDataSource= new AirportsAPIDataSource(context, retrofit);
        airportsAPIDataSource.SetStatusEvent(clientStatus);
        if (continent.length()==0) {
            airportsAPIDataSource.loadAirports(statistics.AirportsCount);
        }
        else
        {
            airportsAPIDataSource.loadAirportsByContinent(statistics.AirportsCount, continent);
        }
    }

    private void retrieveCountries(Statistics statistics, String continent) {
        CountriesAPIDataSource countriesAPIDataSource = new CountriesAPIDataSource(context, retrofit);
        countriesAPIDataSource.SetStatusEvent(clientStatus);
        countriesAPIDataSource.loadcountries(statistics.CountriesCount);
    }

    private void retrieveRegions(Statistics statistics, String continent) {
        RegionsAPIDataSource regionsAPIDataSource= new RegionsAPIDataSource(context, retrofit);
        regionsAPIDataSource.SetStatusEvent(clientStatus);
        regionsAPIDataSource.loadRegions(statistics.RegionsCount);
    }

    private void retrieveFirs(Statistics statistics, String continent) {
        FirsAPIDataSource firsAPIDataSource = new FirsAPIDataSource(context, retrofit);
        firsAPIDataSource.SetStatusEvent(clientStatus);
        firsAPIDataSource.loadfirs(statistics.FirsCount);
    }

    private void retrieveFixes(Statistics statistics, String continent) {
        FixesAPIDataSource fixesAPIDataSource= new FixesAPIDataSource(context, retrofit);
        fixesAPIDataSource.SetStatusEvent(clientStatus);
        if (continent.length()==0) {
            fixesAPIDataSource.loadfixes(statistics.FixesCount);
        }
        else
        {
            fixesAPIDataSource.loadfixesByContinent(statistics.FixesCount, continent);
        }
    }

    private void retrieveAirspaces(Statistics statistics, String continent)
    {
        AirspaceAPIDataSource airspaceAPIDataSource= new AirspaceAPIDataSource(context, retrofit);
        airspaceAPIDataSource.SetStatusEvent(clientStatus);
        if (continent.length()==0) {
            airspaceAPIDataSource.loadAirspaces(statistics.AirspacesCount);
        }
        else
        {
            airspaceAPIDataSource.loadAirspacesByContinent(statistics.AirspacesCount, continent);
        }
    }

    private void retrieveCharts(Statistics statistics, String continent)
    {
        MBTilesAPIDataSource mbTilesAPIDataSource= new MBTilesAPIDataSource(context, retrofit);
        mbTilesAPIDataSource.SetStatusEvent(clientStatus);
        mbTilesAPIDataSource.loadTiles(statistics.MBTilesCount);
    }

    private void retrieveCities(Statistics statistics, String continent)
    {
        CitiesAPIDataSource citiesAPIDataSource = new CitiesAPIDataSource(context, retrofit);
        citiesAPIDataSource.SetStatusEvent(clientStatus);
        if (continent.length()==0) {
            citiesAPIDataSource.loadcities(statistics.CitiesCount);
        }
        else
        {
            citiesAPIDataSource.loadcitiesByContinent(statistics.CitiesCount, continent);
        }
    }

    private void retrieveDatabaseByType(Statistics statistics, String continent, TableType tableType)
    {
        switch (tableType) {
            case airports:
                retrieveAirports(statistics, continent);
                break;
            case airspaces:
                retrieveAirspaces(statistics, continent);
                break;
            case firs:
                retrieveFirs(statistics, continent);
                break;
            case fixes:
                retrieveFixes(statistics,continent);
                break;
            case navaids:
                retrieveNavaids(statistics, continent);
                break;
            case countries:
                retrieveCountries(statistics, continent);
                break;
            case regions:
                retrieveRegions(statistics, continent);
                break;
            case mbtiles:
                retrieveCharts(statistics, continent);
                break;
            case cities:
                retrieveCities(statistics, continent);
                break;
        }
    }

    private void retrieveDatabases(Statistics statistics, String continent){
        retrieveNavaids(statistics, continent);
        retrieveAirports(statistics, continent);
        retrieveCountries(statistics, continent);
        retrieveFirs(statistics, continent);
        retrieveAirspaces(statistics, continent);
        retrieveCharts(statistics, continent);
        retrieveCities(statistics, continent);
        retrieveFixes(statistics, continent);
        retrieveRegions(statistics, continent);
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
            public void OnFinished(TableType tableType, String continent) {
                Log.i(TAG, "OnFinished: " + tableType.toString());
                if (responseStatus!= null) responseStatus.OnFinished(tableType, continent);
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
