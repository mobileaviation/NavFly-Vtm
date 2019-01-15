package com.mobileaviationtools.nav_fly.Markers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

//import org.jeo.filter.In;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.io.InputStream;

public class AircraftMarkerItem extends MarkerItem {
    public static AircraftMarkerItem getNewAircraftMarkerItem(Context context, FspLocation location)
    {
        GeoPoint locationGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        AircraftMarkerItem markerItem = new AircraftMarkerItem("AircraftMarker",
                "AircraftMarker", locationGeoPoint);
        markerItem.setLocation(location);
        markerItem.context = context;
        markerItem.setMarker(markerItem.getAircraftMarkerSymbol());
        return markerItem;
    }

    private AircraftMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    private Context context;

    public void setLocation(FspLocation location)
    {
        GeoPoint locationGeoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
        this.geoPoint = locationGeoPoint;
        this.setRotation(location.getBearing());
    }

    private MarkerSymbol getAircraftMarkerSymbol()
    {
        return new MarkerSymbol(new AndroidBitmap(getAircraftIcon()), MarkerSymbol.HotspotPlace.CENTER, false);
    }

    private Bitmap getAircraftIcon()
    {
        Integer width = 100;
        Integer height = 1000;
        android.graphics.Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(width, height,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(4);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawLine(width/2, 0f, width/2, height, p);

//        BitmapFactory.Options o = new BitmapFactory.Options();
//        o.inScaled = false;
//
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.plane2);

//        InputStream is = context.getResources().openRawResource(R.drawable.plane2);
//        Bitmap icon = BitmapFactory.decodeStream(is);

        baseCanvas.drawBitmap(icon, 0, (height/2)-(icon.getHeight()/2), null);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
