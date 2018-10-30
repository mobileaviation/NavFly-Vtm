package org.oscim.android.cache;

public interface OfflineTileDownloadEvent {
    public void onProgress(long percent);
    public void onFinished();
}
