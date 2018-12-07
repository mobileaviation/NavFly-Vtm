package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.DateConverter;

import java.util.Date;

@Entity(tableName = "tbl_Databases")
public class Database {
    @PrimaryKey
    public Integer id;
    public String database_name;
    public String continent;
    @TypeConverters({DateConverter.class})
    public Date download_date;
    public Integer version;
}
