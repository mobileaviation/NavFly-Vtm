package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavAirportInfoDatabase;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.MapperHelper;
import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

public class Station {
    public Station(Context context)
    {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
        a_db = AirnavAirportInfoDatabase.getInstance(context);
    }
    private Context context;
    private AirnavDatabase db;
    private AirnavAirportInfoDatabase a_db;

    public void setStation_id(String station_id)
    {
        this.station_id = station_id;
        airport = db.getAirport().getAirportByIdent(station_id);
        if (airport != null)
            airport.runways = db.getRunways().getRunwaysByAirport(airport.id);
    }
    public String station_id;
    public Metar metar;

    public void setMetar(Metar metar) {
        this.metar = metar;
        if (metar != null) {
            com.mobileaviationtools.airnavdata.Entities.Metar
                    db_metar = MapperHelper.getMetarEntity(metar, airport);
            a_db.getMetar().InsertMetar(db_metar);
        }
    }

    public Taf taf;

    public void setTaf(Taf taf) {
        this.taf = taf;
        if (taf != null) {
            com.mobileaviationtools.airnavdata.Entities.Taf
                    db_taf = MapperHelper.getTafEntity(taf, airport);
            a_db.getTaf().InsertTaf(db_taf);
        }
    }

    public Airport airport;
}
