package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Classes.AirportTypeConverter;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "tbl_Airports",
        indices = {@Index(name = "airport_location_index", value = {"latitude_deg","longitude_deg"}),
            @Index(name="ident_index", value = {"name","ident"}),
            @Index(name="id_index", value = {"id"})})
public class Airport {
    public Airport()
    {

    }

    @Ignore
    public Integer index;

    @Ignore
    public List<Runway> runways;
    @Ignore
    public List<Frequency> frequencies;

//    @PrimaryKey(autoGenerate = true)
//    public Integer _id;
    @PrimaryKey
    public Integer id;
    public String ident;
    @TypeConverters({AirportTypeConverter.class})
    public AirportType type;
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
