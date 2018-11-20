package com.mobileaviationtools.nav_fly.Location;

import android.location.Location;

public class FspLocation extends Location {
    public FspLocation(String provider) {
        super(provider);
    }

    public FspLocation(Location l) {
        super(l);
    }

    public static FspLocation getInstance(Location location)
    {
        return new FspLocation(location);
    }


}
