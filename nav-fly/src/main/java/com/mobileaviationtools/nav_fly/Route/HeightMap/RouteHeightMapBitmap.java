package com.mobileaviationtools.nav_fly.Route.HeightMap;


import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.GlobalVars;

public class RouteHeightMapBitmap  {

    public RouteHeightMapBitmap(Bitmap bitmap, GlobalVars vars)
    {
        this.bitmap = bitmap;
        this.vars = vars;
        imageHeight = this.bitmap.getHeight();
        imageWidth = this.bitmap.getWidth();
    }

    private Bitmap bitmap;
    private GlobalVars vars;
    private Integer imageWidth;
    private Integer imageHeight;

    public Bitmap drawRoute(RoutePoints points)
    {
        Canvas canvas = new Canvas(bitmap);
        imageWidth = imageWidth - Helpers.dpToPx(100);
        doDrawRoute(canvas, points);
        return this.bitmap;
    }

    private void doDrawRoute(Canvas canvas, RoutePoints routePoints)
    {
        double maxAltitude = routePoints.getMaxAltitude().altitude;
        double maxAltitudePlus = maxAltitude + (maxAltitude * .15);
        double minElevation = routePoints.getMinElevation().elevation;

        drawHeightLines(minElevation, maxAltitudePlus, canvas);

        //double addX = (double)imageWidth / (double)routePoints.size();

        Paint pLine = new Paint();
        pLine.setColor(Color.GREEN);
        pLine.setStrokeWidth(Helpers.dpToPx(20));

        double startX = Helpers.dpToPx(50);
        double startY = calcYFrom(routePoints.get(0).elevation, minElevation, maxAltitudePlus);
        for (ExtCoordinate c: routePoints) {
            double addX = (double)imageWidth / (double)routePoints.size();
            double nextX = startX + addX;
            double nextY = calcYFrom(c.elevation, minElevation, maxAltitudePlus);
            canvas.drawLine((float)startX, (float)startY, (float)nextX, (float)nextY, pLine);
            startX = nextX;
            startY = nextY;
        }

        pLine.setColor(Color.BLUE);

        startX = Helpers.dpToPx(50);
        startY = calcYFrom(routePoints.get(0).altitude, minElevation, maxAltitudePlus);
        for (ExtCoordinate c: routePoints) {
            double addX = (c.distanceToNext_meter==0) ? 0 :
                    ((double)imageWidth / (double)routePoints.getTotalDistance_meter()) * c.distanceToNext_meter;
            double nextX = startX + addX;
            double nextY = calcYFrom(c.altitude, minElevation, maxAltitudePlus);
            canvas.drawLine((float)startX, (float)startY, (float)nextX, (float)nextY, pLine);
            startX = nextX;
            startY = nextY;
        }
    }

    private void drawHeightLines(double minElevation, double maxAltitude, Canvas canvas)
    {
        double drawAltitude = 0;
        Paint p = new Paint();
        p.setStrokeWidth(Helpers.dpToPx(4));
        p.setTextSize(Helpers.dpToPx(40));

        while (drawAltitude<maxAltitude)
        {
            double Y = calcYFrom(drawAltitude, minElevation, maxAltitude);
            p.setColor(Color.GRAY);
            canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
            p.setColor(Color.BLACK);
            canvas.drawText(Double.toString(drawAltitude), Helpers.dpToPx(20), (float)Y-Helpers.dpToPx(4), p);
            drawAltitude = drawAltitude + 500;
        }

        double Y = calcYFrom(drawAltitude, minElevation, maxAltitude);
        p.setColor(Color.GRAY);
        canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
        p.setColor(Color.BLACK);
        canvas.drawText(Double.toString(drawAltitude), Helpers.dpToPx(20), (float)Y-Helpers.dpToPx(4), p);

    }

    private double calcYFrom(double value, double min, double max)
    {
        double size = max - min;
        double factor = (imageHeight) / size;
        double retValue = factor * value;
        return imageHeight - (retValue + Helpers.dpToPx(20));
    }
}
