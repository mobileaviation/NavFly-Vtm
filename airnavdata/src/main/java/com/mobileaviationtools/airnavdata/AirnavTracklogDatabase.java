package com.mobileaviationtools.airnavdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mobileaviationtools.airnavdata.DAOs.TrackLogDao;
import com.mobileaviationtools.airnavdata.DAOs.TrackLogItemDao;
import com.mobileaviationtools.airnavdata.Entities.TrackLog;
import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;

@Database(entities = {TrackLog.class, TrackLogItem.class}, version = 1)
public abstract class AirnavTracklogDatabase extends RoomDatabase {
    public static  final String DB_NAME = "room_airnav_tracklog.db";
    private static volatile AirnavTracklogDatabase instance;

    public static synchronized AirnavTracklogDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AirnavTracklogDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                AirnavTracklogDatabase.class,
                DB_NAME
        ).allowMainThreadQueries().build();
    }

    public abstract TrackLogDao getTrackLog();
    public abstract TrackLogItemDao getTracklogItems();
}
