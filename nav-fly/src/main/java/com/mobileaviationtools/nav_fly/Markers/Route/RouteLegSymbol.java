package com.mobileaviationtools.nav_fly.Markers.Route;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Leg;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.marker.MarkerSymbol;

public class RouteLegSymbol extends MarkerSymbol {
    public RouteLegSymbol(Bitmap bitmap, HotspotPlace hotspot, Boolean billboard) {
        super(bitmap, hotspot, billboard);
    }

    public static RouteLegSymbol GetRouteLegSymbol(Leg leg, Context context)
    {
        return new RouteLegSymbol(new AndroidBitmap(DrawRouteLegIcon(leg, context)), HotspotPlace.CENTER, false);
    }

    public static android.graphics.Bitmap DrawRouteLegIcon(Leg leg, Context context)
    {
        return GetIcon((float)leg.getDistanceNM(), (float)leg.getBearing(), context);
    }

    public static android.graphics.Bitmap GetIcon(float distance, float compass_heading, Context context)
    {
        BitmapFactory.Options op = new BitmapFactory.Options();
        op.inMutable = true;
        android.graphics.Bitmap courseBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.direction_marker_square, op);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(CanvasAdapter.getScale() * 16f);
        textPaint.setFakeBoldText(true);
        textPaint.setTextAlign(Paint.Align.LEFT);

        Canvas aircourseCanvas = new Canvas(courseBitmap);
        aircourseCanvas.save();
        String h = "000" + Integer.toString(Math.round(compass_heading));
        String d = Integer.toString(Math.round(distance));
        h = h.substring(h.length()-3) + "\u00b0";

        float x = 40f;
        if (compass_heading<180) {
            aircourseCanvas.rotate(180, aircourseCanvas.getWidth()/2, aircourseCanvas.getHeight()/2);
            x=20f;
        }

        aircourseCanvas.drawText(h, CanvasAdapter.getScale() * x, CanvasAdapter.getScale() * 50f, textPaint);
        textPaint.setTextSize(CanvasAdapter.getScale() *14f);
        aircourseCanvas.drawText(d, CanvasAdapter.getScale() * x, CanvasAdapter.getScale() * 62f, textPaint);
        textPaint.setTextSize(CanvasAdapter.getScale() * 10f);
        aircourseCanvas.drawText("NM", CanvasAdapter.getScale() * x+50f, CanvasAdapter.getScale() * 62f, textPaint);

        if (compass_heading<180){
            aircourseCanvas.restore();
        }
        aircourseCanvas.save();

        return courseBitmap;
    }
}
