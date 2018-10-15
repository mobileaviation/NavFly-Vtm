package com.mobileaviationtools.nav_fly.Route.Weather;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;
import com.mobileaviationtools.weater_notam_data.services.WeatherServices;
import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

import org.oscim.core.MapPosition;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

public class WeatherListLayout extends LinearLayout {
    private String TAG = "WeatherListLayout";
    private Map map;
    private Context context;
    private Activity activity;
    private ImageButton weatherRefreshBtn;
    private WeatherResponseEvent weatherResponseEvent;
    private ListView weatherStationsList;

    private List<Metar> metars;
    private List<Taf> tafs;
    private List<String> stations;

    public WeatherListLayout(Context context) {
        super(context);
    }

    public WeatherListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void init(Context context, Activity activity)
    {
        this.context = context;
        this.activity = activity;
        weatherRefreshBtn = (ImageButton) findViewById(R.id.weatherRefreshBtn);
        weatherStationsList = (ListView) findViewById(R.id.weatherAirportsListView);
    }

    private void setWeatherResponse()
    {
        weatherResponseEvent = new WeatherResponseEvent() {
            @Override
            public void OnMetarsResponse(List<Metar> metars, String message) {
                WeatherListLayout.this.metars = metars;
                setWeatherData();
            }

            @Override
            public void OnTafsResponse(List<Taf> tafs, String message) {
                WeatherListLayout.this.tafs = tafs;
                setWeatherData();
            }

            @Override
            public void OnFailure(String message) {

            }
        };
    }

    private void setWeatherData()
    {
        if ((metars!=null) && (tafs!=null))
        {
            stations = new ArrayList<>();
            if (metars.size()>tafs.size())
            {
                for(Metar m : metars) stations.add(m.station_id);
            } else
            {
                for(Taf t: tafs) stations.add(t.station_id);
            }
            WeatherAirportItemAdapter weatherAirportItemAdapter = new WeatherAirportItemAdapter(stations, getContext());
            weatherStationsList.setAdapter(weatherAirportItemAdapter);
        }
    }

    public void weatherBtnClick()
    {
        if (stations == null)
        {
            getWeatherData();
        }
    }


    private void getWeatherData()
    {
        metars = null;
        tafs = null;
        stations = null;

        MapPosition pos = map.getMapPosition();
        WeatherServices weatherServices = new WeatherServices();
        weatherServices.GetTafsByLocationAndRadius(pos.getGeoPoint(), 100l,
                weatherResponseEvent);
        weatherServices.GetMetarsByLocationAndRadius(pos.getGeoPoint(), 100l,
                weatherResponseEvent);
    }
}
