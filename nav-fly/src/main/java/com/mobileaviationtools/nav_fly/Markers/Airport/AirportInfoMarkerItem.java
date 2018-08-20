package com.mobileaviationtools.nav_fly.Markers.Airport;

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
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.util.concurrent.Executor;

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
//        class DrawTask extends AsyncTask<Void, Void, Void>
//        {
//            public AirportInfoMarkerItem item;
//            private MarkerSymbol symbol;
//            @Override
//            protected Void doInBackground(Void... voids) {
//                symbol = new MarkerSymbol(new AndroidBitmap(item.draw()), MarkerSymbol.HotspotPlace.TOP_CENTER);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                item.setMarker(symbol);
//                super.onPostExecute(aVoid);
//            }
//        }
//
//        DrawTask drawTask = new DrawTask();
//        drawTask.item = this;
//        drawTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, null);

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

        String airportName = shortenName(airport.name);
        airportName = airportName + " (" + airport.ident + ")";
        Rect airportNameRect = new Rect();
        p.getTextBounds(airportName, 0, airportName.length(), airportNameRect);

        String frequencies = "";
        for (Frequency f : airport.frequencies)
        {
            frequencies = frequencies + f.frequency_mhz.toString() + " ";
        }
        Rect frequenciesRect = new Rect();
        p.getTextBounds(frequencies, 0, frequencies.length(), frequenciesRect);
        int width = (frequenciesRect.width()>airportNameRect.width()) ? frequenciesRect.width() : airportNameRect.width();

        int bitmapSize = (airport.type== AirportType.large_airport)? 20 : 15;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(width,
                2+ (airportNameRect.height() * 2) + bitmapSize,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        baseCanvas.drawText(airportName, 0, bitmapSize + Math.abs(airportNameRect.top) , p);
        p.setTypeface(Typeface.create("sans-serif",Typeface.ITALIC));
        baseCanvas.drawText(frequencies, 0, 2 + bitmapSize + Math.abs(airportNameRect.top) + frequenciesRect.height(), p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
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
