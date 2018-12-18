package com.mobileaviationtools.airnavdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mobileaviationtools.airnavdata.DAOs.DatabaseDao;
import com.mobileaviationtools.airnavdata.DAOs.PropertiesDao;
import com.mobileaviationtools.airnavdata.Entities.Property;

@Database(entities = {com.mobileaviationtools.airnavdata.Entities.Database.class, Property.class}, version = 1)
public abstract class AirnavUserSettingsDatabase extends RoomDatabase {

    public static  final String DB_NAME = "room_airnav_settings.db";
    private static volatile AirnavUserSettingsDatabase instance;

    public static synchronized AirnavUserSettingsDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AirnavUserSettingsDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                AirnavUserSettingsDatabase.class,
                DB_NAME
        ).allowMainThreadQueries().build();
    }

    public abstract DatabaseDao getDatabase();
    public abstract PropertiesDao getProperties();
}
