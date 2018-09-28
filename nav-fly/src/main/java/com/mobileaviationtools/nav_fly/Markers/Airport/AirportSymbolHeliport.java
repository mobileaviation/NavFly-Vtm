package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

import java.util.ConcurrentModificationException;

public class AirportSymbolHeliport extends MarkerSymbol {
    public AirportSymbolHeliport(Bitmap bitmap, HotspotPlace hotspot) {
        super(bitmap, hotspot);
    }

    public static AirportSymbolHeliport GetAirportSymbol(Airport airport, Context context)
    {
        return new AirportSymbolHeliport(new AndroidBitmap(DrawAirportIcon(airport)), HotspotPlace.CENTER);
    }

    public static android.graphics.Bitmap DrawAirportIcon(Airport airport)
    {
        int size = 30;
        int radius = 13;

        String text = "H";

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(17);
        p.setStrokeWidth(size/10);
        p.setStyle(Paint.Style.STROKE);
        p.setTextAlign(Paint.Align.CENTER);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        p.setStrokeWidth(2);
        Rect textMathRect = new Rect();
        p.getTextBounds(text, 0, 1, textMathRect);

        baseCanvas.drawText(text, size/2, (size/2)+ (textMathRect.height()/2), p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}