package com.mobileaviationtools.nav_fly.Settings.HomeAirport;

import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.AirnavUserSettingsDatabase;
import com.mobileaviationtools.airnavdata.Classes.PropertiesGroup;
import com.mobileaviationtools.airnavdata.Classes.PropertiesName;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Property;
import com.mobileaviationtools.nav_fly.GlobalVars;

public class HomeAirportService {
    public HomeAirportService(GlobalVars vars)
    {
        this.vars = vars;
    }

    private GlobalVars vars;
    private final String TAG = "HomeAirportService";

    public void storeAirport(SelectedAirport airport)
    {
        AirnavUserSettingsDatabase db = AirnavUserSettingsDatabase.getInstance(vars.context);
        Property p = db.getProperties().getPropertyByGroupAndName(PropertiesGroup.home_location.toString(),
                PropertiesName.home_airport.toString());

        boolean insert = false;
        if (p == null) {
            p = new Property();
            p.groupname = PropertiesGroup.home_location;
            p.name = PropertiesName.home_airport;
            insert = true;
        }

        p.value1 = airport.airport.ident;
        if (airport.runway != null )
        {
            p.value2 = airport.runway.id.toString();
            p.value3 = airport.runwayIdent;
        }
        else {
            p.value2 = "";
            p.value3 = "";
        }

        if(insert) db.getProperties().InsertProperty(p);
        else db.getProperties().UpdateProperty(p);
    }

    public SelectedAirport getSelectedHomeAirport()
    {
        Log.i(TAG, "Start retrieving selected home airport");

        AirnavUserSettingsDatabase db = AirnavUserSettingsDatabase.getInstance(vars.context);
        SelectedAirport airport = null;
        Property p = db.getProperties().getPropertyByGroupAndName(PropertiesGroup.home_location.toString(),
                PropertiesName.home_airport.toString());
        if (p != null)
        {
            AirnavDatabase adb = AirnavDatabase.getInstance(vars.context);
            Airport a = adb.getAirport().getAirportByIdent(p.value1);
            a.runways = adb.getRunways().getRunwaysByAirport(a.id);
            airport = new SelectedAirport();
            airport.airport = a;
            Log.i(TAG, "Found Airport: " + a.name);
            if (p.value2 != null)
            {
                if (p.value2.length()>0)
                {
                    Integer r_id = Integer.parseInt(p.value2);
                    airport.runway = adb.getRunways().getRunwayById(r_id);
                    airport.runwayIdent = p.value3;

                    Log.i(TAG, "Found Runway: " + airport.runwayIdent);
                }
            }
        }

        return airport;
    }
}
