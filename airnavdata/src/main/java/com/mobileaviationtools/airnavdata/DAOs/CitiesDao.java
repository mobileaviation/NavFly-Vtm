package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;

import com.mobileaviationtools.airnavdata.Entities.City;

import java.util.List;

@Dao
public abstract class CitiesDao {
    @Insert
    public abstract void insertCities(List<City> cities);
}
