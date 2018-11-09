package com.mobileaviationtools.nav_fly.Settings.Overlays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Settings.SettingsObject;

import java.util.ArrayList;
import java.util.EventListener;

public class ChartSettingsItemAdapter extends BaseAdapter {

    private SettingsObject settingsObject;
    private Context context;
    private EventListener eventListener;
    public void setEventListener(EventListener eventListener)
    {
        this.eventListener = eventListener;
    }

    public ChartSettingsItemAdapter(Context context, SettingsObject settingsObject)
    {
        this.settingsObject = settingsObject;
        this.settingsObject.chartSettingsItemAdapter = this;
        this.context = context;
    }

    @Override
    public int getCount() {
        return settingsObject.getMbTileCharts().size();
    }

    @Override
    public Object getItem(int i) {
        return settingsObject.getMbTileCharts().get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        view = inflater.inflate(R.layout.chartsetting_item, viewGroup, false);

        ImageView chartSetupImage = (ImageView)view.findViewById(R.id.chartSetupImage);
        CheckBox activateChartCheckBox = (CheckBox)view.findViewById(R.id.activateChartCheckBox);
        TextView chartSetupTxt = (TextView)view.findViewById(R.id.chartSetupTxt);
        ImageButton downloadChartButton = (ImageButton) view.findViewById(R.id.downloadChartButton);
        ProgressBar chartDownloadProgress = (ProgressBar) view.findViewById(R.id.chartDownloadProgress);
        chartDownloadProgress.setMax(100);

        MBTileChart chart = settingsObject.getMbTileCharts().get(i);
        activateChartCheckBox.setTag(chart);
        downloadChartButton.setTag(chart);

        chartSetupTxt.setText(chart.getName());

        switch (chart.chartType)
        {
            case fsp:{
                chartSetupImage.setImageDrawable(context.getResources().getDrawable(R.drawable.fsp_charts_header));
                break;
            }
            case ofm:{
                chartSetupImage.setImageDrawable(context.getResources().getDrawable(R.drawable.ofm_charts_header));
                break;
            }
            case local:
            {
                chartSetupImage.setImageDrawable(context.getResources().getDrawable(R.drawable.local_charts_header));
                break;
            }
        }

        setupListeners(activateChartCheckBox, downloadChartButton);

        switch (chart.chartStatus){
            case gone:
            {
                chartDownloadProgress.setVisibility(View.GONE);
                downloadChartButton.setVisibility(View.VISIBLE);
                downloadChartButton.setImageDrawable(context.getResources().getDrawable(R.drawable.download_btn));
                downloadChartButton.setEnabled(true);
                activateChartCheckBox.setEnabled(false);
                activateChartCheckBox.setChecked(false);
                activateChartCheckBox.setBackgroundColor(context.getResources().getColor(R.color.secundaryColorB1));
                break;
            }
            case present:
            {
                chartDownloadProgress.setVisibility(View.GONE);
                downloadChartButton.setVisibility(View.VISIBLE);
                downloadChartButton.setImageDrawable(context.getResources().getDrawable(R.drawable.delete_download_btn));
                downloadChartButton.setEnabled(true);
                activateChartCheckBox.setEnabled(true);
                activateChartCheckBox.setChecked(false);
                activateChartCheckBox.setBackgroundColor(context.getResources().getColor(R.color.secundaryColorA1T));
                break;
            }
            case visible:
            {
                chartDownloadProgress.setVisibility(View.GONE);
                downloadChartButton.setVisibility(View.VISIBLE);
                downloadChartButton.setImageDrawable(context.getResources().getDrawable(R.drawable.delete_download_btn));
                downloadChartButton.setEnabled(true);
                activateChartCheckBox.setEnabled(true);
                activateChartCheckBox.setChecked(true);
                activateChartCheckBox.setBackgroundColor(context.getResources().getColor(R.color.secundaryColorA1T));
                break;
            }
            case downloading:
            {
                chartDownloadProgress.setVisibility(View.VISIBLE);
                downloadChartButton.setVisibility(View.GONE);
                chartDownloadProgress.setProgress((int)Math.round(chart.progress));
                downloadChartButton.setImageDrawable(context.getResources().getDrawable(R.drawable.delete_download_btn));
                downloadChartButton.setEnabled(false);
                activateChartCheckBox.setEnabled(false);
                activateChartCheckBox.setChecked(false);
                activateChartCheckBox.setBackgroundColor(context.getResources().getColor(R.color.secundaryColorB1));
                break;
            }

        }

        return view;
    }

    private void setupListeners(CheckBox activateCheckBox, ImageButton actionBtn)
    {
        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageButton imageButton = (ImageButton)view;
                MBTileChart chart = (MBTileChart)imageButton.getTag();
                if (chart.chartStatus==MBTileChart.status.gone) {
                    chart.startDownload();
                    ChartSettingsItemAdapter.this.notifyDataSetChanged();
                }
                if((chart.chartStatus==MBTileChart.status.present) || (chart.chartStatus==MBTileChart.status.visible))
                {
                    chart.deleteChart();
                    chart.chartStatus = MBTileChart.status.gone;
                    ChartSettingsItemAdapter.this.notifyDataSetChanged();

                }
            }
        });

        activateCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CheckBox checkBox = (CheckBox)compoundButton;
                checkBox.setChecked(b);
                MBTileChart chart = (MBTileChart)checkBox.getTag();
                chart.updateChart(b);
                ChartSettingsItemAdapter.this.notifyDataSetChanged();
            }
        });
    }
}
