package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class AirportSymbolBaloonBase extends MarkerSymbol {
    public AirportSymbolBaloonBase(Bitmap bitmap, HotspotPlace hotspot) {
        super(bitmap, hotspot);
    }

    public static AirportSymbolBaloonBase GetAirportSymbol(Airport airport, Context context)
    {
        return new AirportSymbolBaloonBase(new AndroidBitmap(DrawAirportIcon(airport)), HotspotPlace.CENTER);
    }

    public static android.graphics.Bitmap DrawAirportIcon(Airport airport)
    {
        int size = 30;
        int radius = 12;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(size/10);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}