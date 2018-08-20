package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import java.util.List;

@Dao
public interface NavaidsDao {
    @Insert
    public void insertNavaids(List<Navaid> navaidList);

    @Insert
    public void insertNavaid(Navaid navaid);
}
