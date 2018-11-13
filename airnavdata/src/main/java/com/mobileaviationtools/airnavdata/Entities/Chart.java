package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Classes.ChartTypeTypeConverter;

@Entity(tableName = "tbl_Charts")
public class Chart {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String name;
    @TypeConverters({ChartTypeTypeConverter.class})
    public ChartType type;
    public String filelocation;
    public int airport_ref;
    public int mbtile_id;
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    public byte[] chart;
    public Double latitude_deg_n;
    public Double longitude_deg_w;
    public Double latitude_deg_s;
    public Double longitude_deg_e;
    public Boolean active;
    public int order;

    @Ignore
    public boolean validate()
    {
        if ((latitude_deg_n != null) && (longitude_deg_e != null)
                && (longitude_deg_w != null) && (latitude_deg_s != null))
        {
            if (latitude_deg_s < -90 && latitude_deg_s > 90) return false;
            if (latitude_deg_n < -90 && latitude_deg_s > 90) return false;
            if (longitude_deg_e < -180 && longitude_deg_e > 180) return false;
            if (longitude_deg_w < -180 && longitude_deg_e > 180) return false;
            return ((latitude_deg_n>latitude_deg_s) && (longitude_deg_w<longitude_deg_e));
        }
        else
            return false;
    }
}
