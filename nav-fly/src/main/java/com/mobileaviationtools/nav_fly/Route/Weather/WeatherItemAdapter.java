package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;

import java.util.List;

public class WeatherItemAdapter extends BaseAdapter {
    public WeatherItemAdapter(Context context, List<Station> stations)
    {
        tafMetar = stations;
        this.context = context;
    }

    private Context context;
    private List<Station> tafMetar;

    @Override
    public int getCount() {
        return tafMetar.size();
    }

    @Override
    public Object getItem(int i) {
        return tafMetar.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.weather_list_item, viewGroup, false);

        TextView typeTextView = (TextView) rowView.findViewById(R.id.weatherTypeTxt);
        TextView infoTextView = (TextView) rowView.findViewById(R.id.weatherItemTxt);
        TextView dateView = (TextView) rowView.findViewById(R.id.metarDateTextView);

        Station station = tafMetar.get(i);
        typeTextView.setText((station.metar != null) ? "Metar:" : "Taf:");

        infoTextView.setText((station.metar != null) ? station.metar.raw_text : station.taf.raw_text);
        dateView.setText((station.metar != null) ? station.metar.observation_time : station.taf.valid_time_from);

        return rowView;
    }
}
