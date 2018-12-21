package com.mobileaviationtools.nav_fly.Settings.HomeAirport;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Entities.Runway;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.utils.pool.Inlist;

import java.util.ArrayList;
import java.util.List;

public class RunwaysAdapter extends BaseAdapter {


    public RunwaysAdapter(List<Runway> runways)
    {
        this.runways = runways;
        runwayItems = new ArrayList<>();
        for (Runway r : this.runways)
        {
            RunwayItem itemLe = new RunwayItem();
            itemLe.runway = r;
            itemLe.leHe = false;
            runwayItems.add(itemLe);

            RunwayItem itemHe = new RunwayItem();
            itemHe.runway = r;
            itemHe.leHe = true;
            runwayItems.add(itemHe);
        }
    }

    private List<Runway> runways;
    private List<RunwayItem> runwayItems;
    private View view;

    @Override
    public int getCount() {
        return runwayItems.size();
    }

    @Override
    public Object getItem(int position) {
        return runwayItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        view = inflater.inflate(R.layout.runway_item, parent, false);

        TextView runwayTextView = (TextView) view.findViewById(R.id.runwayNameText);
        RunwayItem runwayItem = runwayItems.get(position);
        runwayTextView.setText((runwayItem.leHe) ? runwayItem.runway.he_ident.toString() : runwayItem.runway.le_ident.toString() );

        return view;
    }
}
