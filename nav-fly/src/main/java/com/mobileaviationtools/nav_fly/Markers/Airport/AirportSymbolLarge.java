package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Runway;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.locationtech.jts.geom.Coordinate;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;



public class AirportSymbolLarge extends MarkerSymbol {
    public AirportSymbolLarge(Bitmap bitmap, HotspotPlace hotspot, Boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static AirportSymbolLarge GetAirportSymbol(Airport airport, Context context)
    {
        return new AirportSymbolLarge(new AndroidBitmap(DrawAirportIcon(airport)), HotspotPlace.CENTER, false);
    }

    public static android.graphics.Bitmap DrawAirportIcon(Airport airport)
    {
        RunwayHelpers runwayHelpers = new RunwayHelpers(airport);

        int size = 40;
        int radius = 20;

        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(0);
        p.setStyle(Paint.Style.FILL);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawCircle(size/2,size/2,radius,p);

        p.setColor(Color.argb(255,240,240,240));
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

    private static void calculate(RunwayHelpers helpers, double symbolWidth, double symbolHeight, Runway runway)
    {
        Coordinate[] boundary = helpers.getBoundary().getCoordinates();
        // get basepoint
        Coordinate basepoint = boundary[0];
        // Calculate Width and Heigth
        double width = boundary[2].x - boundary[1].x;
        double heigth = boundary[1].y - boundary[0].y;
        double symbolSize = (symbolWidth>symbolHeight) ? symbolWidth : symbolHeight;

        // TODO XFactor needs to be adjusted conform the latitude
        double xFactor = width / symbolSize;
        double yFactor = heigth / symbolSize;

        double x1 = (runway.le_longitude_deg-basepoint.x)/xFactor;
        double x2 = (runway.he_longitude_deg-basepoint.x)/xFactor;
        double y1 = (runway.le_latitude_deg-basepoint.y)/yFactor;
        double y2 = (runway.he_latitude_deg-basepoint.y)/yFactor;

        int test =1;

    }
}