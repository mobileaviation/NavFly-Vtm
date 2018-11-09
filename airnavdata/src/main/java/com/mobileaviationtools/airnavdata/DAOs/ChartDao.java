package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Entities.Chart;

import java.util.ArrayList;

@Dao
public abstract class ChartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract long InsertChart(Chart chart);

    @Insert
    public abstract void InsertCharts(ArrayList<Chart> charts);

    @Update
    public abstract void UpdateChart(Chart chart);

    @Query("SELECT * FROM tbl_Charts WHERE airport_ref=:ref")
    public abstract Chart[] getChartsByAirportRef(long ref);

    @Query("SELECT * FROM tbl_Charts WHERE active=:active")
    public abstract Chart[] getActiveCharts(Boolean active);

    @Query("SELECT * FROM tbl_Charts WHERE active=:active AND type=:type")
    public abstract Chart[] getActiveChartsByType(Boolean active, String type);

    @Query("SELECT * FROM tbl_Charts")
    public abstract Chart[] getAllCharts();

    @Delete
    public abstract void DeleteChart(Chart chart);
}
