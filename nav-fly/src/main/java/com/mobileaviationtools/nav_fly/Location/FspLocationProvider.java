package com.mobileaviationtools.nav_fly.Location;

import android.content.Context;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Settings.Services.LocationProviderService;

import org.modelmapper.internal.bytebuddy.matcher.CollectionOneToOneMatcher;

public class FspLocationProvider {
    public FspLocationProvider(GlobalVars vars)
    {
        this.vars = vars;
    }

    private GlobalVars vars;

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
                locationProvider = new FspGPSLocationProvider(vars.context);
                break;
            }
            case simulator:{
                locationProvider = new FspSimLocationProvider(vars);
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
        LocationProviderService service = new LocationProviderService(vars);

        //return LocationProviderType.simulator;
        return service.getLocationProviderType();
    }

}
