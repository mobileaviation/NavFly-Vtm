package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Navaid;

import java.util.List;

@Dao
public interface NavaidsDao {
    @Insert
    public void insertNavaids(List<Navaid> navaidList);

    @Insert
    public void insertNavaid(Navaid navaid);

    @Query("SELECT * FROM tbl_Navaids WHERE (longitude_deg BETWEEN :Wlon AND :Elon AND latitude_deg BETWEEN :Slat AND :Nlat)")
    public Navaid[] getNavaidsWithinBoundsByTypes(double Wlon, double Elon, double Nlat, double Slat);
}
