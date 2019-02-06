package com.mobileaviationtools.nav_fly.Dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;


public class DashboardFragment extends Fragment {
    public DashboardFragment() {
        // Required empty public constructor
    }

    private View view;
    private SpeedType speedType = SpeedType.knots;

    private Float toKnots = 1.94384449f;
    private Float toKm = 3.6f;
    private Float meterToFeet = 3.2808399f; // meters to feet



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        return view;
    }

    public void reloadLayout()
    {
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        ViewGroup viewGroup = (ViewGroup) getView();
        viewGroup.removeAllViewsInLayout();
        view = onCreateView(inflater, viewGroup, null);
        viewGroup.addView(view);
        //this.getFragmentManager().
    }

    private String getZuluTime()
    {
        String DATEFORMAT = "HH:mm"; //"yyyy-MM-dd HH:mm:ss"
        final SimpleDateFormat sdf = new SimpleDateFormat(DATEFORMAT);
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        return sdf.format(new Date());
    }

    private String getEET(Double distanceRemaining, Double speed)
    {
        Double timeMin = (distanceRemaining / speed) * 60;
        Long hours = (long)(timeMin / 60d);
        Long minutes = (long)(timeMin % 60d);
        return String.format("%02d:%02d", hours, minutes);
    }

    public void setLocation(FspLocation location, double presetAirspeed)
    {
        TextView toDestTextView = (TextView) view.findViewById(R.id.toDestTextView);
        TextView coarseTextView = (TextView) view.findViewById(R.id.coarseTextView);
        TextView speedTextView = (TextView) view.findViewById(R.id.speedTextView);
        TextView heightTextView = (TextView) view.findViewById(R.id.heightTextView);
        TextView eetTextView = (TextView) view.findViewById(R.id.eetTextView);



        coarseTextView.setText(String.format("%03d", Math.round(location.getBearing())));
        Float sf = (speedType == SpeedType.knots) ? toKnots : toKm;
        speedTextView.setText(String.format("%03d", Math.round(location.getSpeed() * sf)));
        heightTextView.setText(String.format("%05d", Math.round(location.getAltitude() * meterToFeet)));
        Double d = location.GetDistanceRemaining();
        String toDest =  (d==0) ? "UNK-" : String.format("%04d", Math.round(d));

        toDestTextView.setText(toDest);
        eetTextView.setText(getEET(d, (location.getSpeed()>5) ? location.getSpeed() : presetAirspeed));


    }

    public void setQnh(String[] qnh)
    {
        if (qnh != null) {
            if (qnh.length==2) {
                TextView qnhStationTextView = (TextView) view.findViewById(R.id.qnhStationTextView);
                TextView qnhTextView = (TextView) view.findViewById(R.id.qnhTextView);

                qnhStationTextView.setText(qnh[0]);
                qnhTextView.setText(qnh[1]);
            }
        }
    }

    public void setZuluTime()
    {
        TextView zuluTimeTestView = (TextView) view.findViewById(R.id.zuluTimeTestView);
        zuluTimeTestView.setText(getZuluTime());
    }

}
