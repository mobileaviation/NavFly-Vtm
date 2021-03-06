package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.City;

import java.util.List;

@Dao
public abstract class CitiesDao {
    @Insert
    public abstract void insertCities(List<City> cities);

    @Query("SELECT * FROM tbl_Cities WHERE (longitude BETWEEN :Wlon AND :Elon AND latitude BETWEEN :Slat AND :Nlat)")
    public abstract List<City> getCitiesWithinBounds(double Wlon, double Elon, double Nlat, double Slat);

    @Query("DELETE FROM tbl_Cities WHERE country_code in (SELECT country_code FROM tbl_Countries WHERE continent=:continent)")
    public abstract void deleteFromCitiesByContinent(String continent);

    @Query("DELETE FROM tbl_Cities")
    public abstract void deleteFromCities();
}
