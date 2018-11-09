package com.mobileaviationtools.nav_fly.Settings.Overlays;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.MBTile;
import com.mobileaviationtools.nav_fly.Classes.FileDownloader;

import java.io.File;

public class MBTileChart {
    public interface MBTileChartChangedEvent
    {
        public void OnChangeInProgress(MBTileChart chart);
        public void OnChanged(MBTileChart chart, status newStatus);
    }

    public MBTileChart(Context context)
    {
        this.context = context;
        baseParh = this.context.getApplicationInfo().dataDir + "/charts/";
    }

    private Context context;
    private String baseParh;
    private MBTileChartChangedEvent mbTileChartChangedEvent;
    public void SetMBTileChartChangedEvent (MBTileChartChangedEvent event)
    {
        mbTileChartChangedEvent = event;
    }

    public enum type
    {
        ofm,
        fsp,
        local
    }

    public enum status
    {
        gone,
        downloading,
        present,
        visible
    }


    private MBTile tile;
    public void setTile(MBTile tile)
    {
        this.tile = tile;
        this.chartType = type.ofm;
        File f = new File(Uri.parse(tile.mbtileslink).getPath());
        localFilename = baseParh + f.getName();
        this.localfile = new File(this.localFilename);
        chartStatus = status.gone;
        newChartFromTile();
    }

    private Chart chart;
    public void setChart(Chart chart)
    {
        this.chart = chart;
        this.chartType = type.local;
        this.localFilename = chart.filelocation;
        this.localfile = new File(this.localFilename);
        if (localfile.exists())
            chartStatus = (chart.active)? status.visible : status.present;
        else chartStatus = status.gone;
    }

    private void newChartFromTile()
    {
        this.chart = new Chart();
        this.chart.type = ChartType.mbtiles;
        this.chart.filelocation = this.localFilename;
        this.chart.name = this.localfile.getName();
        this.chart.mbtile_id = tile.id;
        this.chart.id = 0;
    }

    private String localFilename;
    private File localfile;
    public String getLocalFilename() { return localFilename; }

    public type chartType;
    public status chartStatus;
    public double progress;

    public String getName()
    {
        if (this.chart != null) return this.chart.name;
        if (this.tile != null) return this.tile.name;
        return localfile.getName();
    }

    public void updateChart(boolean active)
    {
        this.chart.active = active;
        AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
        this.chart.id = db.getCharts().InsertChart(this.chart);

        // TODO make chart visible on, or remove it from the map
    }

    public void deleteChart()
    {
        if (this.chart.id>0) {
            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
            db.getCharts().DeleteChart(this.chart);
            // TODO Remove chart from the map
        }
    }

    public void startDownload()
    {
        FileDownloader downloader = new FileDownloader(context);
        File D = new File(baseParh);
        if (!D.exists()) D.mkdir();

        downloader.SetLocaDir(baseParh);
        downloader.SetUrl(this.tile.mbtileslink);
        downloader.SetOnDownloadInfo(new FileDownloader.DownloadInfo() {
            @Override
            public void OnProgress(String url, File result_file, Integer progress) {
                MBTileChart.this.progress = progress;
                MBTileChart.this.chartStatus = status.downloading;
                if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChangeInProgress(MBTileChart.this);
            }

            @Override
            public void OnError(String url, File result_file, String message) {
                MBTileChart.this.progress = 0;
                if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChanged(MBTileChart.this, status.gone);
                // TODO Display download error message
            }

            @Override
            public void OnFinished(String url, File result_file) {
                MBTileChart.this.progress = 100;
                if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChanged(MBTileChart.this, status.present);
            }
        });

        downloader.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        MBTileChart.this.progress = 0;
        MBTileChart.this.chartStatus = status.downloading;
        if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChangeInProgress(MBTileChart.this);
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MBTileChart) {
            if (((MBTileChart)object).tile==null) return false;
            if (this.chart == null) return false;

            return (((MBTileChart)object).tile.id==
                    this.chart.mbtile_id);
            } else return false;
    }
}
