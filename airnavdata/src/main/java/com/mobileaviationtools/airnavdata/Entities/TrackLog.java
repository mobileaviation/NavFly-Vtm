package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.DateConverter;

import java.util.Date;

@Entity(tableName = "tbl_TrackLogs")
public class TrackLog {
    @PrimaryKey
    public Long id;
    public String name;
    @TypeConverters({DateConverter.class})
    public Date logDate;
    public Long routeId;
    public String routeName;
}
