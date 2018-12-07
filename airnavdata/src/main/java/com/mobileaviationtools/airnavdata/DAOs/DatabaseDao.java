package com.mobileaviationtools.airnavdata.DAOs;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.Database;

import java.util.List;
@Dao
public abstract class DatabaseDao {
    @Insert
    public abstract void InsertDatabase(Database database);

    @Query("SELECT * FROM tbl_Databases WHERE database_name=:database_name AND continent=:continent")
    public abstract List<Database> getDatabaseByNameAndContinent(String database_name, String continent );
}
