package com.mobileaviationtools.nav_fly.Location;

import android.content.Context;

import org.modelmapper.internal.bytebuddy.matcher.CollectionOneToOneMatcher;

public class FspLocationProvider {
    public FspLocationProvider(Context context)
    {
        this.context = context;
    }

    private Context context;

    private LocationEvents locationEvents;
    private LocationProviderType type;
    public Boolean Start(LocationEvents locationEvents, LocationProviderType type)
    {
        this.locationEvents = locationEvents;
        this.type = type;

        switch (type)
        {
            case gps:{
                FspGPSLocationProvider gps = new FspGPSLocationProvider(context);
                if (gps.setupGps())
                {
                    gps.SetLocationEvents(locationEvents);
                    return true;
                }
                else return false;
            }
            case simulator:{
                break;
            }
            case playback:{
                break;
            }
        }

        return false;
    }

}
