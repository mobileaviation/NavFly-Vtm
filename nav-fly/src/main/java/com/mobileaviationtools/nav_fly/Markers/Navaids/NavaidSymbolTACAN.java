package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.graphics.Canvas;

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
        return new NavaidSymbolTACAN(new AndroidBitmap(draw(navaid)), HotspotPlace.CENTER, false);
    }

    private static android.graphics.Bitmap draw(Navaid navaid)
    {
        int size = 33;
        int radius = 12;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
