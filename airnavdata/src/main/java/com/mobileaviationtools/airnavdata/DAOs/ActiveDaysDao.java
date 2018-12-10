package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.ActiveDay;

import java.util.List;

@Dao
public abstract class ActiveDaysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertActiveDays(List<ActiveDay> activeDaysList);

    @Insert
    public abstract void insertActiveDay(ActiveDay activeDay);

    @Transaction
    public void insertActiveDaysTransaction(List<ActiveDay> activeDaysList)
    {
        insertActiveDays(activeDaysList);
    }

    @Query("SELECT * FROM tbl_ActiveDays WHERE airspace_id=:airspace_id")
    public abstract List<ActiveDay> getActiveDaysByAirspaceId(Integer airspace_id);
}
