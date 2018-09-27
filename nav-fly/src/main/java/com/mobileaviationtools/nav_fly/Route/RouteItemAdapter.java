package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;

public class RouteItemAdapter extends BaseAdapter {
    private Route route;
    private Context context;

    public RouteItemAdapter(Route route, Context context)
    {
        this.context = context;
        this.route = route;
    }

    @Override
    public int getCount() {
        return route.size();
    }

    @Override
    public Waypoint getItem(int i) {
        return route.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.route_list_item, viewGroup, false);

        TextView waypointNameTextView = (TextView) rowView.findViewById(R.id.waypointNameText);
        TextView legNameTextView = (TextView) rowView.findViewById(R.id.legNameText);

        Waypoint waypoint = route.get(i);
        waypointNameTextView.setText(waypoint.name);

        return rowView;
    }
}
