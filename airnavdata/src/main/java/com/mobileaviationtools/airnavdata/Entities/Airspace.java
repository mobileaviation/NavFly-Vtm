package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Airspaces")
public class Airspace {
    @PrimaryKey
    public long id;
    public String name;
    public String version;
    public String category;
    public long airspace_id;
    public String country;
    public long altLimit_top;
    public String altLimit_top_unit;
    public String altLimit_top_ref;
    public long altLimit_bottom;
    public String altLimit_bottom_unit;
    public String altLimit_bottom_ref;
    public String geometry;
}
