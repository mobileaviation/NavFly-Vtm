package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.DateConverter;

import java.util.Date;

@Entity(tableName = "tbl_TrackLogItems")
public class TrackLogItem {
    @PrimaryKey
    public Long id;
    public Long trackLogId;
    public Double longitude_deg;
    public Double latitude_deg;
    public Double true_heading_deg;
    public Double ground_speed_kt;
    public Double altitude_ft;
    @TypeConverters({DateConverter.class})
    public Date logDate;
}


