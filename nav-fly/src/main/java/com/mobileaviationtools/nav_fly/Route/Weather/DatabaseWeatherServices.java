package com.mobileaviationtools.nav_fly.Route.Weather;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavAirportInfoDatabase;
import com.mobileaviationtools.airnavdata.Entities.Metar;
import com.mobileaviationtools.airnavdata.Entities.Taf;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.Classes.MapperHelper;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

import static org.locationtech.jts.operation.buffer.BufferParameters.CAP_ROUND;

public class DatabaseWeatherServices {
    public DatabaseWeatherServices(Context context)
    {
        db = AirnavAirportInfoDatabase.getInstance(context);
    }

    private WeatherStations.WeatherDataReceivedEvent weatherDataReceivedEvent;

    private List<Taf> getTafsByEnvelope(Geometry envelope)
    {
        List<Taf> wTafs = null;
        if (envelope.getNumPoints()>3) {
            Coordinate[] coordinates = envelope.getCoordinates();
            wTafs = db.getTaf().getTafsWithinBounds(coordinates[0].x,
                    coordinates[2].x,
                    coordinates[2].y,
                    coordinates[0].y);
        }

        return wTafs;
    }

    public void GetTafsByLocationAndRadius(GeoPoint position, Long distance,
                                           WeatherResponseEvent weatherDataResponseEvent)
    {
        // TODO Implement TAF from database retrieval
        Long m = distance * 1609;
        Geometry c = GeometryHelpers.getCircle(position, m);
        Geometry b = c.getEnvelope();
        List<com.mobileaviationtools.weater_notam_data.weather.Taf> wTafs = new ArrayList<>();
        List<Taf> tafs = getTafsByEnvelope(c);
        if (tafs != null)
        {
            for (Taf me : tafs)
            {
                wTafs.add(MapperHelper.getTaf(me));
            }

            if (weatherDataResponseEvent != null) weatherDataResponseEvent.OnTafsResponse(wTafs, position, "Received DatabaseTafs");
        }
    }

    public void GetTafsByRoute(Geometry route, WeatherResponseEvent weatherDataResponseEvent)
    {
        Geometry b = route.buffer(0.50d, 20, CAP_ROUND).getEnvelope();
        List<com.mobileaviationtools.weater_notam_data.weather.Taf> wTafs = new ArrayList<>();
        List<Taf> tafs = getTafsByEnvelope(b);
        if (tafs != null)
        {
            for (Taf me : tafs)
            {
                if (b.contains(new GeometryFactory().createPoint(new Coordinate(me.longitude, me.latitude))))
                    wTafs.add(MapperHelper.getTaf(me));
            }

            if (weatherDataResponseEvent != null) weatherDataResponseEvent.OnTafsResponse(wTafs, null, "Received DatabaseTafs");
        }
    }

    private List<Metar> getMetarsByEnvelope(Geometry envelope)
    {
        List<Metar> wMetars = null;
        if (envelope.getNumPoints()>3) {
            Coordinate[] coordinates = envelope.getCoordinates();
            wMetars = db.getMetar().getMetarsWithinBounds(coordinates[0].x,
                    coordinates[2].x,
                    coordinates[2].y,
                    coordinates[0].y);
        }

        return wMetars;
    }

    public void GetMetarsByLocationAndRadius(GeoPoint position, Long distance,
                                             WeatherResponseEvent weatherDataResponseEvent)
    {
        Long m = distance * 1609;
        Geometry c = GeometryHelpers.getCircle(position, m);
        Geometry b = c.getEnvelope();
        List<com.mobileaviationtools.weater_notam_data.weather.Metar> wMetars = new ArrayList<>();
        List<Metar> metars = getMetarsByEnvelope(b);
        if (metars != null)
        {
            for (Metar me : metars)
            {
                me.distance_to_org_m = position.sphericalDistance(new GeoPoint(me.latitude, me.longitude));
                wMetars.add(MapperHelper.getMetar(me));
            }

            if (weatherDataResponseEvent != null) weatherDataResponseEvent.OnMetarsResponse(wMetars, position, "Received DatabaseMetars");
        }
    }

    public void GetMetarsByRoute(Geometry route, WeatherResponseEvent weatherDataResponseEvent)
    {
        Geometry b = route.buffer(0.50d, 20, CAP_ROUND).getEnvelope();
        List<com.mobileaviationtools.weater_notam_data.weather.Metar> wMetars = new ArrayList<>();
        List<Metar> metars = getMetarsByEnvelope(b);
        if (metars != null)
        {
            for (Metar me : metars)
            {
                if (b.contains(new GeometryFactory().createPoint(new Coordinate(me.longitude, me.latitude)))) {
                    me.distance_to_org_m = -1d;
                    wMetars.add(MapperHelper.getMetar(me));
                }
            }

            if (weatherDataResponseEvent != null) weatherDataResponseEvent.OnMetarsResponse(wMetars, null, "Received DatabaseMetars");
        }
    }


    private AirnavAirportInfoDatabase db;
}
