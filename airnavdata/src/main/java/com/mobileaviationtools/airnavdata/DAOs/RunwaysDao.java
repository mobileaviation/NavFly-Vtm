package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.List;

import static android.icu.text.MessagePattern.ArgType.SELECT;

@Dao
public interface RunwaysDao {
    @Insert
    public void insertRunways(List<Runway> airportList);

    @Insert
    public void insertRunway(Runway runway);

    @Query("SELECT * FROM tbl_Runways WHERE airport_ref=:airport_ref")
    public Runway[] getRunwaysByAirport(Integer airport_ref);
 }
