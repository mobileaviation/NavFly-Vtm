package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;



public class AirportSymbolLarge extends MarkerSymbol {
    public AirportSymbolLarge(Bitmap bitmap, HotspotPlace hotspot) {
        super(bitmap, hotspot);
    }

    public static AirportSymbolLarge GetAirportSymbol(Airport airport, Context context)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(context);
        airport.runways = db.getRunways().getRunwaysByAirport(airport.id);
        return new AirportSymbolLarge(new AndroidBitmap(draw(airport)), HotspotPlace.CENTER);
    }

    private static android.graphics.Bitmap draw(Airport airport)
    {
        int size = 30;
        int radius = 15;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}