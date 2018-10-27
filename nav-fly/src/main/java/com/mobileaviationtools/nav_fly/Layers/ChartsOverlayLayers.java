package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.Entities.Chart;

import org.oscim.android.tiling.Overlay.OverlayTileSource;
import org.oscim.core.BoundingBox;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.map.Map;
import org.oscim.utils.pool.Inlist;

import java.util.ArrayList;
import java.util.List;

public class ChartsOverlayLayers {

    public ChartsOverlayLayers(Context content, Map map)
    {
        this.mMap = map;
        this.context = content;
        this.charts = new ArrayList<ChartOverlay>();
    }

    public void InitChartsFromDB()
    {
        AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
        Chart[] dbCharts = db.getCharts().getActiveCharts(true);
        for (Chart c : dbCharts)
        {
            setChart(c);
        }
    }

    public class ChartOverlay
    {
        public ChartOverlay(Context context, Map map, Chart chart)
        {
            this.context = context;
            this.map = map;
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
                    createOverlayLayer();
                }
                else
                {
                    if (!map.layers().contains(bitmapLayer))
                    {
                        map.layers().add(bitmapLayer);
                        map.updateMap(true);
                    }
                }
            }
            else
            {
                if (bitmapLayer != null)
                {
                    if (map.layers().contains(bitmapLayer))
                    {
                        map.layers().remove(bitmapLayer);
                        map.updateMap(true);
                    }
                }
            }
            updateChart();
        }

        private void updateChart()
        {
            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
            db.getCharts().UpdateChart(chart);
            //SetChart();
        }

        private void createOverlayLayer()
        {
            overlayTileSource = new OverlayTileSource(chart.chart, boundingBox);
            overlayTileSource.open();
            bitmapLayer = new BitmapTileLayer(map, overlayTileSource);
            map.layers().add(bitmapLayer);
            map.updateMap(true);
        }

        private BoundingBox boundingBox;
        private Context context;
        private Map map;
        private Chart chart;
        private OverlayTileSource overlayTileSource;
        private BitmapTileLayer bitmapLayer;

        private void setBoundingBox()
        {
            if (chart != null)
            {
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
        ChartOverlay chartOverlay = new ChartOverlay(context, mMap, chart);
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

    private Map mMap;
    private Context context;


}
