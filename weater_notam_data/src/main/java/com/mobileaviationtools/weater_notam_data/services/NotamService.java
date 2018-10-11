package com.mobileaviationtools.weater_notam_data.services;

import android.util.Log;

import com.google.gson.Gson;
import com.mobileaviationtools.weater_notam_data.notams.NotamResponseEvent;
import com.mobileaviationtools.weater_notam_data.notams.Notams;

import org.oscim.core.GeoPoint;
import org.oscim.core.GeoPointConvertion;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NotamService {
    public NotamService()
    {

    }

    private String TAG = "NotamService";
    private String aafUrl = "https://notams.aim.faa.gov/notamSearch/search";

    public void GetNotamsByLocationAndRadius(GeoPoint location, Long radius, NotamResponseEvent notamResponseEvent)
    {
        MediaType FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        String q = getQuery(3, "", location, radius);
        RequestBody requestBody = RequestBody.create(FROM, q);
        Request request = getRequest(requestBody);
        doCall(request, notamResponseEvent);
    }

    public void GetNotamsByICAO(String icao, NotamResponseEvent notamResponseEvent)
    {
        MediaType FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        String q = getQuery(0, icao, null, 100l);
        RequestBody requestBody = RequestBody.create(FROM, q);
        Request request = getRequest(requestBody);
        doCall(request, notamResponseEvent);
    }

    private void doCall(Request request, final NotamResponseEvent event)
    {
        Call call = GetHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "NotamsCall error: " + e.getMessage());
                if (event != null) event.OnFailure("NotamsCall error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String notamsJson = response.body().string();
                Notams notams = new Gson().fromJson(notamsJson, Notams.class);
                Log.i(TAG, "Retrieved Notams");
                if (event != null) event.OnNotamsResponse(notams, response.message());
            }
        });
    }

    private Request getRequest(RequestBody requestBody)
    {
        Request request = new Request.Builder()
                .url(aafUrl)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("origin", "https://notams.aim.faa.gov")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        return request;
    }

    private OkHttpClient GetHttpClient()
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(10);

        return client;
    }

    private String getQuery(Integer searchType, String icao, GeoPoint location, Long radius)
    {
        // searchtype 0 = designatorsForLocation = {icao code}
        // searchtype 3 = lat/lon/radius

        if (location != null)
        {
            GeoPointConvertion geoPointConvertion = new GeoPointConvertion();
            GeoPointConvertion.GeoPointDMS gmsGeoPoint = geoPointConvertion.getGeoPointDMS(location);
            Log.i(TAG, "test");
        }

        String query = "searchType=" + searchType.toString() +
                "&designatorsForLocation=" + icao +
                "&designatorForAccountable=" +
                "&latDegrees=" +
                "&latMinutes=0" +
                "&latSeconds=0" +
                "&longDegrees=" +
                "&longMinutes=0" +
                "&longSeconds=0" +
                "&radius=" + radius.toString() +
                "&sortColumns=5+false" +
                "&sortDirection=true" +
                "&designatorForNotamNumberSearch=" +
                "&notamNumber=" +
                "&radiusSearchOnDesignator=false" +
                "&radiusSearchDesignator=" +
                "&latitudeDirection=N" +
                "&longitudeDirection=W" +
                "&freeFormText=" +
                "&flightPathText=" +
                "&flightPathDivertAirfields=" +
                "&flightPathBuffer=4" +
                "&flightPathIncludeNavaids=true" +
                "&flightPathIncludeArtcc=false" +
                "&flightPathIncludeTfr=true" +
                "&flightPathIncludeRegulatory=false" +
                "&flightPathResultsType=All+NOTAMs" +
                "&archiveDate=" +
                "&archiveDesignator=" +
                "&offset=0" +
                "&notamsOnly=false" +
                "&filters=" +
                "&minRunwayLength=" +
                "&minRunwayWidth=" +
                "&runwaySurfaceTypes=" +
                "&predefinedAbraka=" +
                "&predefinedDabra=" +
                "&flightPathAddlBuffer=";

        return query;
    }
}
