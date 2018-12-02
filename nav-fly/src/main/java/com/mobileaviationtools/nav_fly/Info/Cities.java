package com.mobileaviationtools.nav_fly.Info;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Cities extends ArrayList<City> {
    public Cities(Context context, GeoPoint location)
    {
        this.context = context;
        getCities(location);
    }

    private Context context;

    private void getCities(GeoPoint location)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(context);
        Long m = 40l * 1609l;
        Geometry c = GeometryHelpers.getCircle(location, m);
        Geometry b = c.getEnvelope();
        List<com.mobileaviationtools.airnavdata.Entities.City> entitiesCities = null;
        if (b.getNumPoints()>3) {
            Coordinate[] coordinates = b.getCoordinates();
            entitiesCities =
                    db.getCities().getCitiesWithinBounds(coordinates[0].x,
                            coordinates[2].x,
                            coordinates[2].y,
                            coordinates[0].y);
        }

        if (entitiesCities != null) {
            for (com.mobileaviationtools.airnavdata.Entities.City city : entitiesCities) {
                add(City.newInstance(city, location));
            }
        }
    }

    public Integer getNearestCity()
    {
        Double min = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < this.size(); i++) {
            City s = this.get(i);
            if (Double.compare(s.distance, min) < 0) {
                min = s.distance;
                index = i;
            }
        }
        return index;
    }
}
