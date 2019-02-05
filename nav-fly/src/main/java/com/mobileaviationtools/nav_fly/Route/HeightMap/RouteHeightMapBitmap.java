package com.mobileaviationtools.nav_fly.Route.HeightMap;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

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
        imageWidth = imageWidth - Math.round(Helpers.dpToPx(25));
        doDrawRoute(canvas, points);
        return this.bitmap;
    }

    public Bitmap drawPositionOnTop(double index, double startIndex, double endIndex, double altitude)
    {
        double mapXpos =  (((double)imageWidth) / (endIndex-startIndex)) * index;
        mapXpos = mapXpos + Helpers.dpToPx(10);
        double altitudeFt = altitude * meterToFeet;

        double mapYpos = calcYFrom(altitudeFt, minElevation, maxAltitudePlus);

        Canvas canvas = new Canvas(bitmap);
        Paint pLine = new Paint();
        pLine.setColor(Color.RED);
        pLine.setStrokeWidth(Helpers.dpToPx(5));

        canvas.drawCircle((float)mapXpos, (float) mapYpos, Helpers.dpToPx(2), pLine);
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
        pLine.setStrokeWidth(Helpers.dpToPx(5));

        double startX = Helpers.dpToPx(10);
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

        startX = Helpers.dpToPx(10);
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

        drawWaypoints(routePoints, canvas);
    }

    private void drawHeightLines(double minElevation, double maxAltitude, Canvas canvas)
    {
        double drawAltitude = ((minElevation-100)<0)? 0 : minElevation -100;
        Paint p = new Paint();
        p.setStrokeWidth(Helpers.dpToPx(1));
        p.setTextSize(Helpers.dpToPx(10));

        double add = 500;
        if ((maxAltitude - minElevation)>5000) add = 1000;

        while (drawAltitude<maxAltitude)
        {
            Long D = ((Math.round(drawAltitude) + 499) /500) * 500;
            double Y = calcYFrom(D, minElevation, maxAltitude);
            p.setColor(Color.GRAY);
            canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
            p.setColor(Color.BLACK);
            canvas.drawText(D.toString(), Helpers.dpToPx(5), (float)Y-Helpers.dpToPx(1), p);
            drawAltitude = drawAltitude + add;
        }

        double Y = calcYFrom(drawAltitude, minElevation, maxAltitude);
        p.setColor(Color.GRAY);
        canvas.drawLine(0, (float)Y, (float)imageWidth, (float)Y, p);
        p.setColor(Color.BLACK);
        canvas.drawText(Double.toString(drawAltitude), Helpers.dpToPx(5), (float)Y-Helpers.dpToPx(1), p);

    }

    private void drawWaypoints(RoutePoints points, Canvas canvas)
    {
        Double[] waypointIndexes = points.getWaypointIndexes();
        double startIndex = points.getStartIndex();
        double endIndex = points.getEndIndex();
        Resources res = vars.context.getResources();
        Bitmap markerBitmap = BitmapFactory.decodeResource(res, R.drawable.marker_poi);


        for (Double p: waypointIndexes)
        {
            ExtCoordinate c = points.findPointNearestTo(p);
            double mapXpos =  (((double)imageWidth) / (endIndex-startIndex)) * c.index;
            mapXpos = mapXpos + Helpers.dpToPx(10) - (markerBitmap.getWidth()/2);
            double mapYpos = calcYFrom(c.altitude, minElevation, maxAltitudePlus) - (markerBitmap.getHeight()/2);

            canvas.drawBitmap(markerBitmap, (float)mapXpos, (float)mapYpos, new Paint());
        }
    }

    private double calcYFrom(double value, double min, double max)
    {
        double size = max - min;
        double factor = (imageHeight) / size;
        double retValue = factor * (value - min);
        return imageHeight - (retValue + Helpers.dpToPx(5));
    }
}
