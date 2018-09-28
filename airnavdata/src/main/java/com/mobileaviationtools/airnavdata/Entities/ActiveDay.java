package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_ActiveDays")
public class ActiveDay {
    @PrimaryKey
    public Integer id;
    public Long airspace_id;

    public String day;
    public String start;
    public String end;
    public String timezone;
}
