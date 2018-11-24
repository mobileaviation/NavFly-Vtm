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
        this.setLatitude(g.getLatitude());
        this.setLongitude(g.getLongitude());
    }

    public static FspLocation getInstance(Location location)
    {
        return new FspLocation(location);
    }


}
