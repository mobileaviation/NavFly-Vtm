package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Region;

import java.util.List;

@Dao
public abstract class RegionsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertRegions(List<Region> regions);

    @Transaction
    public void insertRegionsTransaction(List<Region> regions)
    {
        insertRegions(regions);
    }

    @Insert
    public abstract void insertRegion(Region frequency);

    @Query("SELECT * FROM tbl_Regions WHERE code=:code")
    public abstract List<Region> getRegionsByCode(String code);
}
