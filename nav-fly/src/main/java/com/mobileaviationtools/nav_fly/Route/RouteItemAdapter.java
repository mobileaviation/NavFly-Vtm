package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkerItem;
import com.mobileaviationtools.nav_fly.R;

import org.oscim.android.Utils.StringFormatter;

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
        TextView waypointLatText = (TextView) rowView.findViewById(R.id.waypointLatText);
        TextView waypointLonText = (TextView) rowView.findViewById(R.id.waypointLonText);

        Waypoint waypoint = route.get(i);
        waypointNameTextView.setText(waypoint.name);

        waypointLatText.setText(StringFormatter.convertLatitude(waypoint.point.getLatitude()));
        waypointLonText.setText(StringFormatter.convertLongitude(waypoint.point.getLongitude()));

        ImageView icon = (ImageView) rowView.findViewById(R.id.waypointIconImage);
        if (waypoint.type == WaypointType.airport) {
            Airport a = (Airport)waypoint.ref;
            icon.setImageBitmap(AirportMarkerItem.GetMarkerBitmap(a));
        }

        return rowView;
    }
}
