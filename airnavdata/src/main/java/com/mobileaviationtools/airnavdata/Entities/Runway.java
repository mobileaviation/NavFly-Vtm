package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Runways",
        foreignKeys = @ForeignKey(entity = Airport.class, parentColumns = "id", childColumns = "airport_ref"),
        indices = {@Index(name = "location_index", value = {"latitude_deg", "longitude_deg"}),
                    @Index(name = "airport_ident_index", value = "airport_ident")})
public class Runway {
    @PrimaryKey
    public Integer id;
    public Integer airport_ref;
    public String airport_ident;
    public Integer length_ft;
    public Integer width_ft;
    public String surface;
    public String le_ident;
    public Double le_latitude_deg;
    public Double le_longitude_deg;
    public Integer le_elevation_ft;
    public Double le_heading_degT;
    public Integer le_displaced_threshold_ft;
    public String he_ident;
    public Double he_latitude_deg;
    public Double he_longitude_deg;
    public Integer he_elevation_ft;
    public Double he_heading_degT;
    public Integer he_displaced_threshold_ft;
    public Integer lighted;
    public Integer closed;
}
