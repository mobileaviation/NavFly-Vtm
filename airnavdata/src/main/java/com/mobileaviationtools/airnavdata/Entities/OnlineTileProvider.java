package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviderConverter;
import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;

@Entity(tableName = "tbl_OnlineTileProviders")
public class OnlineTileProvider {
    @PrimaryKey(autoGenerate = true)
    public Long id;

    @TypeConverters(OnlineTileProviderConverter.class)
    public OnlineTileProviders type;

    public Boolean active;
    public Boolean cache;
}
