package com.mobileaviationtools.weater_notam_data.services;

import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public interface WeatherResponseEvent {
    void OnMetarsResponse(List<Metar> metars, String message);
    void OnTafsResponse(List<Taf> tafs, String message);
    void OnFailure(String message);
}
