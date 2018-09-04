package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Fix;

import java.util.List;

@Dao
public abstract class FixesDao {
    @Insert
    public abstract void insertFixes(List<Fix> fixes);

    @Transaction
    public void insertFixesTransaction(List<Fix> fixes) {
        insertFixes(fixes);
    }

    @Insert
    public abstract void insertFix(Fix fix);

    @Query("SELECT * FROM tbl_Fixes WHERE ident=:ident")
    public abstract List<Fix> getFixesByIdent(String ident);
}
