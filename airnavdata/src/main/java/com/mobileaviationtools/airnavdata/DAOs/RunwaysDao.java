package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.ArrayList;
import java.util.List;

import static android.icu.text.MessagePattern.ArgType.SELECT;

@Dao
public abstract class RunwaysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRunways(List<Runway> runways);

    @Transaction
    public void insertRunwaysTransaction(List<Runway> runways)
    {
        insertRunways(runways);
    }

    @Insert
    public abstract void insertRunway(Runway runway);

    @Query("SELECT * FROM tbl_Runways WHERE airport_ref=:airport_ref")
    public abstract List<Runway> getRunwaysByAirport(Integer airport_ref);

    @Query("SELECT * FROM tbl_Runways WHERE id=:id")
    public abstract Runway getRunwayById(Integer id);
 }
