package com.mobileaviationtools.nav_fly.Settings.Providers;

import android.text.format.DateUtils;
import android.util.Log;

import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;
import com.mobileaviationtools.nav_fly.GlobalVars;

import org.oscim.android.cache.OfflineTileCache;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.tiling.source.bitmap.BitmapTileSource;

public class OnlineTileProviderSet {
    public enum ChangeType
    {
        active,
        cache
    }

    public interface TileProviderSetChanged
    {
        public void onTileProviderSetActivated(OnlineTileProviderSet tileProviderSet, ChangeType type);
    }

    private String TAG = "OnlineTileProviderSet";

    private TileProviderSetChanged changeEvent;
    public void SetTileProviderSetChanged(TileProviderSetChanged changeEvent)
    {
        this.changeEvent = changeEvent;
    }

    public void FireChangeEvent(ChangeType type)
    {
        if (changeEvent != null) changeEvent.onTileProviderSetActivated(this, type);
    }

    public OnlineTileProviders provider;
    private String getTimeStamp()
    {
        //Long dd = 1544634000l;
        long timeMs = System.currentTimeMillis() - 300000;
        Long roundedtimeMs =  (Math.round( (double)( (double)timeMs/(double)(10*60*1000) ) ) * (10*60*1000)) / 1000;
        Log.i(TAG, "roundedTime: " + roundedtimeMs.toString());
        return roundedtimeMs.toString();
    }
    public String getUrl()
    {
        return provider.getBaseUrl().replace("{TIMESTAMP}", getTimeStamp());
    }
    public String getName()
    {
        return provider.getName();
    }

    public Long id;
    public Boolean active;
    public Boolean cache;

    private BitmapTileSource tileSource;
    private BitmapTileLayer layer;
    private OfflineTileCache baseCache;

    public BitmapTileSource getTileSource() {
        if (tileSource == null)
            tileSource = new BitmapTileSource(getUrl(),
                    provider.getTilePath(), 4, 16);
        return tileSource;
    }

    public void addLayer(GlobalVars vars)
    {
        BitmapTileSource source = getTileSource();
        if (cache) setupCache(vars, source);
        if (layer == null)
            layer = new BitmapTileLayer(vars.map, source);

        vars.map.layers().add(layer, vars.ONLINETILES_GROUP);
    }

    private void setupCache(GlobalVars vars, BitmapTileSource source)
    {
        if (baseCache == null) {
            baseCache = new OfflineTileCache(vars.context, null, "airnav_" +
                    provider.getName().toLowerCase().replace(' ', '_') + "_tiles_cache.db");
            long s = 512 * (1 << 10);
            baseCache.setCacheSize(512 * (1 << 10));
        }
        source.setCache(baseCache);
    }

    public void removeCache()
    {
        if (tileSource != null)
            tileSource.tileCache = null;
        baseCache = null;
    }



    public void removeLayer(GlobalVars vars)
    {
        if (layer != null)
        {
            vars.map.layers().remove(layer);
            layer = null;
            tileSource = null;
        }
    }
}
