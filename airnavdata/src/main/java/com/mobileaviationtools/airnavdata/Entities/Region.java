package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Regions")
public class Region {
    @PrimaryKey
    public Integer id;
    public String code;
    public String local_code;
    public String name;
    public String continent;
    public String iso_country;
    public String wikipedia_link;
    public String keywords;
}
