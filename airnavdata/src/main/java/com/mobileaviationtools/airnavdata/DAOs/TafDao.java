package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

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
}
