package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

import java.util.ArrayList;

import static java.lang.Math.PI;
import static java.lang.Math.log;
import static java.lang.Math.tan;

public class RunwayHelpers {
    public RunwayHelpers(Airport airport)
    {
        this.airport = airport;
        boundary = null;
        center = null;
        process();
    }

    private  Airport airport;

    public Geometry getBoundary() {
        return boundary;
    }
    private Geometry boundary;

    public Point getCenter() {
        return center;
    }
    private Point center;

    private double rightX;
    private double bottomY;

    private void process()
    {
        if (airport.runways != null)
        {
            if (airport.runways.size()>0)
            {
                ArrayList<Geometry> _runwayList = new ArrayList<>();
                for (Runway runway : airport.runways) {
                    if ((runway.le_longitude_deg>0) && (runway.le_latitude_deg>0) && (runway.he_longitude_deg>0)  && (runway.he_latitude_deg>0)) {
                        Coordinate[] runwayLine = new Coordinate[2];
                        runwayLine[0] = new Coordinate(runway.le_longitude_deg, runway.le_latitude_deg);
                        runwayLine[1] = new Coordinate(runway.he_longitude_deg, runway.he_latitude_deg);
                        Geometry r = new GeometryFactory().createLineString(runwayLine);
                        _runwayList.add(r);
                    }
                }

                if (_runwayList.size()>0) {
                    GeometryCollection runwayLines = new GeometryFactory().createGeometryCollection(_runwayList.toArray(new Geometry[_runwayList.size()]));
                    boundary = runwayLines.getEnvelope();
                    center = boundary.getCentroid();
                }
            }
        }
    }

    private void calculatePixelCoords()
    {
//        if (boundary != null) {
//            Coordinate[] b = boundary.getCoordinates();
//            double width = b[2].x - b[1].x;
//            double heigth = b[1].y - b[0].y;
//            double topX =
//        }
    }

    public void DrawRunwayLine3(Runway runway, Canvas canvas, Paint paint)
    {
        if (boundary != null)
        {
            double symbolWidth = canvas.getWidth() * 0.7;
            double symbolHeight = canvas.getHeight() * 0.7;

            Coordinate rLe = new Coordinate(runway.le_longitude_deg, runway.le_latitude_deg);
            Coordinate rHe = new Coordinate(runway.he_longitude_deg, runway.he_latitude_deg);
            Coordinate c1 = locationToXYMercator(rLe,symbolWidth, symbolHeight);
            Coordinate c2 = locationToXYMercator(rHe,symbolWidth, symbolHeight);

            int i = 0;
        }
    }

    public void DrawRunwayLine2(Runway runway, Canvas canvas, Paint paint)
    {
        if (boundary != null) {
            Coordinate[] b = boundary.getCoordinates();
            Coordinate c = center.getCoordinate();
            double width = b[2].x - b[1].x;
            double heigth = b[1].y - b[0].y;
            double canvasWidth = canvas.getWidth();
            double canvasHeight = canvas.getHeight();

            double symbolWidth = canvas.getWidth() * 0.6;
            double symbolHeight = canvas.getHeight() * 0.6;

            double xFactor = width / symbolWidth;
            double yFactor = heigth / symbolHeight;

            double centerX = ((c.x) / xFactor);
            double centerY = ((c.y) / yFactor);

            double x1 = (runway.le_longitude_deg / xFactor);// * Math.cos(Math.toRadians(runway.le_latitude_deg));
            double x2 = (runway.he_longitude_deg / xFactor);// * Math.cos(Math.toRadians(runway.he_latitude_deg));
            double y1 = (runway.le_latitude_deg / yFactor);
            double y2 = (runway.he_latitude_deg / yFactor);

            x1 = (canvasWidth / 2) + (x1 - centerX);
            x2 = (canvasWidth / 2) + (x2 - centerX);
            y1 = canvasHeight - ((canvasHeight / 2) + (y1 - centerY));
            y2 = canvasHeight - ((canvasHeight / 2) + (y2 - centerY));

            canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);

            int i = 0;
        }
    }

    public void DrawRunwayLine(Runway runway, Canvas canvas, Paint paint)
    {
        if (boundary != null) {
            Coordinate[] b = boundary.getCoordinates();
            double width = b[2].x - b[1].x;
            double heigth = b[1].y - b[0].y;

            Coordinate c = center.getCoordinate();

            double symbolWidth = canvas.getWidth() * 0.7;
            double symbolAddWidth = (canvas.getWidth() - symbolWidth) / 2;
            double symbolHeight = canvas.getHeight() * 0.7;
            double symbolAddHeight = (canvas.getHeight() - symbolHeight) / 2;
            double symbolSize = (symbolWidth > symbolHeight) ? symbolWidth : symbolHeight;
            double symbolAddSize = (symbolAddWidth > symbolAddHeight) ? symbolAddWidth : symbolAddHeight;

            double xFactor = width / symbolSize;
            double yFactor = heigth / symbolSize;

            double xCenter = ((((c.x - b[0].x)) / xFactor) * Math.cos(Math.toRadians(c.y)));
            double lelon = (runway.le_longitude_deg - b[0].x) + (c.x - b[0].x);
            double helon = (runway.he_longitude_deg - b[0].x) + (c.x - b[0].x);

            double x1 = ((lelon / xFactor) * Math.cos(Math.toRadians(runway.le_latitude_deg)));// + ((symbolAddSize/Math.cos(Math.toRadians(b[2].y))) * 2);
            //x1 = x1 - ((symbolAddSize/Math.cos(Math.toRadians(b[2].y))) * 2);
            //double x1 = (((runway.le_longitude_deg-b[0].x)/xFactor) + symbolAddSize) * Math.cos(Math.toRadians(runway.le_latitude_deg));
            x1 = x1 + (xCenter / 2);//(symbolAddSize / Math.cos(Math.toRadians(b[2].y)));
            double x2 = ((helon / xFactor) * Math.cos(Math.toRadians(runway.he_latitude_deg)));// + ((symbolAddSize/Math.cos(Math.toRadians(b[2].y))) * 2);
            //x2 = x2 - ((symbolAddSize/Math.cos(Math.toRadians(b[2].y))) * 2);
            //double x2 = (((runway.he_longitude_deg-b[0].x)/xFactor) + symbolAddSize) * Math.cos(Math.toRadians(runway.he_latitude_deg));
            x2 = x2 + (xCenter / 2);//(symbolAddSize / Math.cos(Math.toRadians(b[2].y)));
            //if (x1<x2) add = (canvas.getWidth()/2); else

            // IF one runway this seems to work
            //double mid = (canvas.getWidth()/2);
            //double add = (x2-x1) / 2;
            //x1 = x1 + (mid-add);
            //x2 = x2 + (mid-add);

            double y1 = symbolHeight - ((runway.le_latitude_deg - b[0].y) / yFactor) + symbolAddSize;
            double y2 = symbolHeight - ((runway.he_latitude_deg - b[0].y) / yFactor) + symbolAddSize;

            canvas.drawLine((float) x1, (float) y1, (float) x2, (float) y2, paint);
        }


    }

    private Coordinate locationToXYMercator(Coordinate location, double mapWidth, double mapHeight)
    {
        double latitude    = location.y;// 41.145556; // (φ)
        double longitude   = location.x;// -73.995;   // (λ)

        //mapWidth    = 200;
        //mapHeight   = 100;

// get x value
        double x = (longitude+180)*(mapWidth/360);

// convert from degrees to radians
        double latRad = latitude*PI/180;

// get y value
        double mercN = log(tan((PI/4)+(latRad/2)));
        double y     = (mapHeight/2)-(mapWidth*mercN/(2*PI));

        return new Coordinate(x,y);
    }
}
