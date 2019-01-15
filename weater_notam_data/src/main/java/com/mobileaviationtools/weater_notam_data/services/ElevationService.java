package com.mobileaviationtools.weater_notam_data.services;

import com.google.gson.Gson;
import com.mobileaviationtools.weater_notam_data.Elevation.ElevationResponseEvent;
import com.mobileaviationtools.weater_notam_data.Elevation.elevation;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.core.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ElevationService {
    public ElevationService()
    {

    }

    private String elevationBaseUrl = "http://dev.virtualearth.net/REST/v1/Elevation/";
    private String key = "Ai_iuAs_d6oZU9rruo0k2UHlOIJVExqa5bZZ_kbOMx3X8xtFQ1gE1bYJXjK91mLG";

    private OkHttpClient client;

    public void getElevationsByPolyline(Coordinate[] polyline, Long samples,
                                        ElevationResponseEvent elevationResponseEvent)
    {

        HttpUrl.Builder urlBuilder = HttpUrl.parse(elevationBaseUrl + "Polyline").newBuilder();
        urlBuilder.addQueryParameter("points", getPolyline(polyline));
        urlBuilder.addQueryParameter("samples", Long.toString(samples));
        urlBuilder.addQueryParameter("key", key);
        String url = urlBuilder.build().toString();

        Request request = new Request.Builder()
                .url(url)
                .build();

        doCall(request, elevationResponseEvent);
    }

    private void doCall(final Request request, final ElevationResponseEvent elevationResponseEvent)
    {
        client = HttpClientInstance.getClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (elevationResponseEvent != null) elevationResponseEvent.OnFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String elevationJson = response.body().string();
                //NotamCounts counts = new Gson().fromJson(notamsJson, NotamCounts.class);

                elevation _elevation = new Gson().fromJson(elevationJson, elevation.class);
                if (elevationResponseEvent != null) elevationResponseEvent.
                        OnElevationResponse(_elevation,elevationJson, response.message());

            }
        });
    }

    private String getPolyline (Coordinate[] polyline)
    {
        String points = "";
        for(Coordinate p : polyline)
        {
            points = points + Double.toString(p.y) + "," +
                    Double.toString(p.x) + ",";
        }

        points = points.substring(0, points.length()-1);
        return points;
    }

}
