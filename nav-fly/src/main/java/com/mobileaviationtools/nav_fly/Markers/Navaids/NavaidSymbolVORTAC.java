package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;
import android.graphics.Canvas;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportSymbolSmall;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class NavaidSymbolVORTAC extends MarkerSymbol {
    public NavaidSymbolVORTAC(Bitmap bitmap, HotspotPlace hotspot, boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static NavaidSymbolVORTAC GetVORTACSymbol(Navaid navaid, Context context)
    {
        return new NavaidSymbolVORTAC(new AndroidBitmap(DrawNavaidsIcon(navaid)), HotspotPlace.CENTER, false);
    }

    public static android.graphics.Bitmap DrawNavaidsIcon(Navaid navaid)
    {
        int size = 33;
        int radius = 12;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
