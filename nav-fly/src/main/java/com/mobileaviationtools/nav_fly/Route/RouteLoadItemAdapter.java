package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Entities.Route;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.utils.pool.Inlist;

import java.util.List;

public class RouteLoadItemAdapter extends BaseAdapter {
    public RouteLoadItemAdapter(List<Route> routes, Context context)
    {
        this.context = context;
        this.routes = routes;
    }

    private List<Route> routes;
    private Context context;

    @Override
    public int getCount() {
        return routes.size();
    }

    @Override
    public Object getItem(int i) {
        return routes.get(i);
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
        routeNameText.setText(routes.get(i).name);

        return rowView;
    }
}
