package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobileaviationtools.airnavdata.Entities.Route;

import java.util.List;

@Dao
public abstract class RouteDao {
    @Insert
    public abstract long InsertRoute(Route route);

    @Query("UPDATE tbl_Route set modifiedDate=:modifiedDate WHERE id=:id")
    public abstract void UpdateRoute(Long id, Long modifiedDate);

    @Query("UPDATE tbl_Route SET elevation_json=:elevation_json WHERE id=:id")
    public abstract void UpdateRouteElevationJson(String elevation_json, Long id);

    @Query("SELECT * FROM tbl_Route WHERE id=:id")
    public abstract Route getRouteById(Long id);

    @Query("SELECT * FROM tbl_Route ORDER BY id DESC")
    public abstract List<Route> getAllRoutes();

    @Query("SELECT * FROM tbl_Route WHERE name=:name")
    public abstract Route getRouteByName(String name);

    @Delete
    public abstract void DeleteRoute(Route route);
}
