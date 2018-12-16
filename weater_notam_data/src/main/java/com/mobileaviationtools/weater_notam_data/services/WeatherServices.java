package com.mobileaviationtools.weater_notam_data.services;

import com.mobileaviationtools.weater_notam_data.Values;
import com.mobileaviationtools.weater_notam_data.weather.MetarsResponse;
import com.mobileaviationtools.weater_notam_data.weather.TafsResponse;
import org.oscim.core.GeoPoint;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.concurrent.TimeUnit;

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

    private String TAG = "WeatherServices";

    private GeoPoint location;
    private Long distance;

    public void GetMetarsByLocationAndRadius(GeoPoint location, Long distance, WeatherResponseEvent weatherResponseEvent)
    {
        String cmd = "metars";
        HttpUrl.Builder urlBuilder = buildBaseUrl(cmd);
        Request request = getDataByLocationAndRadius(location, distance, urlBuilder);
        doCall(cmd, request, weatherResponseEvent);
    }

    public void GetTafsByLocationAndRadius(GeoPoint location, Long distance, WeatherResponseEvent weatherResponseEvent)
    {
        String cmd = "tafs";
        HttpUrl.Builder urlBuilder = buildBaseUrl(cmd);
        Request request = getDataByLocationAndRadius(location, distance, urlBuilder);
        doCall(cmd, request, weatherResponseEvent);
    }

    private Request getDataByLocationAndRadius(GeoPoint location, Long distance, HttpUrl.Builder urlBuilder)
    {
        this.location = location;
        this.distance = distance;

        String command = "#DIS#;#LON#,#LAT#";
        command = command.replace("#DIS#", Long.toString(distance));
        command = command.replace("#LON#", Double.toString(location.getLongitude()));
        command = command.replace("#LAT#", Double.toString(location.getLatitude()));

        urlBuilder.addQueryParameter("radialDistance", command);
        urlBuilder.addQueryParameter("hoursBeforeNow", "1");
        urlBuilder.addQueryParameter("mostRecentForEachStation", "true");

        Request request = new Request.Builder()
                .url(urlBuilder.build().toString())
                .build();

        return request;
    }

    private HttpUrl.Builder buildBaseUrl(String datasource)
    {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(Values.weather_base_url + "adds/dataserver_current/httpparam").newBuilder();
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
        builder.connectTimeout(2000, TimeUnit.MILLISECONDS);
        builder.readTimeout(2000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(2000, TimeUnit.MILLISECONDS);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(10);

        return client;
    }


    private void doCall(final String command, Request request, final WeatherResponseEvent event)
    {
        OkHttpClient client = GetHttpClient();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (event != null) event.OnFailure(e.getMessage(), location, distance);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String xml = response.body().string();
                if (command.equals("metars"))
                    getMetars(xml, event, response.message());
                if (command.equals("tafs"))
                    getTafs(xml, event, response.message());
            }
        });
    }

    private void getMetars(String xml, final WeatherResponseEvent event, String responseMessage)
    {
        try {
            Serializer serializer = new Persister();
            Reader reader = new StringReader(xml);
            MetarsResponse resp =
                    serializer.read(MetarsResponse.class, reader, false);
            if (event != null) event.OnMetarsResponse(resp.data, location, responseMessage);
        }
        catch (Exception e)
        {
            if (event != null) event.OnFailure(e.getMessage() + "Metar Response: "
                    + responseMessage + " XML: " + xml, location, distance);
        }
    }

    private void getTafs(String xml, final WeatherResponseEvent event, String responseMessage)
    {
        try {
            Serializer serializer = new Persister();
            Reader reader = new StringReader(xml);
            TafsResponse resp =
                    serializer.read(TafsResponse.class, reader, false);
            if (event != null) event.OnTafsResponse(resp.data, location, responseMessage);
        }
        catch (Exception e)
        {
            if (event != null) event.OnFailure(e.getMessage() + "TAF Response: "
                    + responseMessage + " XML: " + xml, location, distance);
        }
    }
}
