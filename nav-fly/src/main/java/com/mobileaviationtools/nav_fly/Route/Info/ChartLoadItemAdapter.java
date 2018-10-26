package com.mobileaviationtools.nav_fly.Route.Info;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Entities.Route;
import com.mobileaviationtools.nav_fly.R;

import java.io.File;
import java.util.List;

public class ChartLoadItemAdapter extends BaseAdapter {
    public ChartLoadItemAdapter(File[] files, Context context)
    {
        this.context = context;
        this.files = files;
    }

    private File[] files;
    private Context context;

    @Override
    public int getCount() {
        return files.length;
    }

    @Override
    public Object getItem(int i) {
        return files[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_input_text_item, viewGroup, false);

        TextView routeNameText = (TextView)rowView.findViewById(R.id.itemText);
        routeNameText.setText(files[i].getName());

        return rowView;
    }
}
