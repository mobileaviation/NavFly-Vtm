package com.mobileaviationtools.nav_fly.Info;

import org.oscim.core.GeoPoint;

public class City {
    public String name;
    public Double latitude;
    public Double longitude;
    public Double distance;
    public Double heading;

    public static City newInstance(com.mobileaviationtools.airnavdata.Entities.City city, GeoPoint location)
    {
        City c = new City();
        c.name = city.name;
        c.latitude = city.latitude;
        c.longitude = city.longitude;
        c.distance = location.sphericalDistance(new GeoPoint(c.latitude, c.longitude));
        c.heading = location.bearingTo(new GeoPoint(c.latitude, c.longitude));
        return c;
    }

//    @Override
//    public boolean equals(Object obj) {
//        if (obj instanceof City)
//        {
//            City c = (City)obj;
//            return (c.distance < this.distance);
//        }
//        else return false;
//    }
}
