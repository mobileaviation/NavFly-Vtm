package com.mobileaviationtools.nav_fly.Settings.Overlays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;

import java.util.ArrayList;

public class ChartSettingsItemAdapter extends BaseAdapter {
    private ArrayList<MBTileChart> charts;
    private Context context;

    public ChartSettingsItemAdapter(Context context, ArrayList<MBTileChart> charts)
    {
        this.charts = charts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return charts.size();
    }

    @Override
    public Object getItem(int i) {
        return charts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.chartsetting_item, viewGroup, false);

        CheckBox activateChartCheckBox = (CheckBox)view.findViewById(R.id.activateChartCheckBox);
        TextView chartSetupTxt = (TextView)view.findViewById(R.id.chartSetupTxt);
        ImageButton downloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        ProgressBar chartDownloadProgress = (ProgressBar) view.findViewById(R.id.chartDownloadProgress);

        return view;
    }
}
