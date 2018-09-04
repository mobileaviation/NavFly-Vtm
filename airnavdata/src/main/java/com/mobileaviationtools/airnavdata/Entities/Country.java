package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Countries")
public class Country {
    @PrimaryKey
    public int id;
    public String code;
    public String name;
    public Integer index;
    public String continent;
    public String wikipedia_link;
    public String keywords;
}
