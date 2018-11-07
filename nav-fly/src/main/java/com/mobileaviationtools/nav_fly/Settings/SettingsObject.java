package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.icu.text.CollationKey;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.MBTile;
import com.mobileaviationtools.nav_fly.Settings.Overlays.MBTileChart;

import org.oscim.android.cache.OfflineTileCache;
import org.oscim.android.cache.OfflineTileDownloadEvent;
import org.oscim.core.BoundingBox;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;

import java.util.ArrayList;

public class SettingsObject  {
    public enum SettingType
    {
        basechart,
        additionalcharts,
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
    private ArrayList<MBTileChart> mbTileCharts;

    public SettingsObject(Context context, Map map)
    {
        this.map = map;
        this.context = context;

        baseCache = new OfflineTileCache(context, null, "airnav_base_tiles_cache.db");
        long s = 512 * (1 << 10);
        baseCache.setCacheSize(512 * (1 << 10));

        mbTileCharts = new ArrayList<>();
        loadMBTileChartsOverlays();
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

    private void loadMBTileChartsOverlays()
    {
        AirnavDatabase airnavDatabase = AirnavDatabase.getInstance(context);
        MBTile[] ofmTiles = airnavDatabase.getTiles().getAllMBTiles();
        AirnavChartsDatabase airnavChartsDatabase = AirnavChartsDatabase.getInstance(context);
        Chart[] charts = airnavChartsDatabase.getCharts().getAllCharts();

        for (MBTile tile: ofmTiles)
        {
            MBTileChart mbTileChart = new MBTileChart(context);
            mbTileChart.setTile(tile);
            mbTileCharts.add(mbTileChart);
        }

        for (Chart chart: charts)
        {
            MBTileChart mbTileChart = new MBTileChart(context);
            mbTileChart.setChart(chart);
            if (!mbTileCharts.contains(mbTileChart))
                mbTileCharts.add(mbTileChart);
        }
    }

    public void dispose()
    {
        if (baseCache != null)
            baseCache.dispose();
    }

}
