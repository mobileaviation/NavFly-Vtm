package com.mobileaviationtools.weater_notam_data.services;

import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

import org.locationtech.jts.geom.Geometry;
import org.oscim.core.GeoPoint;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface WeatherResponseEvent {
    void OnMetarsResponse(List<Metar> metars, GeoPoint location, String message);
    void OnTafsResponse(List<Taf> tafs, GeoPoint location, String message);
    void OnFailure(String message, GeoPoint position, Long distance, Geometry route);
}
