package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Tafs", indices = {@Index(name="tafs_loadedDate_index", value = {"loadedDate"}),
        @Index(name = "tafs_airportRef_index", value = "airport_ref")})
public class Taf {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String station_id;
    public Long loadedDate;
    public Integer airport_ref;

    public String raw_text;
    public String valid_time_from;
    public String valid_time_to;
    public float latitude;
    public float longitude;
    public float elevation_m;
}
