package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Waypoint;

@Dao
public abstract class WaypointDao {
    @Insert
    public abstract long InsertWaypoint(Waypoint waypoint);

    @Query("SELECT * FROM tbl_Waypoint WHERE route_id=:route_id")
    public abstract Waypoint[] GetWaypointsByRouteID(Long route_id);

    @Query("DELETE FROM tbl_Waypoint WHERE route_id=:route_id")
    public abstract void DeleteWaypointsByRouteID(Long route_id);
}
