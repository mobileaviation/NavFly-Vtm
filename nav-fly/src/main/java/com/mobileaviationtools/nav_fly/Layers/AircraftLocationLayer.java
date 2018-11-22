package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Markers.AircraftMarkerItem;

import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.map.Map;

public class AircraftLocationLayer extends ItemizedLayer {
    public AircraftLocationLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public static AircraftLocationLayer createNewAircraftLayer(Map map, Context context, FspLocation initLocation)
    {
        AircraftLocationLayer layer = new AircraftLocationLayer(map, null);
        layer.map = map;
        layer.context = context;
        layer.addAircraftItem(initLocation);
        layer.populate();
        return layer;
    }

    private Map map;
    private Context context;
    private AircraftMarkerItem item;

    private void addAircraftItem(FspLocation location)
    {
        item = AircraftMarkerItem.getNewAircraftMarkerItem(context, location);
        this.addItem(item);
    }

    public void UpdateLocation(FspLocation location)
    {
        this.removeAllItems();
        item.setLocation(location);
        this.addItem(item);
        this.update();

    }
}
