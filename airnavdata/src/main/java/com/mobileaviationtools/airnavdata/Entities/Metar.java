package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Metars", indices = {@Index(name="metars_loadedDate_index", value = {"loadedDate"}),
        @Index(name = "metars_airportRef_index", value = "airport_ref")})
public class Metar {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String station_id;
    public Long loadedDate;
    public Integer airport_ref;

    public String raw_text;
    public String observation_time;
    public String flight_category;
    public float latitude;
    public float longitude;
    public float elevation_m;

    @Ignore
    public Double distance_to_org_m;
}
