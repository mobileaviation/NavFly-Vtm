package com.mobileaviationtools.nav_fly.Classes;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Location.FspLocation;

import java.util.LinkedList;

public class FspLocationAnalysis extends LinkedList<FspLocation> {
    public enum AnalyzedType
    {
        on_ground,                  // Get ground level from airport, over 10 items remains near groundlevel, does not change (within ?)
        takeoff,                    // From "on_ground", calculate vertical speed > 500 fpm
        climbing,                  // for any level calculate vertical speed > 500 fpm
        cruise,                     // from "climbing or decent not landing " any level (not on_ground) steady (within ??)
        to_left,
        to_right,
        decent,                     // from "cruise" any level vertical speed < -500 fpm
        landing                     // from "decent" to ground level (within?)
    }

    public FspLocationAnalysis(Airport airport)
    {
        this.airport = airport;
    }

    private final Integer ELEMENTS_COUNT = 10;
    private Integer elements_count;
    private Airport airport;

    @Override
    public void addFirst(FspLocation location) {
        super.addFirst(location);
        if (elements_count == ELEMENTS_COUNT)
            removeLast();
        else elements_count++;
    }
}
