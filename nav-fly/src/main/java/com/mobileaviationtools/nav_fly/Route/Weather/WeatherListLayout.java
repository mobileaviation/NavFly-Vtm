package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;

public class WeatherListLayout extends LinearLayout {
    private String TAG = "WeatherListLayout";
//    private Map map;
//    private Context context;
//    private Activity activity;
    private GlobalVars vars;
    private ImageButton weatherRefreshBtn;
    private WeatherResponseEvent weatherResponseEvent;
    private ListView weatherStationsList;
    private ProgressBar weatherProgressBar;

    private WeatherStations stations;

    public WeatherListLayout(Context context) {
        super(context);
    }

    public WeatherListLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

//    public void setMap(Map map) {
//        this.map = map;
//    }

    public void init(GlobalVars vars, ProgressBar weatherProgressBar)
    {
        this.vars = vars;
        this.weatherProgressBar = weatherProgressBar;
        weatherRefreshBtn = (ImageButton) findViewById(R.id.weatherRefreshBtn);
        weatherStationsList = (ListView) findViewById(R.id.weatherAirportsListView);
        setWeatherListOnItemClick();
        setWeatherRefreshBtnClick();
    }

    public void setWeatherData(GlobalVars vars, WeatherStations stations)
    {
        this.vars = vars;
        this.stations = stations;
        if (stations != null) {

            vars.mainActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    WeatherAirportItemAdapter weatherAirportItemAdapter =
                            new WeatherAirportItemAdapter(WeatherListLayout.this.stations, getContext());
                    weatherStationsList.setAdapter(weatherAirportItemAdapter);
                }
            });

        }
    }


    private void setWeatherRefreshBtnClick()
    {
        weatherRefreshBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (stations != null) {
                    if (weatherProgressBar != null) weatherProgressBar.setVisibility(VISIBLE);
                    FspLocation loc = new FspLocation(vars.map.getMapPosition().getGeoPoint(), "weatherLoc");
                    if (Helpers.isConnected(vars.context))
                        stations.getWeatherData(loc, vars.route, 100l);
                    else
                        if(vars.route==null)
                            stations.getDatabaseWeatherByLocationAndDistance(loc.getGeopoint(), 100l);
                        else
                            stations.getDatabaseWeatherByRoute(vars.route.getFlightPathGeometry());
                }
            }
        });
    }


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
