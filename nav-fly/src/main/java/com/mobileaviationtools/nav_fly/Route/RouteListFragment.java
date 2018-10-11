package com.mobileaviationtools.nav_fly.Route;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.weater_notam_data.notams.NotamResponseEvent;
import com.mobileaviationtools.weater_notam_data.notams.Notams;
import com.mobileaviationtools.weater_notam_data.services.NotamService;
import com.mobileaviationtools.weater_notam_data.services.WeatherResponseEvent;
import com.mobileaviationtools.weater_notam_data.services.WeatherServices;
import com.mobileaviationtools.weater_notam_data.weather.Metar;
import com.mobileaviationtools.weater_notam_data.weather.Taf;

import org.oscim.core.MapPosition;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteListFragment extends Fragment {
    public RouteListFragment() {
        // Required empty public constructor
    }

    private String TAG = "RouteListFragment";

    private Route route;
    private RouteItemAdapter routeItemAdapter;

    private ImageButton routeBtn;
    private ImageButton weatherBtn;
    private ImageButton notamsBtn;

    private Map map;

    public void setMap(Map map) {
        this.map = map;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_list, container, false);

        routeBtn = (ImageButton) view.findViewById(R.id.routeTabBtn);
        weatherBtn = (ImageButton) view.findViewById(R.id.weatherTabBtn);
        notamsBtn = (ImageButton) view.findViewById(R.id.notamsTabBtn);

        setWeatherBtnOnClick();
        setNotamBtnOnClick();

        return view;
    }

    public void SetRoute(Route route)
    {
        this.route = route;
        ListView routeListView = (ListView) this.getView().findViewById(R.id.routeListView);
        routeItemAdapter = new RouteItemAdapter(route, this.getContext());
        setupRouteEvents(route);
        routeListView.setAdapter(routeItemAdapter);
    }

    public void InvalidateList()
    {
        routeItemAdapter.notifyDataSetChanged();
    }


    private void setupRouteEvents(Route route)
    {
        route.setRouteEvents(new RouteEvents() {
            @Override
            public void NewWaypointInserted(Route route, Waypoint newWaypoint) {
                RouteListFragment.this.InvalidateList();
            }

            @Override
            public void NewRouteCreated(Route route) {
                RouteListFragment.this.SetRoute(route);
            }
            @Override
            public void WaypointUpdated(Route route, Waypoint updatedWaypoint)
            {
                RouteListFragment.this.InvalidateList();
            }
            @Override
            public void RouteUpdated(Route route)
            {
                RouteListFragment.this.InvalidateList();
            }

        });
    }

    private void setWeatherBtnOnClick()
    {
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapPosition pos = map.getMapPosition();
                WeatherServices weatherServices = new WeatherServices();
                weatherServices.GetTafsByLocationAndRadius(pos.getGeoPoint(), 100l,
                        new WeatherResponseEvent() {
                            @Override
                            public void OnMetarsResponse(List<Metar> metars, String message) {
                                Log.i(TAG, message);
                            }

                            @Override
                            public void OnTafsResponse(List<Taf> tafs, String message) {
                                Log.i(TAG, message);
                            }

                            @Override
                            public void OnFailure(String message) {
                                Log.i(TAG, "Failure: " + message);
                            }
                        });
            }
        });
    }

    private void setNotamBtnOnClick()
    {
        notamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapPosition pos = map.getMapPosition();
                NotamService notamService = new NotamService();
                notamService.GetNotamsByLocationAndRadius(pos.getGeoPoint(), 100l, new NotamResponseEvent() {
                    @Override
                    public void OnNotamsResponse(Notams notams, String message) {
                        Log.i(TAG, message);
                    }

                    @Override
                    public void OnFailure(String message) {
                        Log.i(TAG, "Failure: " + message);
                    }
                });
            }
        });
    }

}
