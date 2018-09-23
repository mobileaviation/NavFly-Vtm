package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class WaypointSymbol extends MarkerSymbol{
    public WaypointSymbol(Bitmap bitmap, MarkerSymbol.HotspotPlace hotspot) {
        super(bitmap, hotspot);
    }

    public static WaypointSymbol GetWaypointSymbol(Waypoint waypoint, Context context)
    {
        return new WaypointSymbol(new AndroidBitmap(draw(waypoint)), MarkerSymbol.HotspotPlace.CENTER);
    }

    private static android.graphics.Bitmap draw(Waypoint waypoint)
    {
        int size = 30;
        int radius = 12;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(150, 126,199,10));

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        p.setAntiAlias(true);
        p.setStrokeWidth(size/10);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.DKGRAY);

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
