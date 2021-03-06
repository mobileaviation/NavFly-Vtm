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

    @Query("SELECT TL.* FROM tbl_TrackLogs TL " +
            " WHERE (SELECT COUNT(*) FROM tbl_TrackLogItems WHERE trackLogId=TL.id)>0" +
            " ORDER BY logDate DESC")
    public abstract List<TrackLog> getTracklogs();


    @Insert
    public abstract Long InsertLog(TrackLog log);
}
