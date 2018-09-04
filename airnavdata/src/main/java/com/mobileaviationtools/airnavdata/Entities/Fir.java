package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Firs")
public class Fir {
    @PrimaryKey
    public Integer id;
    public String name;
    public String ident;
    public String position;
    public String polygon;
}
