package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Metar;
import com.mobileaviationtools.airnavdata.Entities.Taf;

import java.util.List;

@Dao
public abstract class TafDao {
    @Insert
    public abstract void InsertTaf(Taf taf);

    @Insert
    public abstract void InsertTafs(List<Taf> tafs);

    @Transaction
    public void InsertTafsTransaction(List<Taf> tafs)
    {
        InsertTafs(tafs);
    }
    @Query("SELECT * FROM tbl_Tafs WHERE station_id=:station_id")
    public abstract Taf[] getTafsByStationId(String station_id);

    @Query("SELECT * FROM tbl_Tafs WHERE (longitude BETWEEN :Wlon AND :Elon AND latitude BETWEEN :Slat AND :Nlat) " +
            " AND id in (SELECT MAX(id) FROM tbl_Metars GROUP BY station_id)" +
            "ORDER BY id DESC")
    public abstract List<Taf> getTafsWithinBounds(double Wlon, double Elon, double Nlat, double Slat);

}
