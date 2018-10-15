package com.mobileaviationtools.nav_fly.Route.Notams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.weater_notam_data.notams.Notams;

public class NotamsItemAdapter extends BaseAdapter {
    private Notams notams;
    private Context context;

    public NotamsItemAdapter(Context context, Notams notams)
    {
        this.notams = notams;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notams.notamList.length;
    }

    @Override
    public Object getItem(int i) {
        return notams.notamList[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.notam_list_item, viewGroup, false);

        TextView notamsText = (TextView) rowView.findViewById(R.id.notamsItemTxt);
        notamsText.setText(notams.notamList[i].icaoMessage);

        return  rowView;
    }
}
