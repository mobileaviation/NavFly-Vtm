package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavAirportInfoDatabase;
import com.mobileaviationtools.airnavdata.Entities.Metar;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.Classes.MapperHelper;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

public class DatabaseWeatherServices {
    public DatabaseWeatherServices(Context context)
    {
        db = AirnavAirportInfoDatabase.getInstance(context);
    }

    private WeatherStations.WeatherDataReceivedEvent weatherDataReceivedEvent;

    public void GetTafsByLocationAndRadius(GeoPoint position, Long distance,
                                           WeatherResponseEvent weatherDataResponseEvent)
    {

    }

    public void GetMetarsByLocationAndRadius(GeoPoint position, Long distance,
                                             WeatherResponseEvent weatherDataResponseEvent)
    {
        Long m = distance * 1609;
        Geometry c = GeometryHelpers.getCircle(position, m);
        Geometry b = c.getEnvelope();
        List<com.mobileaviationtools.weater_notam_data.weather.Metar> wMetars = new ArrayList<>();
        if (b.getNumPoints()>3)
        {
            Coordinate[] coordinates = b.getCoordinates();
            List<Metar> metars = db.getMetar().getMetarsWithinBounds(coordinates[0].x,
                    coordinates[2].x,
                    coordinates[2].y,
                    coordinates[0].y);

            for (Metar me : metars)
            {
                me.distance_to_org_m = position.sphericalDistance(new GeoPoint(me.latitude, me.longitude));
                wMetars.add(MapperHelper.getMetar(me));
            }

            if (weatherDataResponseEvent != null) weatherDataResponseEvent.OnMetarsResponse(wMetars, position, "Received DatabaseMetars");
        }
    }


    private AirnavAirportInfoDatabase db;
}
