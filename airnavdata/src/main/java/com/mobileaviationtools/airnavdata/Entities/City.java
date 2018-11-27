package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Cities",
        indices = {@Index(name = "cities_location_index", value = {"latitude","longitude"}),
        @Index(name = "cities_country", value ={"country_code"})})
public class City {
    @PrimaryKey
    public Integer id;

    public Integer geonameid;
    public String name;
    public String asciiname;
    public String alternatenames;
    public Double latitude;
    public Double longitude;
    public String feature_class;
    public String feature_code;
    public String country_code;
    public String cc2;
    public String admin1_code;
    public String admin2_code;
    public String admin3_code;
    public String admin4_code;
    public Integer population;
    public Integer elevation;
    public String dem;
    public String timezone;
    public String modification_date;
}
