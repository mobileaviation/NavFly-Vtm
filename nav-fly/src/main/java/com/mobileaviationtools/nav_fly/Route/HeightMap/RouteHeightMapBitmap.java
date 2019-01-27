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
    private double maxAltitude;
    private double maxAltitudePlus;
    private double minElevation;
    private double meterToFeet = 3.2808399d; // meters to feet

    public Bitmap drawRoute(RoutePoints points)
    {
        Canvas canvas = new Canvas(bitmap);
        imageWidth = imageWidth - Helpers.dpToPx(100);
        doDrawRoute(canvas, points);
        return this.bitmap;
    }

    public Bitmap drawPoistionOnTop(double index, double startIndex, double endIndex, double altitude)
    {
        double mapXpos =  (((double)imageWidth) / (endIndex-startIndex)) * index;
        mapXpos = mapXpos + Helpers.dpToPx(50);
        double altitudeFt = altitude * meterToFeet;

        double mapYpos = calcYFrom(altitudeFt, minElevation, maxAltitudePlus);

        Canvas canvas = new Canvas(bitmap);
        Paint pLine = new Paint();
        pLine.setColor(Color.RED);
        pLine.setStrokeWidth(Helpers.dpToPx(20));

        canvas.drawCircle((float)mapXpos, (float) mapYpos, Helpers.dpToPx(10), pLine);
        return bitmap;
    }

    private void doDrawRoute(Canvas canvas, RoutePoints routePoints)
    {
        maxAltitude = routePoints.getMaxAltitude().altitude;
        double maxElevation = routePoints.getMaxElevation().elevation;
        if (maxAltitude<maxElevation) maxAltitude = maxElevation;
        maxAltitudePlus = maxAltitude + (maxAltitude * .15);
        minElevation = routePoints.getMinElevation().elevation;

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
        double drawAltitude = ((minElevation-100)<0)? 0 : minElevation -100;
        Paint p = new Paint();
        p.setStrokeWidth(Helpers.dpToPx(4));
        p.setTextSize(Helpers.dpToPx(40));

        double add = 500;
        if ((maxAltitude - minElevation)>5000) add = 1000;

        while (drawAltitude<maxAltitude)
        {
            Long D = ((Math.round(drawAltitude) + 499) /500) * 500;
            double Y = calcYFrom(D, minElevation, maxAltitude);
            p.setColor(Color.GRAY);
            canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
            p.setColor(Color.BLACK);
            canvas.drawText(D.toString(), Helpers.dpToPx(20), (float)Y-Helpers.dpToPx(4), p);
            drawAltitude = drawAltitude + add;
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
        double retValue = factor * (value - min);
        return imageHeight - (retValue + Helpers.dpToPx(20));
    }
}
