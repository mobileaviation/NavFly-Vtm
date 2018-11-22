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
    private IFspLocationProvider locationProvider;

    public Boolean Start(LocationEvents locationEvents)
    {
        LocationProviderType type = retrieveType();

        this.locationEvents = locationEvents;
        //this.type = type;

        switch (type)
        {
            case gps:{
                locationProvider = new FspGPSLocationProvider(context);
                break;
            }
            case simulator:{
                locationProvider = new FspSimLocationProvider(context);
                break;
            }
            case playback:{
                break;
            }
        }

        if (locationProvider.setup())
            return (locationProvider != null) ? locationProvider.start(locationEvents) : false;
        else return false;
    }

    public Boolean Stop()
    {
        return locationProvider.stop();
    }

    private LocationProviderType retrieveType()
    {
        //TODO Build database logic to retrieve the connection type..
        return LocationProviderType.simulator;
        //return LocationProviderType.gps;
    }

}
