package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.ActiveDay;
import com.mobileaviationtools.airnavdata.Entities.ActivePeriod;

import java.util.List;

@Dao
public abstract class ActivePeriodsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertActivePeriods(List<ActivePeriod> activePeriodsList);
    @Insert
    public abstract void insertActivePeriods(ActivePeriod activePeriod);
    @Transaction
    public void insertActivePeriodsTransaction(List<ActivePeriod> activePeriodsList)
    {
        insertActivePeriods(activePeriodsList);
    }

    @Query("SELECT * FROM tbl_ActivePeriods WHERE airspace_id=:airspace_id")
    public abstract List<ActivePeriod> getActivePeriodsByAirspaceId(Integer airspace_id);
}
