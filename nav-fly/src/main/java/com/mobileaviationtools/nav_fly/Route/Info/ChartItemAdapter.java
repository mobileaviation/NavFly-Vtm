package com.mobileaviationtools.nav_fly.Route.Info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.nav_fly.R;

public class ChartItemAdapter extends BaseAdapter {
    public ChartItemAdapter(Context context, Chart[] charts)
    {
        this.charts = charts;
        this.context = context;
    }

    private ChartEvents chartCheckedEvent;
    public void setChartCheckedEvent(ChartEvents chartCheckedEvent)
    {
        this.chartCheckedEvent = chartCheckedEvent;
    }

    private Context context;
    private Chart[] charts;

    @Override
    public int getCount() {
        return charts.length;
    }

    @Override
    public Object getItem(int i) {
        return charts[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.chart_list_item, viewGroup, false);

        CheckBox chartCheckbox = (CheckBox) rowView.findViewById(R.id.chartActiveCheckbox);
        chartCheckbox.setText(charts[i].name);
        chartCheckbox.setTag(charts[i]);
        chartCheckbox.setChecked(charts[i].active);

        chartCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                CheckBox cb = (CheckBox) compoundButton;
                Chart ch = (Chart) cb.getTag();
                ch.active = b;
                if (chartCheckedEvent != null) chartCheckedEvent.OnChartCheckedEvent(ch, b);
            }
        });

        return rowView;
    }
}
