package com.mobileaviationtools.weater_notam_data.services;

import android.util.Log;

import org.oscim.core.GeoPoint;

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

    public void GetNotamsByICAO(String icao)
    {
        MediaType FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        String q = getQuery(0, icao, null, 100l);
        RequestBody requestBody = RequestBody.create(FROM, q);
        Request request = getRequest(requestBody);
        doCall(request);
    }

    private void doCall(Request request)
    {
        Call call = GetHttpClient().newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "NotamsCall error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String notamsJson = response.body().string();
                Log.i(TAG, "Retrieved Notams");
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
