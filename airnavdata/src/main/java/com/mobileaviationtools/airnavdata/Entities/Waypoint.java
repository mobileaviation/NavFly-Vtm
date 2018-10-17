package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Waypoint")
public class Waypoint {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public Integer index;
    public Long route_id;
    public String name;
    public String type;
    public Long ref;
    public Double latitude;
    public Double longitude;
}
