package com.mobileaviationtools.airnavdata;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.mobileaviationtools.airnavdata.DAOs.RouteDao;
import com.mobileaviationtools.airnavdata.DAOs.WaypointDao;
import com.mobileaviationtools.airnavdata.Entities.Route;
import com.mobileaviationtools.airnavdata.Entities.Waypoint;

@Database(entities = {Route.class, Waypoint.class}, version = 1)
public abstract class AirnavRouteDatabase extends RoomDatabase {
    public static  final String DB_NAME = "room_airnav_route.db";
    private static volatile AirnavRouteDatabase instance;

    public static synchronized AirnavRouteDatabase getInstance(Context context)
    {
        if (instance == null)
            instance = create(context);
        return instance;
    }

    private static AirnavRouteDatabase create(final Context context)
    {
        return Room.databaseBuilder(
                context,
                AirnavRouteDatabase.class,
                DB_NAME
        ).allowMainThreadQueries().build();
    }

    public abstract RouteDao getRoute();
    public abstract WaypointDao getWaypoint();
}
