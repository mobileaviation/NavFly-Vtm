package com.mobileaviationtools.nav_fly.Route;


import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.transition.Visibility;
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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mobileaviationtools.airnavdata.AirnavRouteDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.nav_fly.GlobalVars;
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

import org.oscim.core.GeoPoint;
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

//    private Route route;
//    public Route getRoute() {return route;}

    private RouteItemAdapter routeItemAdapter;
    private NotamsAirportItemAdapter notamsAirportItemAdapter;

    private ImageButton routeBtn;
    private ImageButton weatherBtn;
    public Boolean weatherBtnEnabled = false;
    private ImageButton notamsBtn;
    public Boolean notamsBtnEnabled = true;
    private ImageButton infoBtn;

    private ImageButton routeNewBtn;
    private ImageButton routeOpenBtn;
    private ImageButton routeSaveBtn;
    private ImageButton routeEditBtn;

    private ProgressBar weatherProgressBar;
    private ProgressBar notamsProgressBar;

    private LinearLayout routeLayout;
    private NotamsListLayout notamsLayout;
    private WeatherListLayout weatherLayout;
    private InfoLayout infoLayout;

    private ListView airportsList;
    private RouteEvents routeEvents;

    private WeatherStations weatherStations;
    public void setWeatherStations(GlobalVars vars, WeatherStations stations)
    {
        this.vars = vars;
        weatherBtnEnabled = true;
        this.weatherStations = stations;
        //weatherLayout.setMap(vars.map);
        weatherLayout.setWeatherData(vars, weatherStations);
    }

    public void ToggleWeatherProgressVisibility(final Boolean visible)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (weatherProgressBar != null) weatherProgressBar.setVisibility(
                        (visible) ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public void ToggleNotamsProgressVisibility(final Boolean visible)
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (notamsProgressBar != null) notamsProgressBar.setVisibility(
                        (visible) ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    public NotamResponseEvent getNotamResponseEvent()
    {
        return notamsLayout.getNotamResponseEvent();
    }

//    private Map map;
//
//    public void setMap(Map map) {
//        this.map = map;
//    }

    private GlobalVars vars;
    public void setGlobalVars(GlobalVars vars)
    {
        this.vars = vars;
    }

    public void setRouteEvents(RouteEvents routeEvents)
    {
        this.routeEvents = routeEvents;
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

        weatherProgressBar = (ProgressBar) view.findViewById(R.id.weatherProgressBar);
        notamsProgressBar = (ProgressBar) view.findViewById(R.id.notamsProgressBar);

        routeNewBtn = (ImageButton) view.findViewById(R.id.routeNewBtn);
        routeOpenBtn = (ImageButton) view.findViewById(R.id.routeLoadBtn);
        routeSaveBtn = (ImageButton) view.findViewById(R.id.routeSaveBtn);
        routeEditBtn = (ImageButton) view.findViewById(R.id.routeEditBtn);

        routeLayout = (LinearLayout) view.findViewById(R.id.routeListLayout);
        routeLayout.setVisibility(View.GONE);

        notamsLayout = (NotamsListLayout) view.findViewById(R.id.notamsListLayout);
        notamsLayout.setVisibility(View.GONE);
        notamsLayout.init(getContext(), getActivity(), notamsProgressBar);

        weatherLayout = (WeatherListLayout) view.findViewById(R.id.weatherListLayout);
        weatherLayout.setVisibility(View.GONE);
        weatherLayout.init(vars, weatherProgressBar);

        infoLayout = (InfoLayout) view.findViewById(R.id.infoLayout);
        infoLayout.init();
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
        routeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                RouteItemAdapter adapter = (RouteItemAdapter)adapterView.getAdapter();
                Waypoint waypoint = adapter.getItem(i);
                MapPosition position = vars.map.getMapPosition();
                position.setPosition(waypoint.point);
                vars.map.setMapPosition(position);
                vars.doDeviationLineFromLocation.setGeopoint(waypoint.point);
            }
        });
        routeItemAdapter = new RouteItemAdapter(vars.route, this.getContext());
        setupRouteEvents(vars.route);
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
        infoLayout.setGlobalVars(vars);
        infoLayout.ShowAirportInfo(airport);
    }


    private void setupRouteEvents(Route route)
    {
        route.setRouteEvents(new RouteEvents() {
            @Override
            public void NewWaypointInserted(Route route, Waypoint newWaypoint) {
                RouteListFragment.this.InvalidateRouteList();
                if (routeEvents != null) routeEvents.NewWaypointInserted(route, newWaypoint);
            }

            @Override
            public void NewRouteCreated(Route route) {
                RouteListFragment.this.SetRoute();
                if (routeEvents != null) routeEvents.NewRouteCreated(route);
            }
            @Override
            public void WaypointUpdated(Route route, Waypoint updatedWaypoint)
            {
                RouteListFragment.this.InvalidateRouteList();
                if (routeEvents != null) routeEvents.WaypointUpdated(route, updatedWaypoint);
            }
            @Override
            public void RouteUpdated(Route route) {
                RouteListFragment.this.InvalidateRouteList();
                if (routeEvents != null) routeEvents.RouteUpdated(route);
            }

            @Override
            public void RouteOpened(Route route) {
                RouteListFragment.this.InvalidateRouteList();
                if (routeEvents != null) routeEvents.RouteOpened(route);
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
                if (weatherBtnEnabled) {
                    if (setLayoutVisiblity(layoutType.weather, false)) {
//                        weatherLayout.setMap(map);
//                        weatherLayout.setWeatherData(weatherStations);
                    }
                }
            }
        });
    }


    private void setNotamBtnOnClick()
    {
        notamsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notamsBtnEnabled) {
                    if (setLayoutVisiblity(layoutType.notams, false)) {
                        notamsLayout.setGlobalVars(vars);
                        notamsLayout.notamBtnClick();
                    }
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
                    infoLayout.setGlobalVars(vars);
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
                        vars.route = new Route("Route: " + new Date().toString(), vars);
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
                if (vars.route != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Clear route..")
                            .setMessage("Are you sure you want to close the current route?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    vars.route.ClearRoute(vars.map);
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

        routeEditBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vars.route != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle("Edit Route");
                    View inflatedView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_route_settings, (ViewGroup) getView(), false);
                    final EditText proposedRouteAltitideEditBox = inflatedView.findViewById(R.id.proposedRouteAltitideEditBox);
                    final EditText windSpeedText = inflatedView.findViewById(R.id.windSpeedText);
                    final EditText windDirectionText = inflatedView.findViewById(R.id.windDirectionText);
                    proposedRouteAltitideEditBox.setText(Long.toString(Math.round(vars.route.getProposedAltitude())));
                    windSpeedText.setText(Long.toString(Math.round(vars.route.getWindSpeed())));
                    windDirectionText.setText(Long.toString(Math.round(vars.route.getWindDirection())));
                    builder.setView(inflatedView);
                    builder.setIcon(android.R.drawable.ic_input_get);
                    builder.setMessage("Edit route Variables");
                    builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Integer proposedAltitude = Integer.parseInt(proposedRouteAltitideEditBox.getText().toString());
                            Double windSpeed = Double.parseDouble(windSpeedText.getText().toString());
                            Double windDirection = Double.parseDouble(windDirectionText.getText().toString());

                            vars.route.setWind(windDirection, windSpeed, false);
                            vars.route.setProposedAltitude(proposedAltitude, true);
                            dialog.dismiss();
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    builder.show();
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
                            if (vars.route != null) vars.route.ClearRoute(vars.map);
                            vars.route = new Route(selectedRoute.name, vars);

                            vars.route.createdDate = new Date(selectedRoute.createdDate);
                            vars.route.modifiedDate = new Date(selectedRoute.modifiedDate);
                            SetRoute();
                            vars.route.openRoute(selectedRoute.id, vars.map);
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
                if (vars.route != null)
                {
                    saveRouteDialogs();
                }
            }
        });
    }

    private void saveRouteDialogs()
    {
        if (vars.route != null) {
            if (vars.route.id >0)
            {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setIcon(android.R.drawable.ic_input_get);
                dialogBuilder.setTitle("Save or Save-as");
                dialogBuilder.setMessage("Save this route with a new name or update the already saved version?");
                dialogBuilder.setPositiveButton("New", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doSaveRoute();
                    }
                });
                dialogBuilder.setNegativeButton("Update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vars.route.saveRoute(vars.route.name);
                    }
                });

                dialogBuilder.show();
            }
            else
                doSaveRoute();
        }
        else
        {
            Toast noRouteToast = Toast.makeText(vars.context, "There is no Route open at this moment", Toast.LENGTH_LONG);
            noRouteToast.show();
        }
    }

    private void doSaveRoute()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Save Route");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.text_input, (ViewGroup) getView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setText(vars.route.name);
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
                    vars.route.id = -1l;
                    vars.route.saveRoute(routeName);
                } else {
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
                            vars.route.saveRoute(routeName);
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
