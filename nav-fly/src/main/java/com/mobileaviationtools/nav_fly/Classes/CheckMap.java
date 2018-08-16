package com.mobileaviationtools.nav_fly.Classes;

import android.util.Log;

import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.map.Map;
import org.oscim.scalebar.DistanceUnitAdapter;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MetricUnitAdapter;

public class CheckMap {
    public CheckMap(Map map)
    {
        mMap = map;
        currentMapPosition = mMap.getMapPosition();
        firstCheck = true;
    }

    private double CHECKDISTANCE = 100;
    private String TAG = "CheckMap";

    private Map mMap;
    private MapPosition currentMapPosition;
    //private BoundingBox currentMapBoundingBox;
    private Boolean firstCheck;

    public Boolean Changed()
    {
        double distance = currentMapPosition.getGeoPoint().sphericalDistance(mMap.getMapPosition().getGeoPoint());
        //Log.i(TAG,"Checked Distance: " + distance + " meter");

        BoundingBox box = mMap.getBoundingBox(0);
        double display_diagonal_distance = new GeoPoint(box.minLatitudeE6, box.minLongitudeE6).sphericalDistance(
                new GeoPoint(box.maxLatitudeE6, box.maxLongitudeE6));

        // When the moved distance is more that 5% of the diagonal distance of the display
        Boolean _changed = ((distance/display_diagonal_distance)*100 > 5);
        if (_changed) currentMapPosition = mMap.getMapPosition();

        Boolean changed = (firstCheck) ? true : _changed;
        firstCheck = false;
        return (firstCheck) ? true : changed;
    }

}
