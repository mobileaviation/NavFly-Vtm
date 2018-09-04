package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Country;

import java.util.List;

@Dao
public abstract class CountriesDao {
    @Insert
    public abstract void insertCountries(List<Country> countries);

    @Transaction
    public void insertCountriesTransaction(List<Country> countries) {
        insertCountries(countries);
    }

    @Insert
    public abstract void insertCounty(Country country);

    @Query("SELECT * FROM tbl_Countries WHERE code=:code")
    public abstract List<Country> getCountriesByCode(String code);
}
