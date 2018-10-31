package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.layers.vector.VectorLayer;
import org.oscim.map.Map;

public class SelectionLayer extends VectorLayer {
    public SelectionLayer(Map map, Context context) {
        super(map);
        this.context = context;
        this.mMap = map;
        mMap.layers().add(this);
    }

    private Context context;
    private Map mMap;

    public void setAirportSelected(Airport airport)
    {
        
    }
}
