package com.mobileaviationtools.nav_fly.Dashboard;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.R;

import org.w3c.dom.Text;


public class DashboardFragment extends Fragment {
    public DashboardFragment() {
        // Required empty public constructor
    }

    private View view;

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

    public void setLocation(FspLocation location)
    {
        TextView toDestTextView = (TextView) view.findViewById(R.id.toDestTextView);
        TextView coarseTextView = (TextView) view.findViewById(R.id.coarseTextView);
        TextView speedTextView = (TextView) view.findViewById(R.id.speedTextView);
        TextView heightTextView = (TextView) view.findViewById(R.id.heightTextView);
        TextView qnhStationTextView = (TextView) view.findViewById(R.id.qnhStationTextView);
        TextView eetTextView = (TextView) view.findViewById(R.id.eetTextView);
        TextView zuluTimeTestView = (TextView) view.findViewById(R.id.zuluTimeTestView);

    }

}
