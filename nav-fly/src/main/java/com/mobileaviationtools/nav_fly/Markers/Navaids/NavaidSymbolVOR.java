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

public class NavaidSymbolVOR extends MarkerSymbol {
    public NavaidSymbolVOR(Bitmap bitmap, HotspotPlace hotspot, boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static NavaidSymbolVOR GetVORSymbol(Navaid navaid, Context context)
    {
        return new NavaidSymbolVOR(new AndroidBitmap(DrawNavaidsIcon(navaid)), HotspotPlace.CENTER, true);
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

        baseCanvas.drawLine(7,1,23,1, p);
        baseCanvas.drawLine(23,1,29,15, p);
        baseCanvas.drawLine(29,15,23,29, p);
        baseCanvas.drawLine(23,29,7,29, p);
        baseCanvas.drawLine(7,29,1,15, p);
        baseCanvas.drawLine(1,15,7,1, p);

        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);

        baseCanvas.drawCircle(15,15, 5, p);


        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
