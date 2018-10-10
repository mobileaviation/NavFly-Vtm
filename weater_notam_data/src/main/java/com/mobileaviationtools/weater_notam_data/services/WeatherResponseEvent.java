package com.mobileaviationtools.weater_notam_data.services;

import com.mobileaviationtools.weater_notam_data.weather.Metar;

import java.lang.reflect.Array;
import java.util.ArrayList;

public interface WeatherResponseEvent {
    void OnMetarsResponse(ArrayList<Metar> metars, String message);
    void OnTafsResponse();
    void OnFailure(String message);
}
