package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkerItem;
import com.mobileaviationtools.nav_fly.Markers.Navaids.NavaidMarkerItem;
import com.mobileaviationtools.nav_fly.R;

import com.mobileaviationtools.extras.Utils.StringFormatter;

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
        TextView legHeadingText = (TextView) rowView.findViewById(R.id.headingText);
        TextView legDistanceText = (TextView) rowView.findViewById(R.id.distanceText);
        TextView timeText = (TextView) rowView.findViewById(R.id.timeText);
        TextView legTotalDistanceText = (TextView) rowView.findViewById(R.id.totalDistanceText);
        TextView legTotalTimeText = (TextView) rowView.findViewById(R.id.totaltTimeText);

        LinearLayout legLayout = (LinearLayout) rowView.findViewById(R.id.legLayout);

        ImageView compasImage = (ImageView) rowView.findViewById(R.id.compassImage);

        Waypoint waypoint = route.get(i);
        waypointNameTextView.setText(waypoint.name);

        if (waypoint.ref != null)
        {
            if (waypoint.ref instanceof Airport)
            {
                Airport a = (Airport)waypoint.ref;
                AirnavDatabase db = AirnavDatabase.getInstance(context);
                a.frequencies = db.getFrequency().getFrequenciesByAirport(a.id);
                if (a.frequencies != null) {
                    if (a.frequencies.size() > 0)
                        waypointLatText.setText(a.frequencies.get(0).frequency_mhz.toString() + " " +
                                a.frequencies.get(0).description);
                    else
                        waypointLatText.setText(StringFormatter.convertLatitude(waypoint.point.getLatitude()));
                    if (a.frequencies.size() > 1)
                        waypointLonText.setText(a.frequencies.get(1).frequency_mhz.toString() + " " +
                                a.frequencies.get(1).description);
                    else
                        waypointLonText.setText(StringFormatter.convertLongitude(waypoint.point.getLongitude()));
                }
                else
                {
                    waypointLatText.setText(StringFormatter.convertLatitude(waypoint.point.getLatitude()));
                    waypointLonText.setText(StringFormatter.convertLongitude(waypoint.point.getLongitude()));
                }
            }
        }
        else {
            waypointLatText.setText(StringFormatter.convertLatitude(waypoint.point.getLatitude()));
            waypointLonText.setText(StringFormatter.convertLongitude(waypoint.point.getLongitude()));
        }

        ImageView icon = (ImageView) rowView.findViewById(R.id.waypointIconImage);
        if (waypoint.type == WaypointType.airport) {
            Airport a = (Airport)waypoint.ref;
            icon.setImageBitmap(AirportMarkerItem.GetMarkerBitmap(a));
        }
        if (waypoint.type == WaypointType.navaid) {
            Navaid a = (Navaid)waypoint.ref;
            icon.setImageBitmap(NavaidMarkerItem.GetMarkerBitmap(a));
        }

        if (i == route.size()-1) legLayout.setVisibility(View.GONE);
        else {
            legLayout.setVisibility(View.VISIBLE);
            Leg l = route.getLeg(i);
            legHeadingText.setText(((Long)Math.round(l.getHeading())).toString()+ "º");
            compasImage.setRotation((float)l.getHeading());
            legDistanceText.setText(((Long)Math.round(l.getDistanceNM())).toString());
            timeText.setText(((Long)Math.round(l.getLegTimeMinutes())).toString());
            legTotalDistanceText.setText((((Long)Math.round(l.getTotalDistanceNm())).toString()));
            legTotalTimeText.setText(((Long)Math.round(l.getTotalTimeMin())).toString());
        }



        return rowView;
    }
}
