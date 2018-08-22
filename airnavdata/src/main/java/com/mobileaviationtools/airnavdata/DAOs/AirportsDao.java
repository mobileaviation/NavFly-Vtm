package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Entities.Airport;

import java.util.List;

@Dao
public abstract class AirportsDao {
    @Insert
    public abstract void insertAirports(List<Airport> airportList);

    @Insert
    public abstract void insertAirport(Airport airport);

    @Transaction
    public void insertAirportTransaction(List<Airport> airportList)
    {
        insertAirports(airportList);
    }

    @Query("SELECT * FROM tbl_Airports WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat)")
    public abstract Airport[] getAirportsWithinBounds(double Wlon, double Elon, double Nlat, double Slat);

    @Query("SELECT * FROM tbl_Airports WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat) AND type IN(:types)")
    public abstract Airport[] getAirportsWithinBoundsByTypes(double Wlon, double Elon, double Nlat, double Slat, List<String> types);

    @Query("SELECT * FROM tbl_Airports WHERE ident='EHLE'")
    public abstract Airport getLelystad();
}
