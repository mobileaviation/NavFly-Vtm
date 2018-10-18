package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Route;

import java.util.List;

@Dao
public abstract class RouteDao {
    @Insert
    public abstract long InsertRoute(Route route);

    @Query("SELECT * FROM tbl_Route ORDER BY id")
    public abstract List<Route> getAllRoutes();

    @Query("SELECT * FROM tbl_Route WHERE name=:name")
    public abstract Route getRouteByName(String name);

    @Delete
    public abstract void DeleteRoute(Route route);
}
