package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.MBTile;

import java.util.List;

@Dao
public abstract class MBTilesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertTiles(List<MBTile> mbTiles);

    @Transaction
    public void insertTilesTransaction(List<MBTile> mbTiles) {
        insertTiles(mbTiles);
    }

    @Insert
    public abstract void insertTile(MBTile mbTile);

    @Query("SELECT * FROM tbl_MbTiles")
    public abstract MBTile[] getAllMBTiles();
}
