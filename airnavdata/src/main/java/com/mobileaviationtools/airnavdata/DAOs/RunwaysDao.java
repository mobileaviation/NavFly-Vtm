package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.List;

@Dao
public interface RunwaysDao {
    @Insert
    public void insertRunways(List<Runway> airportList);
}
