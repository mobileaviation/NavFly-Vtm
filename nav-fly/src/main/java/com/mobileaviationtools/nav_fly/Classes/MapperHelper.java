package com.mobileaviationtools.nav_fly.Classes;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Metar;
import com.mobileaviationtools.airnavdata.Entities.Notam;
import com.mobileaviationtools.airnavdata.Entities.Taf;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.modelmapper.ModelMapper;
import org.modelmapper.config.Configuration;
import org.modelmapper.convention.MatchingStrategies;

import java.util.Date;

public class MapperHelper {

    public static Notam getNotamEntity(com.mobileaviationtools.weater_notam_data.notams.Notam notam, Airport airport)
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        Notam res_notam = modelMapper.map(notam, Notam.class);
        res_notam.loadedDate = new Date().getTime();
        WKTReader reader = new WKTReader();
        try {
            Geometry m = reader.read(notam.mapPointer);
            res_notam.longitude = (float)m.getCoordinates()[0].x;
            res_notam.latitude = (float)m.getCoordinates()[0].y;
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (airport != null) res_notam.airport_ref = airport.id;
        return res_notam;
    }

    public static Metar getMetarEntity(com.mobileaviationtools.weater_notam_data.weather.Metar metar, Airport airport)
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        Metar res_metar = modelMapper.map(metar, Metar.class);
        res_metar.loadedDate = new Date().getTime();

        if (airport != null) res_metar.airport_ref = airport.id;
        return res_metar;
    }

    public static Taf getTafEntity(com.mobileaviationtools.weater_notam_data.weather.Taf taf, Airport airport)
    {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);
        Taf res_taf = modelMapper.map(taf, Taf.class);
        res_taf.loadedDate = new Date().getTime();

        if (airport != null) res_taf.airport_ref = airport.id;
        return res_taf;
    }

    public static Airport getAirport(String ident, Context context)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(context);
        return db.getAirport().getAirportByIdent(ident);
    }

}
