package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
//        foreignKeys = @ForeignKey(entity = Airport.class, parentColumns = "id", childColumns = "airport_ref"),
@Entity(tableName = "tbl_Frequencies",
        indices = @Index(name = "airport_ident_index", value = "airport_ident"))
public class Frequency {
    @PrimaryKey
    public Integer id;
    public Integer airport_ref;
    public String airport_ident;
    public String type;
    public String description;
    public Double frequency_mhz;
}
