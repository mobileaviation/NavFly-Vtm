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
        public void OnChanged(MBTileChart chart, status newStatus, boolean active);
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
        this.chartType = (chart.mbtile_id>0) ? type.ofm : type.local;
        this.localFilename = chart.filelocation;
        this.localfile = new File(this.localFilename);
        if (localfile.exists())
            chartStatus = (chart.active)? status.visible : status.present;
        else chartStatus = status.gone;
    }
    public Chart getChart(){ return this.chart; }

    private void newChartFromTile()
    {
        this.chart = new Chart();
        this.chart.type = ChartType.mbtiles;
        this.chart.filelocation = this.localFilename;
        this.chart.name = this.localfile.getName();
        this.chart.mbtile_id = tile.id;
        this.chart.id = 0;
        this.chart.active = false;
    }

    private String localFilename;
    private File localfile;
    public String getLocalFilename() { return localFilename; }

    public type chartType;
    public status chartStatus;
    public double progress;

    public String getName()
    {
        if (this.tile != null) return "(" + this.tile.version.toString() + ") "+ this.tile.name;
        if (this.chart != null) return this.chart.name;
        return localfile.getName();
    }

    public void updateChart(boolean active)
    {
        if (this.chart.active != active) {
            this.chart.active = active;
            this.chartStatus = (active) ? status.visible : status.present;
            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
            this.chart.id = db.getCharts().InsertChart(this.chart);
            // TODO make chart visible on, or remove it from the map
            if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChanged(this,
                    this.chartStatus, this.chart.active);
        }
    }

    public void deleteChart()
    {
        if (this.chart.id>0) {
            this.chart.active = false;
            this.chartStatus = status.present;
            if (mbTileChartChangedEvent!= null) mbTileChartChangedEvent.OnChanged(this,
                    this.chartStatus, this.chart.active);

            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
            db.getCharts().DeleteChart(this.chart);
            this.chart.id = 0;
            this.chartStatus = status.gone;
            if (mbTileChartChangedEvent!= null) mbTileChartChangedEvent.OnChanged(this,
                    this.chartStatus, this.chart.active);
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
                if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChanged(MBTileChart.this,
                        status.gone, MBTileChart.this.chart.active);
                // TODO Display download error message
            }

            @Override
            public void OnFinished(String url, File result_file) {
                MBTileChart.this.progress = 100;
                if (mbTileChartChangedEvent != null) mbTileChartChangedEvent.OnChanged(MBTileChart.this,
                        status.present, MBTileChart.this.chart.active);
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
            if (this.chart == null) return false;
            if (((MBTileChart)object).tile==null)
                return (((MBTileChart)object).chart.id == this.chart.id);

            return (((MBTileChart)object).tile.id==
                    this.chart.mbtile_id);
            } else return false;
    }
}
