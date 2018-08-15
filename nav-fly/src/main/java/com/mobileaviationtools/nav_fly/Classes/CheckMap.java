package com.mobileaviationtools.nav_fly.Classes;

import android.util.Log;

import org.oscim.core.BoundingBox;
import org.oscim.core.MapPosition;
import org.oscim.map.Map;
import org.oscim.scalebar.DistanceUnitAdapter;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MetricUnitAdapter;

public class CheckMap {
    public CheckMap(Map map)
    {
        mMap = map;
        currentMapBoundingBox = mMap.getBoundingBox(0);
        currentMapPosition = mMap.getMapPosition();
        firstCheck = true;
    }

    private double CHECKDISTANCE = 100;
    private String TAG = "CheckMap";

    private Map mMap;
    private MapPosition currentMapPosition;
    private BoundingBox currentMapBoundingBox;
    private Boolean firstCheck;

    public Boolean Changed()
    {
        double distance = currentMapPosition.getGeoPoint().sphericalDistance(mMap.getMapPosition().getGeoPoint());
        Log.i(TAG,"Checked Distance: " + distance + " meter");

        Boolean _changed = false;

        Boolean changed = (firstCheck) ? true : _changed;
        firstCheck = false;
        return (firstCheck) ? true : changed;
    }

}
