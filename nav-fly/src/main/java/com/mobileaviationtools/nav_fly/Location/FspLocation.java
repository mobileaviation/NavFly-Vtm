package com.mobileaviationtools.nav_fly.Location;

import android.location.Location;

import org.oscim.core.GeoPoint;

import java.lang.reflect.Array;

public class FspLocation extends Location {
    public FspLocation(String provider) {
        super(provider);
        createArrays();
    }

    public FspLocation(Location l) {
        super(l);
        createArrays();
    }

    public FspLocation(GeoPoint g, String provider)
    {
        super("provider");
        setGeopoint(g);
        createArrays();
    }

    private void createArrays()
    {
        turnCoordination = new Double[]{0d,0d};
        horizon = new Double[]{0d,0d};
    }

    public Double verticalSpeed;
    public Double[] turnCoordination;
    public Double[] horizon;

    private double distanceRemaining;

    public static FspLocation getInstance(Location location)
    {
        return new FspLocation(location);
    }

    public void setGeopoint(GeoPoint geopoint)
    {
        this.setLatitude(geopoint.getLatitude());
        this.setLongitude(geopoint.getLongitude());
    }

    public GeoPoint getGeopoint()
    {
        return new GeoPoint(this.getLatitude(), this.getLongitude());
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

    public void SetDistanceRemaining(Double distanceRemaining)
    {
        this.distanceRemaining = distanceRemaining;
    }

    public Double GetDistanceRemaining()
    {
        return distanceRemaining;
    }

}
