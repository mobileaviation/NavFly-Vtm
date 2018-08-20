package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.graphics.Canvas;

import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class NavaidSymbolNDBDME extends MarkerSymbol {
    public NavaidSymbolNDBDME(Bitmap bitmap, HotspotPlace hotspot, boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static NavaidSymbolNDBDME GetNDBDMESymbol(Navaid navaid, Context context)
    {
        return new NavaidSymbolNDBDME(new AndroidBitmap(draw(navaid)), HotspotPlace.CENTER, false);
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
