package com.mobileaviationtools.airnavdata.Classes;

import com.mobileaviationtools.airnavdata.Models.Statistics;

public interface DataDownloadStatusEvent {
    public void onProgress(Integer count, Integer downloaded, TableType tableType);
    public void OnFinished(TableType tableType);
    public void OnError(String message, TableType tableType);
    public void OnStatistics(Statistics statistics);
}
