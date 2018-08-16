package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class AirportSymbolSeaBase extends MarkerSymbol {
    public AirportSymbolSeaBase(Bitmap bitmap, HotspotPlace hotspot) {
        super(bitmap, hotspot);
    }

    public static AirportSymbolSeaBase GetAirportSymbol(Airport airport, Context context)
    {
        return new AirportSymbolSeaBase(new AndroidBitmap(draw(airport)), HotspotPlace.CENTER);
    }

    private static android.graphics.Bitmap draw(Airport airport)
    {
        int size = 30 * java.lang.Math.round(CanvasAdapter.getScale());
        int radius = 12 * java.lang.Math.round(CanvasAdapter.getScale());

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(size/10);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.argb(255,168,103,148));

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        return baseBitmap;
    }
}
