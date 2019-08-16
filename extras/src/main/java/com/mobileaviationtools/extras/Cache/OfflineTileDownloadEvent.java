package com.mobileaviationtools.extras.Cache;

public interface OfflineTileDownloadEvent {
    public void onProgress(long percent, String message);
    public void onError(String message);
    public void onFinished();
}
