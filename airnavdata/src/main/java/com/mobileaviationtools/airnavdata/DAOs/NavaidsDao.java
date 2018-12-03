package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Navaid;

import java.util.List;

@Dao
public abstract class NavaidsDao {
    @Insert
    public abstract void insertNavaids(List<Navaid> navaidList);

    @Transaction
    public void insertNavaidsTransaction(List<Navaid> navaidList)
    {
        insertNavaids(navaidList);
    }

    @Insert
    public abstract void insertNavaid(Navaid navaid);

    @Query("SELECT * FROM tbl_Navaids WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat)")
    public abstract Navaid[] getNavaidsWithinBoundsByTypes(double Wlon, double Elon, double Nlat, double Slat);

    @Query("SELECT * FROM tbl_Navaids WHERE id=:id")
    public abstract Navaid getNavaidByID(Long id);

    @Query("SELECT * FROM tbl_Navaids WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat) ORDER BY name LIMIT :limit")
    public abstract List<Navaid> getNavaidsListWithinBoundsLimit(double Wlon, double Elon, double Nlat, double Slat, Long limit);

    @Query("SELECT * FROM tbl_Navaids WHERE name like :name OR ident like :name ORDER BY name LIMIT :limit")
    public abstract List<Navaid> searchNaviadsByNameOrIdentLimit(String name, Long limit);
}
