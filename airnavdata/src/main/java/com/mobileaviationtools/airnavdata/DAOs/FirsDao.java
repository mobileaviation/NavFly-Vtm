package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.mobileaviationtools.airnavdata.Entities.Fir;

import java.util.List;

@Dao
public abstract class FirsDao {
    @Insert
    public abstract void insertFirs(List<Fir> firs);

    @Transaction
    public void insertFirsTransaction(List<Fir> firs) {
        insertFirs(firs);
    }

    @Insert
    public abstract void insertFir(Fir fir);

    @Query("SELECT * FROM tbl_Firs WHERE ident=:ident")
    public abstract List<Fir> getFirsByIdent(String ident);
}
