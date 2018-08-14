package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.mobileaviationtools.airnavdata.Entities.Frequency;

import java.util.List;

@Dao
public interface FrequenciesDao {
    @Insert
    public void insertFrequencies(List<Frequency> airportList);

    @Insert
    public void insertFrequency(Frequency frequency);
}
