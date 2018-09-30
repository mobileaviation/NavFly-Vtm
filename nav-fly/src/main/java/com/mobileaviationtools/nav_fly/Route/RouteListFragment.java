package com.mobileaviationtools.nav_fly.Route;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobileaviationtools.nav_fly.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteListFragment extends Fragment {


    public RouteListFragment() {
        // Required empty public constructor
    }

    private Route route;
    private RouteItemAdapter routeItemAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_route_list, container, false);
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
        });
    }

}
