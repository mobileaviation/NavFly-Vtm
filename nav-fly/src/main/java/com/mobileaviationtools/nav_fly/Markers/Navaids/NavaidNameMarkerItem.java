package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.AsyncTask;

import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.util.concurrent.Executor;

public class NavaidNameMarkerItem extends MarkerItem{
    private Navaid navaid;
    private Context context;

    public NavaidNameMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public NavaidNameMarkerItem(Navaid navaid, Context context) {
        this(navaid.ident, navaid.name, new GeoPoint(navaid.latitude_deg, navaid.longitude_deg));
        Coordinate coordinate = new Coordinate(navaid.longitude_deg, navaid.latitude_deg);
        this.navaid = navaid;
        this.context = context;
    }

    public void InitMarker()
    {
        MarkerSymbol symbol = null;
        symbol = new MarkerSymbol(new AndroidBitmap(draw()), MarkerSymbol.HotspotPlace.TOP_CENTER);
        setMarker(symbol);
    }

    private android.graphics.Bitmap draw()
    {
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(15);
        //p.setStrokeWidth(1);
        //p.setStyle(Paint.Style.STROKE);
        p.setTextAlign(Paint.Align.LEFT);
        p.setColor(Color.argb(255,0,0,0));
        p.setTypeface(Typeface.create("sans-serif",Typeface.BOLD));

        String navaidName = navaid.name;
        navaidName = navaidName + " (" + navaid.ident + ")";
        Rect airportNameRect = new Rect();
        p.getTextBounds(navaidName, 0, navaidName.length(), airportNameRect);

        int width = airportNameRect.width();

        int bitmapSize = 15;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(width,
                2+ airportNameRect.height() + bitmapSize,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        baseCanvas.drawText(navaidName, 0, bitmapSize + Math.abs(airportNameRect.top) , p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NavaidNameMarkerItem) {
            return (((NavaidNameMarkerItem)obj).navaid.ident.equals(this.navaid.ident));
        }
        else
            return false;
    }
}

