package com.mobileaviationtools.nav_fly.Info;

import org.oscim.core.GeoPoint;

public class City {
    public String name;
    public Double latitude;
    public Double longitude;
    public Double distance;
    public Double heading;
    public Double radial;

    private double meterToNMile = 0.000539956803d;

    public static City newInstance(com.mobileaviationtools.airnavdata.Entities.City city, GeoPoint location)
    {
        City c = new City();
        c.name = city.name;
        c.latitude = city.latitude;
        c.longitude = city.longitude;
        c.distance = location.sphericalDistance(new GeoPoint(c.latitude, c.longitude));
        c.heading = location.bearingTo(new GeoPoint(c.latitude, c.longitude));
        c.radial = new GeoPoint(c.latitude, c.longitude).bearingTo(location);
        return c;
    }

    public String getBearingStr()
    {
        Long h = Math.round(heading);
        return h.toString()+ "º";
    }

    public String getDistanceNMStr()
    {
        Double dNm = distance * meterToNMile;
        Long h = Math.round(dNm);
        return h.toString()+ "NM";
    }

    public Double getRadial()
    {
        return radial;
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
