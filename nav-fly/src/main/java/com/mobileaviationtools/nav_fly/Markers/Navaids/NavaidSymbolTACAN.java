package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class NavaidSymbolTACAN extends MarkerSymbol {
    public NavaidSymbolTACAN(Bitmap bitmap, HotspotPlace hotspot, boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static NavaidSymbolTACAN GetTACANSymbol(Navaid navaid, Context context)
    {
        return new NavaidSymbolTACAN(new AndroidBitmap(DrawNavaidsIcon(navaid)), HotspotPlace.CENTER, false);
    }

    public static android.graphics.Bitmap DrawNavaidsIcon(Navaid navaid)
    {
        int size = 30;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(2);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawLine(12,5,19,5, p);
        p.setStrokeWidth(8);
        baseCanvas.drawLine(22,2,27,10, p);
        p.setStrokeWidth(2);
        baseCanvas.drawLine(24,12,19,19, p);
        p.setStrokeWidth(8);
        baseCanvas.drawLine(20,22,11,22, p);
        p.setStrokeWidth(2);
        baseCanvas.drawLine(12,19,7,12, p);
        p.setStrokeWidth(8);
        baseCanvas.drawLine(4,10,9,3, p);

        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);

        baseCanvas.drawCircle(16,12, 3, p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
