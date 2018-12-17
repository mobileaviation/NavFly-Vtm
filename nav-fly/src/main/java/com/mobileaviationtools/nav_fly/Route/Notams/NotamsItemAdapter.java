package com.mobileaviationtools.nav_fly.Route.Notams;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Entities.Notam;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.weater_notam_data.notams.Notams;

public class NotamsItemAdapter extends BaseAdapter {
    private Notam[] notams;
    private Context context;

    public NotamsItemAdapter(Context context, Notam[] notams)
    {
        this.notams = notams;
        this.context = context;
    }

    @Override
    public int getCount() {
        return notams.length;
    }

    @Override
    public Object getItem(int i) {
        return notams[i];
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
        TextView notamsNumberText = (TextView) rowView.findViewById(R.id.notamNumberText);
        TextView notamsEndDateText = (TextView) rowView.findViewById(R.id.notamDataEndText);


        notamsText.setText(notams[i].traditionalMessageFrom4thWord);
        notamsEndDateText.setText(notams[i].endDate);
        notamsNumberText.setText(notams[i].notamNumber);

        return  rowView;
    }
}
