package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Metar;

import java.util.List;

@Dao
public abstract class MetarDao {
    @Insert
    public abstract void InsertMetar(Metar metar);

    @Insert
    public abstract void InsertMetars(List<Metar> metars);

    @Transaction
    public void InsertMetarsTransaction(List<Metar> metars)
    {
        InsertMetars(metars);
    }

    @Query("SELECT * FROM tbl_Metars WHERE station_id=:station_id")
    public abstract Metar[] getMetarsByStationId(String station_id);

    @Query("SELECT * FROM tbl_Metars WHERE (longitude BETWEEN :Wlon AND :Elon AND latitude BETWEEN :Slat AND :Nlat) " +
            " AND id in (SELECT MAX(id) FROM tbl_Metars GROUP BY station_id)" +
            "ORDER BY id DESC")
    public abstract List<Metar> getMetarsWithinBounds(double Wlon, double Elon, double Nlat, double Slat);
}
