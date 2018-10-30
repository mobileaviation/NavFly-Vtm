package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class NavaidSymbolNDB extends MarkerSymbol {
    public NavaidSymbolNDB(Bitmap bitmap, HotspotPlace hotspot, boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static NavaidSymbolNDB GetNDBSymbol(Navaid navaid, Context context)
    {
        return new NavaidSymbolNDB(new AndroidBitmap(DrawNavaidsIcon(navaid)), HotspotPlace.CENTER, false);
    }

    public static android.graphics.Bitmap DrawNavaidsIcon(Navaid navaid)
    {
        int size = 33;
        int radius = 12;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(3);
        p.setStyle(Paint.Style.STROKE);
        p.setPathEffect(new DashPathEffect(new float[]{3, 3}, 0));
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawCircle(15,15, 12, p);
        baseCanvas.drawCircle(15,15, 8, p);
        baseCanvas.drawCircle(15,15, 4, p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
