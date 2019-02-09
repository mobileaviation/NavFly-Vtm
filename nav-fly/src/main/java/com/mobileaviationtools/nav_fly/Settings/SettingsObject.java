package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.icu.text.CollationKey;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.MBTile;
import com.mobileaviationtools.airnavdata.Entities.OnlineTileProvider;
import com.mobileaviationtools.nav_fly.Classes.BaseChartType;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Layers.ChartsOverlayLayers;
import com.mobileaviationtools.nav_fly.Settings.Overlays.ChartSettingsItemAdapter;
import com.mobileaviationtools.nav_fly.Settings.Overlays.MBTileChart;
import com.mobileaviationtools.nav_fly.Settings.Providers.OnlineTileProviderSet;

import org.oscim.android.cache.OfflineTileCache;
import org.oscim.android.cache.OfflineTileDownloadEvent;
import org.oscim.core.BoundingBox;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

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

    private String tiles_url;
    private ArrayList<MBTileChart> mbTileCharts;
    private ArrayList<OnlineTileProviderSet> onlineTileProviders;
    public ChartsOverlayLayers chartsOverlayLayers;
    private Boolean baseCacheEnabled;

    public SettingsObject(GlobalVars vars)
    {
        this.vars = vars;
        baseCacheEnabled = getCache();

        onlineTileProviders = new ArrayList<>();
        mbTileCharts = new ArrayList<>();

        setupOnlineTileProviderChangeEvent();
        loadMBTileChartsOverlays();
    }

    private Boolean getCache()
    {
        SharedPreferences databasePrefs = getSharedPreferences();
        return databasePrefs.getBoolean("BaseChartCache", true);
    }

    private SharedPreferences getSharedPreferences()
    {
        return vars.mainActivity.getSharedPreferences("Database", MODE_PRIVATE);
    }

    public void setupChartsOverlayLayers()
    {
        int index = vars.map.layers().size()-1;
                chartsOverlayLayers = new ChartsOverlayLayers(vars, index);
        AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(vars.context);
        Chart[] dbCharts = db.getCharts().getActiveCharts(true);
        for (Chart c : dbCharts) {
            setChart(c);
        }
    }

    public void setupOnlineTileProviders()
    {
        // TODO This needs to be stored to and retrieved from the database
        AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(vars.context);

        for (OnlineTileProviders provider : OnlineTileProviders.values())
        {
            OnlineTileProviderSet set = new OnlineTileProviderSet();
            set.provider = provider;

            OnlineTileProvider p = db.getOnlineTileProviders().getTileProviderByType(provider.toString());

            if (p != null)
            {
                set.active = p.active;
                set.cache = p.cache;
                set.id = p.id;
                if (set.active) set.addLayer(vars);
            }
            else {
                set.active = false;
                set.cache = false;
                set.id = -1l;
            }

            set.SetTileProviderSetChanged(onlineTileProviderschangeEvent);
            onlineTileProviders.add(set);
        }
    }

    private OnlineTileProviderSet.TileProviderSetChanged onlineTileProviderschangeEvent;
    private void setupOnlineTileProviderChangeEvent()
    {
        onlineTileProviderschangeEvent = new OnlineTileProviderSet.TileProviderSetChanged() {
            @Override
            public void onTileProviderSetActivated(OnlineTileProviderSet tileProviderSet, OnlineTileProviderSet.ChangeType type) {
                if (type==OnlineTileProviderSet.ChangeType.active) {
                    if (tileProviderSet.active) tileProviderSet.addLayer(vars);
                    else
                        tileProviderSet.removeLayer(vars);
                }

                if (type==OnlineTileProviderSet.ChangeType.cache)
                {
                    if (tileProviderSet.cache)
                    {
                        tileProviderSet.removeCache();
                        tileProviderSet.removeLayer(vars);
                        if (tileProviderSet.active) tileProviderSet.addLayer(vars);
                    }
                    else
                    {
                        tileProviderSet.removeCache();
                    }
                }

                OnlineTileProvider provider = new OnlineTileProvider();
                AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(vars.context);

                provider.active = tileProviderSet.active;
                provider.cache = tileProviderSet.cache;
                provider.type = tileProviderSet.provider;
                provider.id = tileProviderSet.id;
                if (provider.id==-1) db.getOnlineTileProviders().InsertOnlineTileProvider(provider);
                else
                    db.getOnlineTileProviders().UpdateOnlineTileProvider(provider);

            }
        };
    }


    public void setBaseCacheEnabled(Boolean baseCacheEnabled)
    {
        this.baseCacheEnabled = baseCacheEnabled;
    }
    private OfflineTileCache baseCache;
    public void setBaseCache(TileSource source, String url, BaseChartType baseChartType)
    {
        baseCache = new OfflineTileCache(vars.context, null, "airnav_" +
                baseChartType.toString() + "_tiles_cache.db");
        long s = 512 * (1 << 10);
        baseCache.setCacheSize(512 * (1 << 10));

        tiles_url = url;

        if (baseCacheEnabled) {
            source.tileCache = baseCache;
        }
        else
        {
            source.tileCache = null;
        }
    }
    public OfflineTileCache getBaseCache()
    {
        return baseCache;
    }
    public void ClearBaseCache()
    {
        baseCache.deleteAllTiles();
    }

    private SettingsEvent settingsEvent;
    public void SetSettingsEvent(SettingsEvent settingsEvent){ this.settingsEvent = settingsEvent; }
    private void fireSettingsChangedEvent(SettingType type)
    {
        if (settingsEvent != null) settingsEvent.OnSettingChanged(type, this);
    }

    private GlobalVars vars;
    public ChartSettingsItemAdapter chartSettingsItemAdapter;

    public void DownloadTiles(OfflineTileDownloadEvent callback)
    {
        baseCache.SetOnOfflineTileDownloadEvent(callback);
        baseCache.DownloadTiles(vars.map.getBoundingBox(0) ,tiles_url);
    }

    private Chart[] getChartsFromDB()
    {
        AirnavChartsDatabase airnavChartsDatabase = AirnavChartsDatabase.getInstance(vars.context);
        Chart[] charts = airnavChartsDatabase.getCharts().getAllCharts();

        ArrayList<Chart> retCharts = new ArrayList<>();
        // remove MBTiles for the charts list that are not present in the DB anymore (due to update oid)
        for (Chart c: charts)
        {
            if (c.mbtile_id>0)
            {
                if (mbTilePresent(c))
                {
                    retCharts.add(c);
                }
                else
                {
                    AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(vars.context);
                    db.getCharts().DeleteChart(c);
                }
            }

        }

        return retCharts.toArray(new Chart[retCharts.size()]);
    }

    private MBTile[] getTilesFromDB()
    {
        AirnavDatabase airnavDatabase = AirnavDatabase.getInstance(vars.context);
        return airnavDatabase.getTiles().getAllMBTiles();
    }

    private void loadTiles(MBTile[] tiles)
    {
        for (MBTile tile: tiles)
        {
            MBTileChart mbTileChart = new MBTileChart(vars.context);
            mbTileChart.setTile(tile);
            setupMBTileChartListener(mbTileChart);
            mbTileCharts.add(mbTileChart);
        }
    }

    private void loadCharts(Chart[] charts)
    {
        for (Chart chart: charts)
        {
            MBTileChart mbTileChart = new MBTileChart(vars.context);
            mbTileChart.setChart(chart);
            if (!mbTileCharts.contains(mbTileChart)) {
                setupMBTileChartListener(mbTileChart);
                mbTileCharts.add(mbTileChart);
            }
            else
            {
                MBTileChart exChart = mbTileCharts.get(mbTileCharts.indexOf(mbTileChart));
                exChart.setChart(chart);
            }
        }
    }

    private boolean mbTilePresent(Chart chart)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        MBTile t = db.getTiles().getMBTileById(chart.mbtile_id);
        return (t != null);
    }

    private void loadMBTileChartsOverlays()
    {
        MBTile[] ofmTiles = getTilesFromDB();
        loadTiles(ofmTiles);
        updateChartsFromDB();
    }

    public void updateChartsFromDB()
    {
        Chart[] charts = getChartsFromDB();
        loadCharts(charts);
    }

    public ArrayList<MBTileChart> getMbTileCharts() {
        return mbTileCharts;
    }

    public ArrayList<OnlineTileProviderSet> getOnlineTileProviders() { return onlineTileProviders; }

    private void setupMBTileChartListener(MBTileChart chart)
    {
        chart.SetMBTileChartChangedEvent(new MBTileChart.MBTileChartChangedEvent() {
            @Override
            public void OnChanged(MBTileChart chart, MBTileChart.status newStatus, boolean active) {
                chart.chartStatus = newStatus;
                if (newStatus == MBTileChart.status.present) setupOverlayChart(chart, false);
                if (newStatus == MBTileChart.status.visible) setupOverlayChart(chart,true);

                if (newStatus == MBTileChart.status.gone) chart.deleteChart();

                SettingsObject.this.chartSettingsItemAdapter.notifyDataSetChanged();
            }
            @Override
            public void OnChangeInProgress(MBTileChart chart)
            {
                SettingsObject.this.chartSettingsItemAdapter.notifyDataSetChanged();
            }
        });
    }

    private void setupOverlayChart(MBTileChart chart, boolean active)
    {
        chart.updateChart(active);
        setChart(chart.getChart());
    }

    private void setChart(Chart chart)
    {
        switch (chart.type){
            case jpg:{
                chartsOverlayLayers.setChart(chart);
                break;
            }
            case pdf:
            {
                break;
            }
            case png:
            {
                chartsOverlayLayers.setChart(chart);
                break;
            }
            case mbtiles:
            {
                chartsOverlayLayers.setChart(chart);
                break;
            }
            case unknown:
            {
                break;
            }
        }
    }

    public void dispose()
    {
        if (baseCache != null)
            baseCache.dispose();
    }

}
