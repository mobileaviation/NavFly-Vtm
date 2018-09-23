package com.mobileaviationtools.nav_fly.Markers.Airport;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.core.GeoPoint;

public interface AirportSelected {
    public void Selected(Airport airport, GeoPoint geoPoint);
}
