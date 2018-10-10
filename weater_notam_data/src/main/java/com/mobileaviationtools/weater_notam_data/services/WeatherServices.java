package com.mobileaviationtools.weater_notam_data.services;

import com.mobileaviationtools.weater_notam_data.weather.Metar;

import org.oscim.core.GeoPoint;

import java.util.ArrayList;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class WeatherServices {
    public WeatherServices()
    {

    }

    private final String BaseUrl = "http://www.aviationweather.gov/adds/dataserver_current/httpparam?dataSource=#DATASOURCE#&requestType=retrieve&format=xml";

    public ArrayList<Metar> GetMetarsByLocationAndRadius(GeoPoint location, long distance)
    {
        String command = "&radialDistance=100;#LON#,#LAT#&hoursBeforeNow=1&mostRecentForEachStation=true";
        command = command.replace("#LON#", Double.toString(location.getLongitude()));
        command = command.replace("#LAT#", Double.toString(location.getLatitude()));
        command = BaseUrl.replace("#DATASOURCE#", "metars") + command;



        return null;
    }

    private void GetHttpClient()
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(10);
    }
}
