package com.mobileaviationtools.nav_fly.Location;

import android.location.Location;

public interface LocationEvents {
    public void OnLocationChanged(LocationProviderType type, FspLocation location, String message, Boolean success);
}
