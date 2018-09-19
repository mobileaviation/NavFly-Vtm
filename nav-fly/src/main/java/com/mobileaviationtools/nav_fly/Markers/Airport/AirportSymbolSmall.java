package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.constraint.ConstraintLayout;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Runway;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class AirportSymbolSmall extends MarkerSymbol {
    public AirportSymbolSmall(Bitmap bitmap, HotspotPlace hotspot, Boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static AirportSymbolSmall GetAirportSymbol(Airport airport, Context context)
    {
        return new AirportSymbolSmall(new AndroidBitmap(draw(airport)), HotspotPlace.CENTER, false);
    }

    private static android.graphics.Bitmap draw(Airport airport)
    {
        RunwayHelpers runwayHelpers = new RunwayHelpers(airport);

        int size = 33;
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

        p.setStrokeWidth(3);
        if (airport.runways != null)
        {
            if (airport.runways.size()>0){
                for (Runway runway: airport.runways) {
                    runwayHelpers.DrawRunwayLine2(runway, baseCanvas, p);
                }
            }
        }

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
