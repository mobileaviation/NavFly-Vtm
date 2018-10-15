package com.mobileaviationtools.nav_fly.Route.Notams;

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
import com.mobileaviationtools.weater_notam_data.notams.Notam;
import com.mobileaviationtools.weater_notam_data.notams.NotamCount;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;

public class NotamsAirportItemAdapter extends BaseAdapter {
    private AirnavDatabase db;
    private NotamCounts notamCounts;
    private Context context;

    public NotamsAirportItemAdapter(NotamCounts counts, Context context)
    {
        notamCounts = counts;
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    @Override
    public int getCount() {
        return notamCounts.counts.length;
    }

    @Override
    public Object getItem(int i) {
        return notamCounts.counts[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public Airport getAirport(int i)
    {
        String icao = notamCounts.counts[i].icaoId;
        return  db.getAirport().getAirportByIdent(icao);
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notam_airport_list_item, viewGroup, false);

        NotamCount notamCount = notamCounts.counts[i];

        ImageView icon = (ImageView) rowView.findViewById(R.id.notamAirportIconImage);
        TextView airportNameTextView = (TextView) rowView.findViewById(R.id.notamAirportNameText);
        TextView airportIcaoText = (TextView) rowView.findViewById(R.id.notamAirportICAOText);
        TextView airportCountText = (TextView) rowView.findViewById(R.id.notamAirportCountTxt);

        Airport a = db.getAirport().getAirportByIdent(notamCount.icaoId);
        a.runways = db.getRunways().getRunwaysByAirport(a.id);

        icon.setImageBitmap(AirportMarkerItem.GetMarkerBitmap(a));

        airportNameTextView.setText(a.name);
        airportCountText.setText(notamCount.notamCount.toString());
        airportIcaoText.setText(a.ident);

        return rowView;
    }
}
