package com.mobileaviationtools.airnavdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mobileaviationtools.airnavdata.DAOs.ChartDao;
import com.mobileaviationtools.airnavdata.DAOs.MBTilesDao;
import com.mobileaviationtools.airnavdata.DAOs.OnlineTileProvidersDao;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.MBTile;
import com.mobileaviationtools.airnavdata.Entities.OnlineTileProvider;
import com.mobileaviationtools.airnavdata.Entities.Route;
import com.mobileaviationtools.airnavdata.Entities.Waypoint;

@Database(entities = {Chart.class, OnlineTileProvider.class}, version = 1)
public abstract class AirnavChartsDatabase extends RoomDatabase {
    public static  final String DB_NAME = "room_airnav_chart.db";
    private static volatile AirnavChartsDatabase instance;

    public static synchronized AirnavChartsDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AirnavChartsDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                AirnavChartsDatabase.class,
                DB_NAME
        ).allowMainThreadQueries().build();
    }

    public abstract ChartDao getCharts();
    public abstract OnlineTileProvidersDao getOnlineTileProviders();
}
