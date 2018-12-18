package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.mobileaviationtools.airnavdata.Entities.Property;

import java.util.List;


@Dao
public abstract class PropertiesDao {
    @Insert
    public abstract void InsertProperty(Property properties);

    @Update
    public abstract void UpdateProperty(Property properties);

    @Query("SELECT * FROM tbl_Properties WHERE groupname=:group AND name=:name")
    public abstract Property getPropertyByGroupAndName(String group, String name);
}
