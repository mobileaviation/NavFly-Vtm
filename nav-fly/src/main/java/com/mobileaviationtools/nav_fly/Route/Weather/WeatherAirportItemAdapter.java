package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkerItem;
import com.mobileaviationtools.nav_fly.R;

import java.util.List;

public class WeatherAirportItemAdapter extends BaseAdapter {
    public WeatherAirportItemAdapter(WeatherStations stations, Context context)
    {
        this.context = context;
        this.stations = stations;
    }

    private WeatherStations stations;
    private Context context;

    @Override
    public int getCount() {
        return stations.size();
    }

    @Override
    public Object getItem(int i) {
        return stations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.weather_airport_list_item, viewGroup, false);

        ImageView icon = (ImageView) rowView.findViewById(R.id.weatherAirportIconImage);
        TextView airportNameTextView = (TextView) rowView.findViewById(R.id.weatgerAirportNameText);
        TextView airportIcaoText = (TextView) rowView.findViewById(R.id.weatherAirportICAOText);

        Station station = stations.get(i);

        if (station.airport != null) {
            icon.setImageBitmap(AirportMarkerItem.GetMarkerBitmap(station.airport));
            airportNameTextView.setText(station.airport.name);
            airportIcaoText.setText(station.airport.ident);
        }
        else
        {
            airportNameTextView.setText("");
            airportIcaoText.setText(station.station_id);
        }

        return rowView;
    }
}
