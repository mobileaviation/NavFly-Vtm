package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

import java.util.List;

@Dao
public abstract class AirspacesDao {
    @Insert
    public abstract void insertAirspaces(List<Airspace> airspaceList);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAirspace(Airspace airspace);

    @Transaction
    public void insertAirspaceTransaction(List<Airspace> airspaceList)
    {
        insertAirspaces(airspaceList);
    }

    @Query("SELECT * FROM tbl_Airspaces WHERE country==:country")
    public abstract List<Airspace> getAirspacesByCountry(String country);

    @Query("SELECT * FROM tbl_Airspaces A " +
            "WHERE ((A.lat_top_left>=:min_lat AND A.lat_top_left<=:max_lat AND A.lon_top_left>=:min_lon AND A.lon_top_left<=:max_lon) " +
            "OR (A.lat_bottom_right>=:min_lat AND A.lat_bottom_right<=:max_lat AND A.lot_bottom_right>=:min_lon AND A.lot_bottom_right<=:max_lon))")
    public abstract Airspace[] getAirspacesByPosition(double min_lon, double max_lon, double min_lat, double max_lat);

    @Query("SELECT * FROM tbl_Airspaces A " +
            "WHERE ((A.lat_top_left>=:min_lat AND A.lat_top_left<=:max_lat AND A.lon_top_left>=:min_lon AND A.lon_top_left<=:max_lon) " +
            "OR (A.lat_bottom_right>=:min_lat AND A.lat_bottom_right<=:max_lat AND A.lot_bottom_right>=:min_lon AND A.lot_bottom_right<=:max_lon)) " +
            "AND category = :category")
    public abstract Airspace[] getAirspacesByPositionAndCategory(double min_lon, double max_lon, double min_lat, double max_lat, String category);

    @Query("SELECT * FROM tbl_Airspaces A " +
            "WHERE ((A.lat_top_left>=:min_lat AND A.lat_top_left<=:max_lat AND A.lon_top_left>=:min_lon AND A.lon_top_left<=:max_lon) " +
            "OR (A.lat_bottom_right>=:min_lat AND A.lat_bottom_right<=:max_lat AND A.lot_bottom_right>=:min_lon AND A.lot_bottom_right<=:max_lon)) " +
            "AND country = :country")
    public abstract Airspace[] getAirspacesByPositionAndCountry(double min_lon, double max_lon, double min_lat, double max_lat, String country);

    @Query("SELECT * FROM  tbl_Airspaces A WHERE "
            + "(:lat<A.lat_top_left AND :lat>A.lat_bottom_right AND :lon>A.lon_top_left AND :lon<A.lot_bottom_right)")
    public abstract Airspace[] getAirspacesSurroundedBy(double lat, double lon);
}
