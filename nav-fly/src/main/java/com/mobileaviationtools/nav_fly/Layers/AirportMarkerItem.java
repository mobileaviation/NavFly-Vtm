package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class AirportMarkerItem extends MarkerItem{
    private Airport airport;

    public AirportMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public AirportMarkerItem(Airport airport, Context context) {
        this(airport.ident, airport.name, new GeoPoint(airport.latitude_deg, airport.longitude_deg));
        this.airport = airport;
        Bitmap bitmapPoi = drawableToBitmap(context.getResources().getDrawable(R.drawable.marker_poi));
        MarkerSymbol symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.CENTER, false);
        this.setMarker(symbol);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AirportMarkerItem) {
            return (((AirportMarkerItem)obj).airport.ident.equals(this.airport.ident));
        }
        else
        return false;
    }
}
