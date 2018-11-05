package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.icu.text.CollationKey;

import org.oscim.android.cache.OfflineTileCache;
import org.oscim.android.cache.OfflineTileDownloadEvent;
import org.oscim.core.BoundingBox;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;

public class SettingsObject  {
    public enum SettingType
    {
        basechart,
        offline,
        overlays
    }

    public interface SettingsEvent
    {
        public void OnSettingChanged(SettingType type, SettingsObject object);
        public void OnSettingsProgress(SettingType type, SettingsObject object, String message);
        public void OnSettingsSaved(SettingsObject object);
        public void OnSettingsLoaded(SettingsObject object);
    }

    private final String OSCIMAPURL = "http://opensciencemap.org/tiles/vtm/{Z}/{X}/{Y}.vtm";

    public SettingsObject(Context context, Map map)
    {
        this.map = map;
        baseCache = new OfflineTileCache(context, null, "airnav_base_tiles_cache.db");
        long s = 512 * (1 << 10);
        baseCache.setCacheSize(512 * (1 << 10));
    }

    private OfflineTileCache baseCache;
    public void setBaseCache(TileSource source)
    {
        source.tileCache = baseCache;
    }
    public OfflineTileCache getBaseCache()
    {
        return baseCache;
    }

    private SettingsEvent settingsEvent;
    public void SetSettingsEvent(SettingsEvent settingsEvent){ this.settingsEvent = settingsEvent; }
    private void fireSettingsChangedEvent(SettingType type)
    {
        if (settingsEvent != null) settingsEvent.OnSettingChanged(type, this);
    }

    private Map map;
    private Context context;

    public void DownloadTiles(OfflineTileDownloadEvent callback)
    {
        baseCache.SetOnOfflineTileDownloadEvent(callback);
        baseCache.DownloadTiles(map.getBoundingBox(0) ,OSCIMAPURL);
    }

    public void dispose()
    {
        if (baseCache != null)
            baseCache.dispose();
    }

}
