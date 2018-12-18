package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.PropertiesGroup;
import com.mobileaviationtools.airnavdata.Classes.PropertiesName;
import com.mobileaviationtools.airnavdata.Classes.PropertyGroupTypeConverter;
import com.mobileaviationtools.airnavdata.Classes.PropertyNameTypeConverter;

@Entity(tableName = "tbl_Properties")
public class Property {
    @PrimaryKey(autoGenerate = true)
    public Long id;
    @TypeConverters(PropertyGroupTypeConverter.class)
    public PropertiesGroup groupname;
    @TypeConverters(PropertyNameTypeConverter.class)
    public PropertiesName name;
    public String value1;
    public String value2;
    public String value3;
    public String value4;
}
