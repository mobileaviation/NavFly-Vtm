package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_ATCStations")
public class ATCStation {
    @PrimaryKey
    public Integer id;
    public Long airspace_id;

    public String frequency;
    public String stationname;
}
