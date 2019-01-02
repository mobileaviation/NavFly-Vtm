package com.mobileaviationtools.nav_fly.Tracks;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Entities.TrackLog;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import java.util.List;

public class TrackLogAdapter extends BaseAdapter {
    private List<TrackLog> logs;
    private GlobalVars vars;

    public TrackLogAdapter(List<TrackLog> logs, GlobalVars vars)
    {
        this.logs = logs;
        this.vars = vars;
    }

    @Override
    public int getCount() {
        return logs.size();
    }

    @Override
    public Object getItem(int position) {
        return logs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) vars.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_input_text_item, parent, false);

        TextView routeNameText = (TextView)rowView.findViewById(R.id.itemText);
        routeNameText.setText(logs.get(position).name);

        return rowView;
    }
}
