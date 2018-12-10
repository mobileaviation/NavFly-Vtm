package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.List;

@Dao
public abstract class FrequenciesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertFrequencies(List<Frequency> frequencies);

    @Transaction
    public void insertFrequenciesTransaction(List<Frequency> frequencies)
    {
        insertFrequencies(frequencies);
    }

    @Insert
    public abstract void insertFrequency(Frequency frequency);

    @Query("SELECT * FROM tbl_Frequencies WHERE airport_ref=:airport_ref")
    public abstract List<Frequency> getFrequenciesByAirport(Integer airport_ref);
}
