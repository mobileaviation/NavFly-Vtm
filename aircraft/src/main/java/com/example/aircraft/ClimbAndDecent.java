package com.example.aircraft;

import org.oscim.core.GeoPoint;

public class ClimbAndDecent {
    private final double meterToNM = 0.000539956803;

    public double ClimbValuePerDistance(Aircraft aircraft, double distance)
    {
        // Use Vx as the climb speed
        // Calculater the time needed to travel "distance"(NM)
        // multiply "time"(minutes) with the climbrate of the aircraft

        double distanceNM = meterToNM * distance;
        double minutesTraveled = (distanceNM / aircraft.Vx()) * 60;
        double climbedHeightFeet = minutesTraveled * aircraft.climbRate();

        return climbedHeightFeet;
    }
}
