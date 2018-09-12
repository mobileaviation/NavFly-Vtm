package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_MbTiles")
public class MBTile {
    @PrimaryKey
    public Integer id;
    public Integer index;
    public Integer startValidity;
    public Integer endValidity;
    public String name;
    public String mbtileslink;
    public String xmlliink;
    public String region;
    public String type;
    public Integer version;
}
