package com.mobileaviationtools.nav_fly.Settings.Overlays;

import android.content.Context;
import android.net.Uri;

import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.MBTile;

import java.io.File;

public class MBTileChart {
    public MBTileChart(Context context)
    {
        this.context = context;
        baseParh = this.context.getApplicationInfo().dataDir + "/charts/";
    }

    private Context context;
    private String baseParh;

    public enum type
    {
        ofm,
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
        chartStatus = status.gone;
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

    private String localFilename;
    private File localfile;
    public String getLocalFilename() { return localFilename; }

    public type chartType;
    public status chartStatus;
    public double progress;

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof MBTileChart) {
            return ((MBTileChart)object).tile.mbtileslink.equals(
                    this.tile.mbtileslink);
            } else return false;
    }
}
