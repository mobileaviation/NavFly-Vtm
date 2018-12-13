package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.nav_fly.GlobalVars;

import org.oscim.android.tiling.Overlay.OverlayTileSource;
import org.oscim.android.tiling.mbtiles.MBTilesTileSource;
import org.oscim.core.BoundingBox;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.tiling.TileSource;
import org.oscim.utils.pool.Inlist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ChartsOverlayLayers {

    public ChartsOverlayLayers(GlobalVars vars, int index)
    {
        //startIndex = index;
        this.vars = vars;
        this.charts = new ArrayList<ChartOverlay>();
    }

    public class ChartOverlay
    {
        public ChartOverlay(GlobalVars vars, Chart chart)
        {
            this.vars = vars;
            this.chart = chart;
            setBoundingBox();
        }

        public void SetChart(Chart chart)
        {
            this.chart = chart;
            if (chart.active)
            {
                if (bitmapLayer == null)
                {
                    createSource();
                }
                else
                {
                    if (!vars.map.layers().contains(bitmapLayer))
                    {
                        vars.map.layers().add(bitmapLayer, vars.OVERLAYCHARTS_GROUP);
                        vars.map.updateMap(true);
                    }
                }
            }
            else
            {
                if (bitmapLayer != null)
                {
                    if (vars.map.layers().contains(bitmapLayer))
                    {
                        vars.map.layers().remove(bitmapLayer);
                        vars.map.updateMap(true);
                    }
                }
            }
            updateChart();
        }

        public int getLayerIndex()
        {
            if (bitmapLayer != null)
            {
                return vars.map.layers().indexOf(bitmapLayer);
            }
            else return -1;
        }

        private void updateChart()
        {
            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(vars.context);
            db.getCharts().UpdateChart(chart);
            //SetChart();
        }

        private void createSource()
        {
            switch (chart.type)
            {
                case mbtiles:
                {
                    createMBTilesOverlaySource();
                    break;
                }
                case jpg:
                {
                    createOverlaySource();
                    break;
                }
                case png:
                {
                    createOverlaySource();
                    break;
                }
                case pdf:
                {
                    break;
                }
                case unknown:
                {
                    break;
                }
            }
        }

        private void createMBTilesOverlaySource()
        {
            File f = new File(chart.filelocation);
            if (f.exists()) {
                mbTilesTileSource = new MBTilesTileSource(chart.filelocation);
                mbTilesTileSource.open();
                createBitmapLayer(mbTilesTileSource);
            }
        }

        private void createOverlaySource()
        {
            overlayTileSource = new OverlayTileSource(chart.chart, boundingBox);
            overlayTileSource.open();
            createBitmapLayer(overlayTileSource);
        }

        private void createBitmapLayer(TileSource source)
        {
            bitmapLayer = new BitmapTileLayer(vars.map, source);
            vars.map.layers().add(bitmapLayer, vars.OVERLAYCHARTS_GROUP);
            vars.map.updateMap(true);
        }

        private BoundingBox boundingBox;
        private GlobalVars vars;
//        private GroupLayer groupLayer;
        private Chart chart;
        private OverlayTileSource overlayTileSource;
        private MBTilesTileSource mbTilesTileSource;
        private BitmapTileLayer bitmapLayer;

        private void setBoundingBox()
        {
            if (chart != null)
            {
                if (chart.type != ChartType.mbtiles)
                    boundingBox = new BoundingBox(chart.latitude_deg_s, chart.longitude_deg_w,
                            chart.latitude_deg_n, chart.longitude_deg_e);
            }
        }

        @Override
        public boolean equals (Object object)
        {
            if (object instanceof ChartOverlay )
                return (this.chart.id==((ChartOverlay)object).chart.id);
            else return false;
        }
    }

    private List<ChartOverlay> charts;

    public void setChart(Chart chart)
    {
        ChartOverlay chartOverlay = new ChartOverlay(vars, chart);
        int i = charts.indexOf(chartOverlay);
        if (i<0) {
            charts.add(chartOverlay);
            chartOverlay.SetChart(chart);
        }
        else
        {
            chartOverlay = charts.get(i);
            chartOverlay.SetChart(chart);
        }
    }

    private GlobalVars vars;
    //private int startIndex;


}
