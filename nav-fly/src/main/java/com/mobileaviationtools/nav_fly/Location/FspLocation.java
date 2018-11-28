package com.mobileaviationtools.nav_fly.Location;

import android.location.Location;

import org.oscim.core.GeoPoint;

public class FspLocation extends Location {
    public FspLocation(String provider) {
        super(provider);
    }

    public FspLocation(Location l) {
        super(l);
    }

    public FspLocation(GeoPoint g, String provider)
    {
        super("provider");
        setGeopoint(g);
    }

    public static FspLocation getInstance(Location location)
    {
        return new FspLocation(location);
    }

    public void setGeopoint(GeoPoint geopoint)
    {
        this.setLatitude(geopoint.getLatitude());
        this.setLongitude(geopoint.getLongitude());
    }

    public void Assign(FspLocation location)
    {
        this.setLongitude(location.getLongitude());
        this.setLatitude(location.getLatitude());
        this.setAltitude(location.getAltitude());
        this.setBearing(location.getBearing());
        this.setSpeed(location.getSpeed());
        this.setAccuracy(location.getAccuracy());

        //FspLocation l = (FspLocation)location.clone();
    }

}
