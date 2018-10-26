package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Chart;

import java.util.ArrayList;

@Dao
public abstract class ChartDao {
    @Insert
    public abstract void InsertChart(Chart chart);

    @Insert
    public abstract void InsertCharts(ArrayList<Chart> charts);

    @Query("SELECT * FROM tbl_Charts WHERE airport_ref=:ref")
    public abstract Chart[] getChartsByAirportRef(long ref);
}
