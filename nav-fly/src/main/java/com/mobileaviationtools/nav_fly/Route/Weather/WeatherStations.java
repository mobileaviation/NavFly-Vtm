package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;
import com.mobileaviationtools.weater_notam_data.services.WeatherServices;
import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.utils.pool.Inlist;

import java.util.ArrayList;
import java.util.List;

public class WeatherStations extends ArrayList<Station> {
    public WeatherStations(Context context)
    {
        this.context = context;
        setWeatherResponse();
    }

    private String TAG = "WeatherStations";

    private Context context;
    private WeatherResponseEvent weatherResponseEvent;
    private Integer fireEvent;


    private void setWeatherResponse()
    {
        weatherResponseEvent = new WeatherResponseEvent() {
            @Override
            public void OnMetarsResponse(List<Metar> metars, String message) {
                fireEvent++;
                setMetars(metars);
                fireEvent();
            }

            @Override
            public void OnTafsResponse(List<Taf> tafs, String message) {
                fireEvent++;
                setTafs(tafs);
                fireEvent();
            }

            @Override
            public void OnFailure(String message) {

            }
        };
    }

    private void setMetars(List<Metar> metars)
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
        if (fireEvent>1)
        {
            Log.i(TAG, "Received both Metars and Tafs");
            // TODO add event handler for weather data
        }
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

    public Integer getStationNearestToOrg()
    {
        Double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < this.size(); i++) {
            Station s = this.get(i);
            if (Double.compare(s.distance_to_org, min) < 0) {
                min = s.distance_to_org;
                index = i;
            }
        }
        return index;
    }
}
