package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity(tableName = "tbl_Airports",
        indices = {@Index(name = "location_index", value = {"latitude_deg","longitude_deg"}),
            @Index(name="ident_index", value = {"name","ident"})})
public class Airport {
    public Airport()
    {
        runways = new ArrayList<>();
        frequencies = new ArrayList<>();
    }

    @Ignore
    public Integer index;

    @Ignore
    public ArrayList<Runway> runways;
    @Ignore
    public ArrayList<Frequency> frequencies;

    @PrimaryKey
    public Integer id;
    public String ident;
    public String type;
    public String name;
    public Double latitude_deg;
    public Double longitude_deg;
    public Double elevation_ft;
    public String continent;
    public String iso_country;
    public String iso_region;
    public String municipality;
    public String scheduled_service;
    public String gps_code;
    public String iata_code;
    public String local_code;
    public String home_link;
    public String wikipedia_link;
    public String keywords;
}
