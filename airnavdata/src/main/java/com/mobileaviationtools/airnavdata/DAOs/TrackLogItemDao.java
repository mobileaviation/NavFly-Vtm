package com.mobileaviationtools.airnavdata.DAOs;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;

import java.util.ArrayList;
import java.util.List;

@Dao
public abstract class TrackLogItemDao {
    @Query("SELECT * FROM tbl_TrackLogItems WHERE trackLogId=:tracklogId")
    public abstract List<TrackLogItem> getTracklogItemByLogId(Long tracklogId);

    @Insert
    public abstract Long insertItem(TrackLogItem item);
}
