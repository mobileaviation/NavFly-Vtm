package com.mobileaviationtools.nav_fly.Base;

import com.mobileaviationtools.extras.tiling.openflightmaps.OpenFlightMapsTileSource;
import com.mobileaviationtools.extras.Classes.BaseChartType;
import com.mobileaviationtools.nav_fly.GlobalVars;

import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import com.mobileaviationtools.extras.tiling.Vector.NextzenMvtTileSource;
import com.mobileaviationtools.extras.tiling.Vector.OpenMapTilesMvtTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

public class BaseChart {
    public BaseChart(GlobalVars vars)
    {
        this.vars = vars;
    }

    public void setBaseCharts(BaseChartType baseChartType)
    {
        boolean _vector = true;
        switch (baseChartType)
        {
            case nextzen:
            {
                setNextZenMap();
                break;
            }
            case openmaptiles:
            {
                setOpenTilesMap();
                break;
            }
            case opensciencemaps:
            {
                setOpenScienceMap();
                break;
            }
            case openflightmaps:
            {
                setOpenFlightMaps();
                _vector = false;
                break;
            }
        }

        vars.baseChartType = baseChartType;

        if (_vector) {
            baseLayer = vars.map.setBaseMap(tileSource);
            labelLayer = new LabelLayer(vars.map, baseLayer);
            vars.map.layers().add(labelLayer, vars.BASE_GROUP);
            vars.map.setTheme(defaultTheme);
        } else
        {
            bitmapBaseLayer = new BitmapTileLayer(vars.map, tileSource);
            vars.map.layers().add(bitmapBaseLayer, vars.BASE_GROUP);
        }

    }

    public void removeBaseLayers()
    {
        vars.map.layers().remove(labelLayer);
        tileSource = null;
    }

    private TileSource tileSource;
    private TileSource tileAeroSource;
    private VectorTileLayer baseLayer;
    private BitmapTileLayer bitmapBaseLayer;
    private BitmapTileLayer bitmapAeroLayer;
    private LabelLayer labelLayer;
    private VtmThemes defaultTheme;
    private GlobalVars vars;

    private void setOpenScienceMap()
    {
        tileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        defaultTheme = VtmThemes.DEFAULT;

        String[] url = new String[1];
        url[0] = ((OSciMap4TileSource) tileSource).getUrl().toString();
        vars.settingsObject.setBaseCache(tileSource, url, vars.baseChartType);
    }

    private void setNextZenMap()
    {
        tileSource = NextzenMvtTileSource.builder()
                //.apiKey("X9Iq4O_GTZeKHy4_w-_q8w") // Put a proper API key
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                //.locale("en")
                .build();

        String[] url = new String[1];
        url[0] = ((NextzenMvtTileSource) tileSource).getNextzenUrl();
        vars.settingsObject.setBaseCache(tileSource, url, vars.baseChartType);
        defaultTheme = VtmThemes.MAPZEN;
    }

    private void setOpenTilesMap()
    {
        String openTilesMapKey = "29WrAEe6HeyBZgOaLFda";
        tileSource = OpenMapTilesMvtTileSource.builder()
                .apiKey(openTilesMapKey)
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        String[] url = new String[1];
        url[0] = ((OpenMapTilesMvtTileSource) tileSource).getOpenTilesMapUrl();
        vars.settingsObject.setBaseCache(tileSource, url, vars.baseChartType);
        defaultTheme = VtmThemes.OPENMAPTILES;
    }

    private void setOpenFlightMaps() {
        tileSource = OpenFlightMapsTileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();
        String[] url = ((OpenFlightMapsTileSource) tileSource).getBaseUrls();

        vars.settingsObject.setBaseCache(tileSource, url, vars.baseChartType);
    }

}
