package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;
import com.mobileaviationtools.airnavdata.Entities.OnlineTileProvider;

import java.util.List;

@Dao
public abstract class OnlineTileProvidersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract Long InsertOnlineTileProvider(OnlineTileProvider provider);

    @Update
    public abstract void UpdateOnlineTileProvider(OnlineTileProvider provider);

    @Query("SELECT * FROM tbl_OnlineTileProviders")
    public abstract List<OnlineTileProvider> GetAllOnlineTileProviders();

    @Query("SELECT * FROM tbl_OnlineTileProviders WHERE type=:type")
    public abstract OnlineTileProvider getTileProviderByType(String type);
}
