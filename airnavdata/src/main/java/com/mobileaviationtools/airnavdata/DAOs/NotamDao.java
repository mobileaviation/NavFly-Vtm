package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Notam;
import com.mobileaviationtools.airnavdata.Entities.Taf;

import java.util.List;

@Dao
public abstract class NotamDao {
    @Insert
    public abstract void InsertNotam(Notam notam);

    @Insert
    public abstract void InsertNotams(List<Notam> notams);

    @Transaction
    public void InsertNotamsTransaction(List<Notam> notams)
    {
        InsertNotams(notams);
    }

    @Query("SELECT * FROM tbl_Notams WHERE icaoId=:icaoId")
    public abstract Notam[] getNotamsByStationId(String icaoId);

    @Query("SELECT * FROM tbl_Notams WHERE icaoId=:icaoId group by notamNumber order by endDate desc")
    public abstract Notam[] getLatestStoredNotamsByStationId(String icaoId);

    @Query("SELECT * FROM tbl_Notams WHERE (longitude BETWEEN :Wlon AND :Elon AND latitude BETWEEN :Slat AND :Nlat) GROUP BY icaoId")
    public abstract List<Notam> getStationsListWithinBoundsLimit(double Wlon, double Elon, double Nlat, double Slat);
}
