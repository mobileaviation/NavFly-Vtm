package com.mobileaviationtools.nav_fly.Location;

import android.content.Context;

public class FspSimLocationProvider {
    public FspSimLocationProvider(Context context)
    {
        this.context = context;
    }

    private Context context;

    private LocationEvents locationEvents;
    public void SetLocationEvents(LocationEvents locationEvents)
    {
        this.locationEvents = locationEvents;
    }
}
