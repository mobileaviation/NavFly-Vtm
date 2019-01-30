package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Fixes")
public class Fix {
    @PrimaryKey(autoGenerate = true)
    public Integer id;
    public String continent;
    public String name;
    public String ident;
    public Double latitude_deg;
    public Double longitude_deg;

}
