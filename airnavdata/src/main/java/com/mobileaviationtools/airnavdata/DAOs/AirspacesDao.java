package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

import java.util.List;

@Dao
public abstract class AirspacesDao {
    @Insert
    public abstract void insertAirspaces(List<Airspace> airspaceList);

    @Insert
    public abstract void insertAirspace(Airspace airspace);

    @Transaction
    public void insertAirspaceTransaction(List<Airspace> airspaceList)
    {
        insertAirspaces(airspaceList);
    }

    @Query("SELECT * FROM tbl_Airspaces WHERE country==:country")
    public abstract List<Airspace> getAirspacesByCountry(String country);
}
