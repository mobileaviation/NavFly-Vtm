package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.ATCStation;

import java.util.List;

@Dao
public abstract class ATCStationsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertATCStations(List<ATCStation> atcStationsList);

    @Insert
    public abstract void insertATCStation(ATCStation atcStation);

    @Transaction
    public void insertATCStationsTransaction(List<ATCStation> atcStationsList)
    {
        insertATCStationsTransaction(atcStationsList);
    }

    @Query("SELECT * FROM tbl_ATCStations WHERE airspace_id=:airspace_id")
    public abstract List<ATCStation> getATCStationByAirspaceId(Integer airspace_id);
}
