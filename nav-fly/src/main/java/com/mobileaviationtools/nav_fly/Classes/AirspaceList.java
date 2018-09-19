package com.mobileaviationtools.nav_fly.Classes;

import com.mobileaviationtools.airnavdata.Entities.Airspace;

import java.util.ArrayList;
import java.util.List;

public class AirspaceList extends ArrayList<Airspace> {
    public Airspace GetAirspace(Airspace airspace)
    {
        return get(indexOf(airspace));
    }
}
