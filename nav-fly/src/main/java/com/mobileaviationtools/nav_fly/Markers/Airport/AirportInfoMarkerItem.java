package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

public class AirportInfoMarkerItem extends MarkerItem{
    private Airport airport;
    private Context context;

    public AirportInfoMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public AirportInfoMarkerItem(Airport airport, Context context) {
        this(airport.ident, airport.name, new GeoPoint(airport.latitude_deg, airport.longitude_deg));
        Coordinate coordinate = new Coordinate(airport.longitude_deg, airport.latitude_deg);
        this.airport = airport;
        this.context = context;
    }

    public void InitMarker()
    {
        MarkerSymbol symbol = null;

        symbol = new MarkerSymbol(new AndroidBitmap(draw()), MarkerSymbol.HotspotPlace.TOP_CENTER);

        this.setMarker(symbol);
    }

    private android.graphics.Bitmap draw()
    {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(30);
        //p.setStrokeWidth(1);
        //p.setStyle(Paint.Style.STROKE);
        p.setTextAlign(Paint.Align.LEFT);
        p.setColor(Color.argb(255,0,0,0));
        p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));

        String airportName = shortenName(airport.name);
        airportName = airportName + " (" + airport.ident + ")";
        Rect airportNameRect = new Rect();
        p.getTextBounds(airportName, 0, airportName.length(), airportNameRect);

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(airportNameRect.width(),
                airportNameRect.height() + 30,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        baseCanvas.drawText(airportName, 0, 35 + Math.abs(airportNameRect.top) , p);

        return baseBitmap;
    }

    private String shortenName(String name)
    {
        String ret = name;
        ret = ret.replace("Airport", "");
        ret = ret.replace("Air Base", "");
        ret = ret.replace("Glider Field", "");
        ret = ret.replace("Aerodrome", "");
        ret = ret.replace("Airfield", "");
        return ret;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AirportInfoMarkerItem) {
            return (((AirportInfoMarkerItem)obj).airport.ident.equals(this.airport.ident));
        }
        else
            return false;
    }
}
