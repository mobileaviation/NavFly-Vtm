package com.mobileaviationtools.airnavdata.Classes;

public interface DataDownloadStatusEvent {
    public void onProgress(Integer count, Integer downloaded, TableType tableType);
    public void OnFinished(TableType tableType);
    public void OnError(String message, TableType tableType);
}
