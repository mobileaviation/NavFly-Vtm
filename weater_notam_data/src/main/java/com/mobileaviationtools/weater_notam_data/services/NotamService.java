package com.mobileaviationtools.weater_notam_data.services;

import android.util.Log;

import com.google.gson.Gson;
import com.mobileaviationtools.weater_notam_data.notams.NotamCount;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;
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
    private String aafUrl = "https://notams.aim.faa.gov/notamSearch/";

    public void GetNotamsByLocationAndRadius(GeoPoint location, Long radius, NotamResponseEvent notamResponseEvent)
    {
        getByLocationAndRadius(location, radius, notamResponseEvent, "search");
    }
    public void GetCountsByLocationAndRadius(GeoPoint location, Long radius, NotamResponseEvent notamResponseEvent)
    {
        getByLocationAndRadius(location, radius, notamResponseEvent, "counts");
    }

    private void getByLocationAndRadius(GeoPoint location, Long radius, NotamResponseEvent notamResponseEvent, String command)
    {
        MediaType FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        String q = getQuery(3, "", location, radius, 0l);
        RequestBody requestBody = RequestBody.create(FROM, q);
        Request request = getRequest(requestBody, command);
        if (command.equals("search")) doNotamCall(request, notamResponseEvent, null);
        if (command.equals("counts")) doCountsCall(request, notamResponseEvent);
    }

    public void GetNotamsByICAO(String icao, NotamResponseEvent notamResponseEvent)
    {
        MediaType FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        String q = getQuery(0, icao, null, 100l, 0l);
        RequestBody requestBody = RequestBody.create(FROM, q);
        Request request = getRequest(requestBody, "search");
        doNotamCall(request, notamResponseEvent, null);
    }

    public void GetNotamsByCount(NotamCount count, NotamResponseEvent notamResponseEvent)
    {
        MediaType FROM = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
        String q = getQuery(0, count.icaoId, null, 100l, 0l);
        RequestBody requestBody = RequestBody.create(FROM, q);
        Request request = getRequest(requestBody, "search");
        doNotamCall(request, notamResponseEvent, count);
    }

    private void doCountsCall(Request request, final NotamResponseEvent event)
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
                NotamCounts counts = new Gson().fromJson(notamsJson, NotamCounts.class);
                Log.i(TAG, "Retrieved Notam Counts");
                if (event != null) event.OnNotamsCountResponse(counts, response.message());
            }
        });
    }

    private void doNotamCall(Request request, final NotamResponseEvent event, final NotamCount count)
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
                count.notams = notams;
                if (event != null) event.OnNotamsResponse(notams, count, response.message());
            }
        });
    }

    private Request getRequest(RequestBody requestBody, String command)
    {
        Request request = new Request.Builder()
                .url(aafUrl + command)
                .post(requestBody)
                .addHeader("accept", "application/json")
                .addHeader("origin", "https://notams.aim.faa.gov")
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .build();

        return request;
    }

    private OkHttpClient GetHttpClient()
    {
//        Dispatcher dispatcher = new Dispatcher();
//        dispatcher.setMaxRequests(10);
//        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        builder.dispatcher(dispatcher);
//
//        OkHttpClient client = builder.build();
//        client.dispatcher().setMaxRequests(10);

        return HttpClientInstance.getClient();
    }

    private String getQuery(Integer searchType, String icao, GeoPoint location, Long radius, Long offset)
    {
        // searchtype 0 = designatorsForLocation = {icao code}
        // searchtype 3 = lat/lon/radius

        GeoPointConvertion geoPointConvertion = new GeoPointConvertion();
        GeoPointConvertion.GeoPointDMS gmsGeoPoint = geoPointConvertion.getNewGeoPointDMS();

        if (location != null)
        {
            gmsGeoPoint = geoPointConvertion.getGeoPointDMS(location);
            Log.i(TAG, "test");
        }

        String query = "searchType=" + searchType.toString() +
                "&designatorsForLocation=" + icao +
                "&designatorForAccountable=" +
                "&latDegrees=" + Math.abs(gmsGeoPoint.latitude.degrees) +
                "&latMinutes=" + Math.abs(gmsGeoPoint.latitude.minutes) +
                "&latSeconds=" + Math.abs(gmsGeoPoint.latitude.seconds) +
                "&longDegrees=" + Math.abs(gmsGeoPoint.longitude.degrees) +
                "&longMinutes=" + Math.abs(gmsGeoPoint.longitude.minutes) +
                "&longSeconds=" + Math.abs(gmsGeoPoint.longitude.seconds) +
                "&radius=" + radius.toString() +
                "&sortColumns=5+false" +
                "&sortDirection=true" +
                "&designatorForNotamNumberSearch=" +
                "&notamNumber=" +
                "&radiusSearchOnDesignator=false" +
                "&radiusSearchDesignator=" +
                "&latitudeDirection=" + gmsGeoPoint.latitude.direction +
                "&longitudeDirection=" + gmsGeoPoint.longitude.direction +
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
                "&offset=" + offset.toString() +
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
