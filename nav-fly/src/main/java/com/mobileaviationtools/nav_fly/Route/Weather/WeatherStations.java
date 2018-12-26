package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.weater_notam_data.Values;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;
import com.mobileaviationtools.weater_notam_data.services.WeatherServices;
import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

import org.oscim.core.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherStations extends ArrayList<Station> {
    public interface WeatherDataReceivedEvent
    {
        public void Received(WeatherStations stations);
    }

    public WeatherStations(Context context)
    {
        fireEvent = 0;
        tafs_received = false;
        metars_received = false;
        this.context = context;
        setWeatherResponse();
    }

    private String TAG = "WeatherStations";

    private Context context;
    private WeatherResponseEvent weatherResponseEvent;
    private WeatherDataReceivedEvent weatherDataReceivedEvent;
    public void SetWeatherDataReceivedEvent(WeatherDataReceivedEvent weatherDataReceivedEvent)
    {
        this.weatherDataReceivedEvent = weatherDataReceivedEvent;
    }
    private Integer fireEvent;
    private Boolean tafs_received;
    private Boolean metars_received;


    private void setWeatherResponse()
    {
        weatherResponseEvent = new WeatherResponseEvent() {
            @Override
            public void OnMetarsResponse(List<Metar> metars, GeoPoint location, String message) {
                //fireEvent++;
                metars_received = true;
                setMetars(metars, location);
                fireEvent();
            }

            @Override
            public void OnTafsResponse(List<Taf> tafs, GeoPoint location, String message) {
                // fireEvent++;
                tafs_received = true;
                setTafs(tafs);
                fireEvent();
            }

            @Override
            public void OnFailure(String message, GeoPoint location, Long distance) {
                Log.e(TAG, "Error retrieving weather: " + message);
                Log.i(TAG, "get stored weather data from the database..");
                getDatabaseWeather(location, distance);
            }
        };
    }

    private void setMetars(List<Metar> metars, GeoPoint location)
    {
        for (Metar m : metars)
        {
            Station station = new Station(context);
            station.setStation_id(m.station_id);
            Integer i = this.indexOf(station);
            if (i>-1)
            {
                station = this.get(i);
            }
            else
            {
                this.add(station);
            }
            m.distance_to_org_m = (float) location.sphericalDistance(new GeoPoint(m.latitude, m.longitude));
            station.setMetar(m);
        }
    }

    private void setTafs(List<Taf> tafs)
    {
        for (Taf t : tafs)
        {
            Station station = new Station(context);
            station.setStation_id(t.station_id);
            Integer i = this.indexOf(station);
            if (i>-1)
            {
                station = this.get(i);
            }
            else
            {
                this.add(station);
            }
            station.setTaf(t);
        }
    }

    private void fireEvent()
    {
        //if (fireEvent>1)
        if (metars_received && tafs_received)
        {
            Log.i(TAG, "Received both Metars and Tafs");
            if(weatherDataReceivedEvent != null) weatherDataReceivedEvent.Received(this);
            // TODO add event handler for weather data
        }
    }

    public void getDatabaseWeather(GeoPoint location, long distance)
    {
        DatabaseWeatherServices databaseWeatherServices = new DatabaseWeatherServices(context);
        databaseWeatherServices.GetMetarsByLocationAndRadius(location, distance, weatherResponseEvent);
        databaseWeatherServices.GetTafsByLocationAndRadius(location, distance, weatherResponseEvent);
    }

    public void getWeatherData(FspLocation location, Long distance)
    {
        fireEvent = 0;
        this.clear();
        GeoPoint pos = new GeoPoint(location.getLatitude(), location.getLongitude());

        WeatherServices weatherServices = new WeatherServices();
        weatherServices.GetTafsByLocationAndRadius(pos, distance, weatherResponseEvent);
        weatherServices.GetMetarsByLocationAndRadius(pos, distance, weatherResponseEvent);
    }

    private Integer getStationNearestToOrg(GeoPoint location)
    {
        Double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < this.size(); i++) {
            Station s = this.get(i);
            if (s.metar != null) {
                Double d = s.GetDistanceTo(location);
                if (Double.compare(d, min) < 0) {
                    min = d;
                    index = i;
                }
            }
        }
        return index;
    }

    public String[] getQNHInfo(GeoPoint location)
    {
        int index = getStationNearestToOrg(location);
        if (index>-1) {
            Metar metar = this.get(index).metar;
            String raw = metar.raw_text;
            String qnh = "UNK";
            String ident = metar.station_id;
            int qindex = raw.indexOf("Q");
            if (qindex > -1)
                qnh = raw.substring(qindex + 1, qindex + 5);
            else {
                qindex = raw.indexOf("A");
                if (qindex > -1)
                    qnh = raw.substring(qindex + 1, qindex + 5);
            }

            return new String[]{ident, qnh};
        }
        else
            return new String[]{"", "UNK"};
    }
}
