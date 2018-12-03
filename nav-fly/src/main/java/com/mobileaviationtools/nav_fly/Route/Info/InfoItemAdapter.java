package com.mobileaviationtools.nav_fly.Route.Info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkerItem;
import com.mobileaviationtools.nav_fly.Markers.Navaids.NavaidMarkerItem;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Weather.Station;

import org.oscim.core.GeoPoint;
import org.oscim.core.GeoPointConvertion;

import java.util.ArrayList;
import java.util.List;

public class InfoItemAdapter extends BaseAdapter {
    public InfoItemAdapter(Context context)
    {
        this.context = context;
        stations = new ArrayList<>();
    }

    private Context context;
    private List<Object> stations;

    public void setAirports(List<Airport> airports)
    {
        stations.clear();
        for (Airport a: airports) stations.add(a);
        this.notifyDataSetChanged();
    }

    public void setNavaids(List<Navaid> navaids)
    {
        stations.clear();
        for (Navaid n: navaids) stations.add(n);
        this.notifyDataSetChanged();
    }

    public void setFix(List<Fix> fixes)
    {
        stations.clear();
        for (Fix f: fixes) stations.add(f);
        this.notifyDataSetChanged();
    }

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
        View rowView = inflater.inflate(R.layout.info_list_item, viewGroup, false);

        ImageView icon = (ImageView) rowView.findViewById(R.id.infoItemIconImage);
        TextView infoNameTextView = (TextView) rowView.findViewById(R.id.infoItemNameText);
        TextView infoCodeText = (TextView) rowView.findViewById(R.id.infoItemCodeText);
        TextView infoLocationText = (TextView) rowView.findViewById(R.id.infoItemLocationTxt);

        Object station = stations.get(i);

        if (station instanceof Airport) {
            Airport a = (Airport)station;
            getRunwaysAndFrequencies(a);
            icon.setImageBitmap(AirportMarkerItem.GetMarkerBitmap(a));
            infoNameTextView.setText(a.name);
            infoCodeText.setText(a.ident + ((a.iata_code.isEmpty()) ? "" : " (" + a.iata_code + ")"));
            GeoPointConvertion dms = new GeoPointConvertion();
            infoLocationText.setText(dms.getStringFormattedDMS(new GeoPoint(a.latitude_deg, a.longitude_deg)));
        }

        if (station instanceof Navaid){
            Navaid n = (Navaid)station;
            icon.setImageBitmap(NavaidMarkerItem.GetMarkerBitmap(n));
            infoNameTextView.setText(n.name);
            infoCodeText.setText(n.ident);
            GeoPointConvertion dms = new GeoPointConvertion();
            infoLocationText.setText(dms.getStringFormattedDMS(new GeoPoint(n.latitude_deg, n.longitude_deg)));
        }

        if (station instanceof Fix)
        {
            Fix n = (Fix)station;
            infoNameTextView.setText(n.ident);
            infoCodeText.setText(n.ident);
            GeoPointConvertion dms = new GeoPointConvertion();
            infoLocationText.setText(dms.getStringFormattedDMS(new GeoPoint(n.latitude_deg, n.longitude_deg)));
        }


        return rowView;
    }

    private void getRunwaysAndFrequencies(Airport airport)
    {
        AirnavDatabase airnavDatabase = AirnavDatabase.getInstance(context);
        airport.runways = airnavDatabase.getRunways().getRunwaysByAirport(airport.id);
        airport.frequencies = airnavDatabase.getFrequency().getFrequenciesByAirport(airport.id);
    }
}
