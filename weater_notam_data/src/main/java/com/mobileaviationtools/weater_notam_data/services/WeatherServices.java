package com.mobileaviationtools.weater_notam_data.services;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.MetarsResponse;

import org.oscim.core.GeoPoint;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Dispatcher;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherServices {
    public WeatherServices()
    {

    }

    public void GetMetarsByLocationAndRadius(GeoPoint location, long distance, WeatherResponseEvent weatherResponseEvent)
    {
        HttpUrl.Builder urlBuilder = buildBaseUrl("metars");

        String command = "100;#LON#,#LAT#";
        command = command.replace("#LON#", Double.toString(location.getLongitude()));
        command = command.replace("#LAT#", Double.toString(location.getLatitude()));

        urlBuilder.addQueryParameter("radialDistance", command);
        urlBuilder.addQueryParameter("hoursBeforeNow", "1");
        urlBuilder.addQueryParameter("mostRecentForEachStation", "true");

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

        doCall(request, weatherResponseEvent);
    }

    private HttpUrl.Builder buildBaseUrl(String datasource)
    {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("http://www.aviationweather.gov/adds/dataserver_current/httpparam").newBuilder();
        urlBuilder.addQueryParameter("dataSource", datasource);
        urlBuilder.addQueryParameter("requestType", "retrieve");
        urlBuilder.addQueryParameter("format", "xml");

        return urlBuilder;
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

    private void doCall(Request request, final WeatherResponseEvent event)
    {
        OkHttpClient client = GetHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (event != null) event.OnFailure(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {


                try {
                    Serializer serializer = new Persister();
                    Reader reader = new StringReader(response.body().string());
                    MetarsResponse resp =
                    serializer.read(MetarsResponse.class, reader, false);
                } catch (Exception e) {
                    if (event != null) event.OnFailure(e.getMessage());
                }

                if (event != null) event.OnMetarsResponse(null, response.body().string());
            }
        });
    }
}
