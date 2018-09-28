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
import com.mobileaviationtools.airnavdata.DAOs.ATCStationsDao;
import com.mobileaviationtools.airnavdata.DAOs.ActiveDaysDao;
import com.mobileaviationtools.airnavdata.DAOs.ActivePeriodsDao;
import com.mobileaviationtools.airnavdata.DAOs.AirportsDao;
import com.mobileaviationtools.airnavdata.DAOs.AirspacesDao;
import com.mobileaviationtools.airnavdata.DAOs.CountriesDao;
import com.mobileaviationtools.airnavdata.DAOs.FirsDao;
import com.mobileaviationtools.airnavdata.DAOs.FixesDao;
import com.mobileaviationtools.airnavdata.DAOs.FrequenciesDao;
import com.mobileaviationtools.airnavdata.DAOs.MBTilesDao;
import com.mobileaviationtools.airnavdata.DAOs.NavaidsDao;
import com.mobileaviationtools.airnavdata.DAOs.RegionsDao;
import com.mobileaviationtools.airnavdata.DAOs.RunwaysDao;
import com.mobileaviationtools.airnavdata.Entities.ATCStation;
import com.mobileaviationtools.airnavdata.Entities.ActiveDay;
import com.mobileaviationtools.airnavdata.Entities.ActivePeriod;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.airnavdata.Entities.Country;
import com.mobileaviationtools.airnavdata.Entities.Fir;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.MBTile;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.airnavdata.Entities.Region;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.io.FileReader;

@Database(entities = {Airport.class, Runway.class, Frequency.class, Navaid.class,
        Country.class, Region.class, Fix.class, Fir.class, Airspace.class, MBTile.class,
        ActivePeriod.class, ActiveDay.class, ATCStation.class},
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
    public abstract CountriesDao getCountries();
    public abstract RegionsDao getRegions();
    public abstract FirsDao getFirs();
    public abstract FixesDao getFixes();
    public abstract AirspacesDao getAirpaces();
    public abstract MBTilesDao getTiles();
    public abstract ATCStationsDao getAtcStations();
    public abstract ActiveDaysDao getActiveDays();
    public abstract ActivePeriodsDao getActivePeriods();

}
