package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Entities.Chart;

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

    public ChartsOverlayLayers(Context content, Map map, int index)
    {
        this.mMap = map;
        this.context = content;
        startIndex = index;
        this.charts = new ArrayList<ChartOverlay>();
    }

    public class ChartOverlay
    {
        public ChartOverlay(Context context, Map map, Chart chart, int index)
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
                    createSource();
                }
                else
                {
                    if (!map.layers().contains(bitmapLayer))
                    {
                        map.layers().add(startIndex, bitmapLayer);
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

        public int getLayerIndex()
        {
            if (bitmapLayer != null)
            {
                return map.layers().indexOf(bitmapLayer);
            }
            else return -1;
        }

        private void updateChart()
        {
            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
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
            bitmapLayer = new BitmapTileLayer(map, source);
            map.layers().add(startIndex, bitmapLayer);
            map.updateMap(true);
        }

        private BoundingBox boundingBox;
        private Context context;
        private Map map;
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
        ChartOverlay chartOverlay = new ChartOverlay(context, mMap, chart, startIndex);
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
    private GroupLayer groupLayer;
    private Context context;
    private int startIndex;


}
