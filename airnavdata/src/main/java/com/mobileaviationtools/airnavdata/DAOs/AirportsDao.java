package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Entities.Airport;

import java.util.List;

@Dao
public abstract class AirportsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
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

    @Query("SELECT * FROM tbl_Airports WHERE ident=:ident")
    public abstract Airport getAirportByIdent(String ident);

    @Query("SELECT * FROM tbl_Airports WHERE id=:id")
    public abstract Airport getAirportByID(Long id);

    @Query("SELECT * FROM tbl_Airports WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat) ORDER BY name LIMIT :limit")
    public abstract List<Airport> getAirportListWithinBoundsLimit(double Wlon, double Elon, double Nlat, double Slat, Long limit);

    @Query("SELECT * FROM tbl_Airports WHERE name like :name OR ident like :name ORDER BY name LIMIT :limit")
    public abstract List<Airport> searchAirportsByNameOrIdentLimit(String name, Long limit);

    @Query("SELECT * FROM tbl_Airports WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat) AND type IN (:types) ORDER BY name LIMIT :limit")
    public abstract List<Airport> getAirportListWithinBoundsLimitType(double Wlon, double Elon, double Nlat, double Slat, Long limit, List<String> types);

    @Query("SELECT * FROM tbl_Airports WHERE (name like :name OR ident like :name) AND type IN (:types) ORDER BY name LIMIT :limit")
    public abstract List<Airport> searchAirportsByNameOrIdentLimitType(String name, Long limit, List<String> types);

}
