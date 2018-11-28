package com.mobileaviationtools.nav_fly.Info.Airspace;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Classes.AltitudeReference;
import com.mobileaviationtools.airnavdata.Classes.AltitudeUnit;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.nav_fly.R;

public class AirspaceAdapter extends BaseAdapter {
    public AirspaceAdapter(Airspace[] airspaces)
    {
        this.airspaces = airspaces;
    }

    public void setAirspaces(Airspace[] airspaces)
    {
        this.airspaces = airspaces;
    }

    private Airspace[] airspaces;

    @Override
    public int getCount() {
        return airspaces.length;
    }

    @Override
    public Object getItem(int i) {
        return airspaces[i];
    }

    public Airspace getAirspace(int i)
    {
        return airspaces[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.airspace_item, viewGroup, false);

        TextView airspaceCategory = (TextView) view.findViewById(R.id.airspaceCategory);
        TextView airspaceTopLevelTxt = (TextView) view.findViewById(R.id.airspaceTopLevelTxt);
        TextView airspacebottomLevelTxt = (TextView) view.findViewById(R.id.airspacebottomLevelTxt);
        TextView airspaceNameTxt = (TextView) view.findViewById(R.id.airspaceNameTxt);

        Airspace airspace = getAirspace(i);

        airspaceCategory.setText(airspace.category.toString());
        airspaceTopLevelTxt.setText(SetString(airspace.altLimit_top, airspace.altLimit_top_unit, airspace.altLimit_top_ref));
        airspacebottomLevelTxt.setText(SetString(airspace.altLimit_bottom, airspace.altLimit_bottom_unit, airspace.altLimit_bottom_ref));
        airspaceNameTxt.setText(airspace.name);

        return view;
    }

    private String SetString(Long altitude, AltitudeUnit unit, AltitudeReference reference )
    {
        if (altitude==0) return "GND";
        if (unit==AltitudeUnit.FL) return "FL " + altitude.toString();
        if (unit==AltitudeUnit.F) return altitude.toString() + " " + reference.toString();
        return altitude.toString() + " " + unit.toString();
    }
}
