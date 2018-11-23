package com.mobileaviationtools.airnavdata.DAOs;

import android.app.DownloadManager;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.mobileaviationtools.airnavdata.Entities.TrackLog;

import java.util.List;

@Dao
public abstract class TrackLogDao {
    @Query("SELECT * FROM tbl_TrackLogs WHERE id=:tracklog_id")
    public abstract TrackLog getTracklogByID(Long tracklog_id);

    @Insert
    public abstract Long InsertLog(TrackLog log);
}
