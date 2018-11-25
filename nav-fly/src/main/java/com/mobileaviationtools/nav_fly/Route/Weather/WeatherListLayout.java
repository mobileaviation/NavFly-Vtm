package com.mobileaviationtools.nav_fly.Route.Weather;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
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
    //private Activity activity;
    private ImageButton weatherRefreshBtn;
    private WeatherResponseEvent weatherResponseEvent;
    private ListView weatherStationsList;

//    private List<Metar> metars;
//    private List<Taf> tafs;
    private WeatherStations stations;

    public WeatherListLayout(Context context) {
        super(context);
    }

    public WeatherListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void init(Context context)
    {
        this.context = context;
        //this.activity = activity;
        weatherRefreshBtn = (ImageButton) findViewById(R.id.weatherRefreshBtn);
        weatherStationsList = (ListView) findViewById(R.id.weatherAirportsListView);
//        setWeatherResponse();
        setWeatherListOnItemClick();
        setWeatherRefreshBtnClick();
    }

//    private void setWeatherResponse()
//    {
//        weatherResponseEvent = new WeatherResponseEvent() {
//            @Override
//            public void OnMetarsResponse(List<Metar> metars, String message) {
//                WeatherListLayout.this.metars = metars;
//                setWeatherData();
//            }
//
//            @Override
//            public void OnTafsResponse(List<Taf> tafs, String message) {
//                WeatherListLayout.this.tafs = tafs;
//                setWeatherData();
//            }
//
//            @Override
//            public void OnFailure(String message) {
//
//            }
//        };
//    }

    public void setWeatherData(WeatherStations stations)
    {
//        activity.runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                if ((metars != null) && (tafs != null)) {
//                    stations = new ArrayList<>();
//                    if (metars.size() > tafs.size()) {
//                        for (Metar m : metars)
//                        {
//                            Station station = new Station(getContext());
//                            station.setStation_id(m.station_id);
//                            station.setMetar(m);
//                            Taf testTaf = new Taf();
//                            testTaf.station_id = m.station_id;
//                            int tafIndex = tafs.indexOf(testTaf);
//                            station.setTaf((tafIndex>-1) ? tafs.get(tafIndex) : null);
//                            stations.add(station);
//                        }
//                    } else {
//                        for (Taf t : tafs){
//                            Station station = new Station(getContext());
//                            station.setStation_id(t.station_id);
//                            station.setTaf(t);
//                            Metar testMetar = new Metar();
//                            testMetar.station_id = t.station_id;
//                            int metarIndex = metars.indexOf(testMetar);
//                            station.setMetar((metarIndex>-1) ? metars.get(metarIndex) : null);
//                            stations.add(station);
//                        }
//                    }
        this.stations = stations;
        if (stations != null) {
            WeatherAirportItemAdapter weatherAirportItemAdapter = new WeatherAirportItemAdapter(stations, getContext());
            weatherStationsList.setAdapter(weatherAirportItemAdapter);
        }
//                }
//            }
//        });
    }

//    public void weatherBtnClick()
//    {
//        if (stations == null)
//        {
////            getWeatherData();
//        }
//    }

    private void setWeatherRefreshBtnClick()
    {
        weatherRefreshBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stations != null) {
                    FspLocation loc = new FspLocation(map.getMapPosition().getGeoPoint(), "weatherLoc");
                    stations.getWeatherData(loc, 100l);
                    // TODO Add event with updated weather data
                }
            }
        });
    }

//    private void getWeatherData()
//    {
//        metars = null;
//        tafs = null;
//        stations = null;
//
//        MapPosition pos = map.getMapPosition();
//        WeatherServices weatherServices = new WeatherServices();
//        weatherServices.GetTafsByLocationAndRadius(pos.getGeoPoint(), 100l,
//                weatherResponseEvent);
//        weatherServices.GetMetarsByLocationAndRadius(pos.getGeoPoint(), 100l,
//                weatherResponseEvent);
//    }

    private void setWeatherListOnItemClick()
    {
        weatherStationsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WeatherAirportItemAdapter adapter = (WeatherAirportItemAdapter) adapterView.getAdapter();
                Station station = (Station)adapter.getItem(i);

                if (station.metar != null) {
                    TextView metarText = (TextView)findViewById(R.id.metarItemTxt);
                    TextView metarDateText = (TextView)findViewById(R.id.metarDateTxt);
                    metarText.setText(station.metar.raw_text);
                    metarDateText.setText(station.metar.observation_time);
                }

                if (station.taf != null) {
                    TextView tafText = (TextView)findViewById(R.id.tafItemTxt);
                    TextView tafDateText = (TextView) findViewById(R.id.tafDateTxt);
                    tafText.setText(station.taf.raw_text);
                    tafDateText.setText(station.taf.valid_time_from);
                }
            }
        });
    }
}
