package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import java.util.List;

@Dao
public interface AirportsDao {
    @Insert
    public void insertAirports(List<Airport> airportList);

    @Insert
    public void insertAirport(Airport airport);
}
