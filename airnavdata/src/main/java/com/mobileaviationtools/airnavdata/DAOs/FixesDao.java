package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Fix;

import java.util.List;

@Dao
public abstract class FixesDao {
    @Insert
    public abstract void insertFixes(List<Fix> fixes);

    @Transaction
    public void insertFixesTransaction(List<Fix> fixes) {
        insertFixes(fixes);
    }

    @Insert
    public abstract void insertFix(Fix fix);

    @Query("SELECT * FROM tbl_Fixes WHERE ident=:ident")
    public abstract List<Fix> getFixesByIdent(String ident);

    @Query("SELECT * FROM tbl_Fixes WHERE id=:id")
    public abstract Fix getFixesByID(Long id);

    @Query("SELECT * FROM tbl_Fixes WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat)")
    public abstract Fix[] getFixessWithinBoundsByTypes(double Wlon, double Elon, double Nlat, double Slat);

    @Query("SELECT * FROM tbl_Fixes WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat) ORDER BY name LIMIT :limit")
    public abstract List<Fix> getFixesListWithinBoundsLimit(double Wlon, double Elon, double Nlat, double Slat, Long limit);

    @Query("SELECT * FROM tbl_Fixes WHERE name like :name OR ident like :name ORDER BY name LIMIT :limit")
    public abstract List<Fix> searchFixesByNameOrIdentLimit(String name, Long limit);

    @Query("DELETE FROM tbl_Fixes WHERE continent=:continent")
    public abstract void deleteFromFixesByContinent(String continent);

    @Query("DELETE FROM tbl_Fixes")
    public abstract void deleteFromFixes();
}
