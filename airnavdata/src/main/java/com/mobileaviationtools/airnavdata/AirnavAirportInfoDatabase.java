package com.mobileaviationtools.airnavdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mobileaviationtools.airnavdata.DAOs.MetarDao;
import com.mobileaviationtools.airnavdata.DAOs.NotamDao;
import com.mobileaviationtools.airnavdata.DAOs.TafDao;
import com.mobileaviationtools.airnavdata.Entities.Metar;
import com.mobileaviationtools.airnavdata.Entities.Notam;
import com.mobileaviationtools.airnavdata.Entities.Taf;

@Database(entities = {Notam.class, Taf.class, Metar.class},
        version = 1)
public abstract class AirnavAirportInfoDatabase extends RoomDatabase{
    public static final String DB_NAME = "room_airnav_airport_info.db";
    private static volatile AirnavAirportInfoDatabase instance;

    public static synchronized AirnavAirportInfoDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AirnavAirportInfoDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                AirnavAirportInfoDatabase.class,
                DB_NAME
        ).allowMainThreadQueries().build();
    }

    public abstract MetarDao getMetar();
    public abstract TafDao getTaf();
    public abstract NotamDao getNotam();
}
