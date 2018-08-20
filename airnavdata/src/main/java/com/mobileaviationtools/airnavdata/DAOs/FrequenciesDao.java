package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.List;

@Dao
public interface FrequenciesDao {
    @Insert
    public void insertFrequencies(List<Frequency> airportList);

    @Insert
    public void insertFrequency(Frequency frequency);

    @Query("SELECT * FROM tbl_Frequencies WHERE airport_ref=:airport_ref")
    public List<Frequency> getFrequenciesByAirport(Integer airport_ref);
}
