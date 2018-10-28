package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Classes.ChartTypeTypeConverter;

@Entity(tableName = "tbl_Charts")
public class Chart {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    @TypeConverters({ChartTypeTypeConverter.class})
    public ChartType type;
    public String filelocation;
    public int airport_ref;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] chart;
    public Double latitude_deg_n;
    public Double longitude_deg_w;
    public Double latitude_deg_s;
    public Double longitude_deg_e;
    public Boolean active;
}
