package com.mobileaviationtools.nav_fly.Route;

import android.util.Log;

import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.Location.FspLocation;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Legs extends ArrayList<Leg> {
    private ArrayList<Double> getSortedMap(HashMap<Double, Leg> map)
    {
        ArrayList<Double> doubles = new ArrayList<>();
        for (Double dis : map.keySet())
        {
            doubles.add(dis);
        }
        Collections.sort(doubles);

        return  doubles;
    }

    private Leg selectedLeg;
    private String TAG = "Legs";
    private double meterToNMile = 0.000539956803d;

    public Leg getActiveLeg(FspLocation location)
    {
        GeometryFactory geometryFactory = new GeometryFactory();
        Geometry p = geometryFactory.createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));
        ArrayList<Leg> foundLeg = new ArrayList<>();
        for (Leg l : this)
        {
            if ( l.legBuffer.contains(p))
            {
                foundLeg.add(l);
            }
        }
        if (foundLeg.size()==1)
        {
            selectedLeg = foundLeg.get(0);
            return selectedLeg;
        }

        if (foundLeg.size()>1)
        {
            GeometryFactory factory = new GeometryFactory();

            HashMap<Double, Leg> distanceMap = new HashMap<>();
            for (Leg l:foundLeg)
            {
                Geometry line = factory.createLineString(l.getLegCoordinates());
                Geometry locPoint = factory.createPoint(new Coordinate(location.getLongitude(), location.getLatitude()));

                Double distance = locPoint.distance(line);
                distanceMap.put(distance, l);
            }

            selectedLeg = distanceMap.get(getSortedMap(distanceMap).get(0));

            Log.i(TAG, selectedLeg.toString());

        }

        return selectedLeg;
    }

    public double remainingDistanceNM(FspLocation location)
    {
        double distance = 0d;
        if (selectedLeg != null)
        {
            Integer index = this.indexOf(selectedLeg);
            distance = location.getGeopoint().sphericalDistance(selectedLeg.endWaypoint.point);

            for (int i=index+1; i<this.size(); i++)
            {
                distance = distance + this.get(i).getDistance();
            }
        }
        else
        {
            return this.get(this.size()-1).getTotalDistanceNm();
        }

        return distance * meterToNMile;
    }
}
