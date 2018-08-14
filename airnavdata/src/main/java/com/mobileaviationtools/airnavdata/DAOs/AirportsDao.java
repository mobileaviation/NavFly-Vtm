package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import java.util.List;

@Dao
public interface AirportsDao {
    @Insert
    public void insertAirports(List<Airport> airportList);

    @Insert
    public void insertAirport(Airport airport);

    @Query("SELECT * FROM tbl_Airports WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat)")
    public Airport[] getAirportsWithinBounds(double Wlon, double Elon, double Nlat, double Slat);

    @Query("SELECT * FROM tbl_Airports WHERE ident='EHLE'")
    public Airport getLelystad();
}
