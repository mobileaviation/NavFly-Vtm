package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Route")
public class Route {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    public Long createdDate;
    public Long modifiedDate;
    public String name;
}
