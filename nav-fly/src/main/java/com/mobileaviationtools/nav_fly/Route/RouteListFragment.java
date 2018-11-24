package com.mobileaviationtools.nav_fly.Route;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.AirnavRouteDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.nav_fly.MainActivity;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Info.ChartEvents;
import com.mobileaviationtools.nav_fly.Route.Info.InfoLayout;
import com.mobileaviationtools.nav_fly.Route.Notams.NotamsAirportItemAdapter;
import com.mobileaviationtools.nav_fly.Route.Notams.NotamsListLayout;
import com.mobileaviationtools.nav_fly.Route.Weather.WeatherListLayout;
import com.mobileaviationtools.nav_fly.Route.Weather.WeatherStations;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;
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
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class RouteListFragment extends Fragment {
    public RouteListFragment() {
        // Required empty public constructor
    }

    public enum layoutType
    {
        route,
        weather,
        notams,
        info
    }

    private String TAG = "RouteListFragment";

    private Route route;
    public Route getRoute() {return route;}

    private RouteItemAdapter routeItemAdapter;
    private NotamsAirportItemAdapter notamsAirportItemAdapter;

    private ImageButton routeBtn;
    private ImageButton weatherBtn;
    private ImageButton notamsBtn;
    private ImageButton infoBtn;

    private ImageButton routeNewBtn;
    private ImageButton routeOpenBtn;
    private ImageButton routeSaveBtn;

    private LinearLayout routeLayout;
    private NotamsListLayout notamsLayout;
    private WeatherListLayout weatherLayout;
    private InfoLayout infoLayout;

    private ListView airportsList;

    private WeatherStations weatherStations;
    public void setWeatherStations(WeatherStations stations)
    {
        this.weatherStations = stations;
    }

    private Map map;

    public void setMap(Map map) {
        this.map = map;
    }

    private ChartEvents chartEvents;
    public void setChartEvents(ChartEvents chartEvents)
    {
        this.chartEvents = chartEvents;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_route_list, container, false);

        routeBtn = (ImageButton) view.findViewById(R.id.routeTabBtn);
        weatherBtn = (ImageButton) view.findViewById(R.id.weatherTabBtn);
        notamsBtn = (ImageButton) view.findViewById(R.id.notamsTabBtn);
        infoBtn = (ImageButton) view.findViewById(R.id.infoTabBtn);

        routeNewBtn = (ImageButton) view.findViewById(R.id.routeNewBtn);
        routeOpenBtn = (ImageButton) view.findViewById(R.id.routeLoadBtn);
        routeSaveBtn = (ImageButton) view.findViewById(R.id.routeSaveBtn);

        routeLayout = (LinearLayout) view.findViewById(R.id.routeListLayout);
        routeLayout.setVisibility(View.GONE);

        notamsLayout = (NotamsListLayout) view.findViewById(R.id.notamsListLayout);
        notamsLayout.setVisibility(View.GONE);
        notamsLayout.init(getContext(), getActivity());

        weatherLayout = (WeatherListLayout) view.findViewById(R.id.weatherListLayout);
        weatherLayout.setVisibility(View.GONE);
        weatherLayout.init(getContext());

        infoLayout = (InfoLayout) view.findViewById(R.id.infoLayout);
        infoLayout.init(getContext(), getActivity());
        infoLayout.setVisibility(View.GONE);
        setupInfoLayoutEvents();

        setWeatherBtnOnClick();
        setNotamBtnOnClick();
        setRouteBtnOnClick();
        setInfoBtnOnClick();
        setRouteFileBtnSClickEvents();

        return view;
    }

    private void SetRoute()
    {
        ListView routeListView = (ListView) this.getView().findViewById(R.id.routeListView);
        routeItemAdapter = new RouteItemAdapter(route, this.getContext());
        setupRouteEvents(route);
        routeListView.setAdapter(routeItemAdapter);
        setLayoutVisiblity(layoutType.route, true);
    }

    public void InvalidateRouteList()
    {
        routeItemAdapter.notifyDataSetChanged();
    }

    public void ShowAirportInfo(Airport airport)
    {
        setLayoutVisiblity(layoutType.info, true);
        infoLayout.setMap(map);
        infoLayout.setRoute(route);
        infoLayout.ShowAirportInfo(airport);
    }


    private void setupRouteEvents(Route route)
    {
        route.setRouteEvents(new RouteEvents() {
            @Override
            public void NewWaypointInserted(Route route, Waypoint newWaypoint) {
                RouteListFragment.this.InvalidateRouteList();
            }

            @Override
            public void NewRouteCreated(Route route) {
                RouteListFragment.this.SetRoute();
            }
            @Override
            public void WaypointUpdated(Route route, Waypoint updatedWaypoint)
            {
                RouteListFragment.this.InvalidateRouteList();
            }
            @Override
            public void RouteUpdated(Route route)
            {
                RouteListFragment.this.InvalidateRouteList();
            }

        });
    }

    private boolean setLayoutVisiblity(layoutType layout, Boolean overrideGone)
    {
        switch (layout)
        {
            case route:{
                routeLayout.setVisibility((routeLayout.getVisibility()==View.VISIBLE) ?
                        ((overrideGone) ? View.VISIBLE : View.GONE) : View.VISIBLE);
                weatherLayout.setVisibility(View.GONE);
                notamsLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                return (routeLayout.getVisibility()==View.VISIBLE);
            }
            case notams:{
                notamsLayout.setVisibility((notamsLayout.getVisibility()==View.VISIBLE) ?
                        ((overrideGone) ? View.VISIBLE : View.GONE) : View.VISIBLE);
                weatherLayout.setVisibility(View.GONE);
                routeLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                return (notamsLayout.getVisibility()==View.VISIBLE);
            }
            case weather:
            {
                weatherLayout.setVisibility((weatherLayout.getVisibility()==View.VISIBLE) ?
                        ((overrideGone) ? View.VISIBLE : View.GONE) : View.VISIBLE);
                routeLayout.setVisibility(View.GONE);
                notamsLayout.setVisibility(View.GONE);
                infoLayout.setVisibility(View.GONE);
                return (weatherLayout.getVisibility()==View.VISIBLE);
            }
            case info:
            {
                infoLayout.setVisibility((infoLayout.getVisibility()==View.VISIBLE) ?
                        ((overrideGone) ? View.VISIBLE : View.GONE) : View.VISIBLE);
                routeLayout.setVisibility(View.GONE);
                notamsLayout.setVisibility(View.GONE);
                weatherLayout.setVisibility(View.GONE);
                return (infoLayout.getVisibility()==View.VISIBLE);
            }
        }
        return false;
    }

    private void setRouteBtnOnClick()
    {
        routeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setLayoutVisiblity(layoutType.route, false);
            }
        });
    }

    private void setWeatherBtnOnClick()
    {
        weatherBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setLayoutVisiblity(layoutType.weather, false)){
                    weatherLayout.setMap(map);
                    weatherLayout.setWeatherData(weatherStations);
                }
            }
        });
    }


    private void setNotamBtnOnClick()
    {
        notamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setLayoutVisiblity(layoutType.notams, false)) {
                    notamsLayout.setMap(map);
                    notamsLayout.notamBtnClick();
                }
            }
        });
    }

    private void setInfoBtnOnClick()
    {
        infoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (setLayoutVisiblity(layoutType.info, false)){
                    infoLayout.setMap(map);
                    infoLayout.setRoute(route);
                    infoLayout.LoadList();
                }
            }
        });
    }

    private void setupInfoLayoutEvents()
    {
        infoLayout.setChartEvent(new ChartEvents() {
            @Override
            public void OnChartCheckedEvent(Chart chart, Boolean checked) {
                if (chartEvents != null) chartEvents.OnChartCheckedEvent(chart, checked);
            }

            @Override
            public void OnChartSelected(Chart chart) {
                if (chartEvents != null) chartEvents.OnChartSelected(chart);
            }
        });
    }

    public void setupNewRoute()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Start new route..")
                .setMessage("Start a new route by selecting the departure and destination airport!")
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        route = new Route("Route: " + new Date().toString(), getActivity());
                        SetRoute();
                    }
                })                 //Do nothing on no
                .show();
    }

    private void setRouteFileBtnSClickEvents()
    {
        routeNewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (route != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Clear route..")
                            .setMessage("Are you sure you want to close the current route?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    route.ClearRoute(map);
                                    setupNewRoute();
                                }
                            })
                            .setNegativeButton("No", null)                        //Do nothing on no
                            .show();
                }
                else {
                    setupNewRoute();
                }
            }
        });

        routeOpenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Open Route");
                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.list_input, (ViewGroup) getView(), false);
                final ListView routesList = (ListView) viewInflated.findViewById(R.id.list);
                AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(getContext());
                final List<com.mobileaviationtools.airnavdata.Entities.Route> routes = db.getRoute().getAllRoutes();
                RouteLoadItemAdapter routeLoadItemAdapter = new RouteLoadItemAdapter(routes, getContext());
                routesList.setAdapter(routeLoadItemAdapter);
                routesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        routesList.setItemChecked(i, true);
                    }
                });
                builder.setView(viewInflated);
                builder.setIcon(android.R.drawable.ic_input_get);
                builder.setMessage("Select Route to load!");
                builder.setPositiveButton("Open", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        int pos = routesList.getCheckedItemPosition();
                        com.mobileaviationtools.airnavdata.Entities.Route selectedRoute =
                                routes.get(pos);

                        dialogInterface.dismiss();
                        if (selectedRoute != null) {
                            if (route != null) route.ClearRoute(map);
                            route = new Route(selectedRoute.name, getActivity());

                            route.createdDate = new Date(selectedRoute.createdDate);
                            route.modifiedDate = new Date(selectedRoute.modifiedDate);
                            SetRoute();
                            route.openRoute(selectedRoute.id, map);
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                builder.show();

            }
        });

        routeSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (route != null)
                {
                    saveRouteDialogs();
                }
            }
        });
    }

    private void saveRouteDialogs()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Save Route");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.text_input, (ViewGroup) getView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setText(route.name);
        builder.setView(viewInflated);
        builder.setIcon(android.R.drawable.ic_input_get);
        builder.setMessage("Save this route with name:?");
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialogInterface, int i) {
                final String routeName = input.getText().toString();
                final AirnavRouteDatabase db = AirnavRouteDatabase.getInstance(getContext());
                final com.mobileaviationtools.airnavdata.Entities.Route r = db.getRoute().getRouteByName(routeName);
                if (r == null) {
                    dialogInterface.dismiss();
                    route.saveRoute(routeName);
                } else
                {
                    AlertDialog.Builder ov_builder = new AlertDialog.Builder(getContext());
                    ov_builder.setTitle("Overwrite route?");
                    ov_builder.setMessage("There is already a route with this name in the database..");
                    ov_builder.setIcon(android.R.drawable.ic_dialog_alert);
                    ov_builder.setPositiveButton("Overwrite", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface ov_dialogInterface, int i) {
                            ov_dialogInterface.dismiss();
                            dialogInterface.dismiss();
                            db.getWaypoint().DeleteWaypointsByRouteID(r.id);
                            db.getRoute().DeleteRoute(r);
                            route.saveRoute(routeName);
                        }
                    });
                    ov_builder.setNegativeButton("Rename", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface ov_dialogInterface, int i) {
                            ov_dialogInterface.cancel();
                            dialogInterface.cancel();
                            saveRouteDialogs();
                        }
                    });
                    ov_builder.show();

                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
