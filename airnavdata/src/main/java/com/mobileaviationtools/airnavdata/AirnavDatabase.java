package com.mobileaviationtools.airnavdata;

import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverter;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.support.annotation.NonNull;

import com.mobileaviationtools.airnavdata.Classes.AirportTypeConverter;
import com.mobileaviationtools.airnavdata.DAOs.AirportsDao;
import com.mobileaviationtools.airnavdata.DAOs.FrequenciesDao;
import com.mobileaviationtools.airnavdata.DAOs.NavaidsDao;
import com.mobileaviationtools.airnavdata.DAOs.RunwaysDao;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.io.FileReader;

@Database(entities = {Airport.class, Runway.class, Frequency.class, Navaid.class},
        version = 1)
public abstract class AirnavDatabase extends RoomDatabase {
    private static  final String DB_NAME = "room_airnav.db";
    private static volatile AirnavDatabase instance;

    public static synchronized AirnavDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AirnavDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                AirnavDatabase.class,
                DB_NAME
        ).allowMainThreadQueries().build();
    }

    public abstract AirportsDao getAirport();
    public abstract RunwaysDao getRunways();
    public abstract FrequenciesDao getFrequency();
    public abstract NavaidsDao getNavaids();

}
