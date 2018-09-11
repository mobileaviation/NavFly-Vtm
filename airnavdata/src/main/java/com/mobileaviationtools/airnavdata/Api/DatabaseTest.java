package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

import java.util.ArrayList;
import java.util.List;

public class DatabaseTest {
    public void Test(Context context)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(context);
        List<Airspace> airspaces = db.getAirpaces().getAirspacesByCountry("Netherlands");
        int i = 0;
    }
}
