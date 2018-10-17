package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Route;

@Dao
public abstract class RouteDao {
    @Insert
    public abstract long InsertRoute(Route route);

    @Query("SELECT * FROM tbl_Route")
    public abstract Route[] getAllRoutes();
}
