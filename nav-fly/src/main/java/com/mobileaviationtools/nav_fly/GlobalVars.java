package com.mobileaviationtools.nav_fly;

import android.app.Activity;
import android.content.Context;

import com.example.aircraft.Aircraft;
import com.mobileaviationtools.nav_fly.Classes.BaseChartType;
import com.mobileaviationtools.nav_fly.Dashboard.DashboardFragment;
import com.mobileaviationtools.nav_fly.Layers.AircraftLocationLayer;
import com.mobileaviationtools.nav_fly.Layers.DeviationLineLayer;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Menus.MapDirectionType;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Settings.SettingsObject;

import org.oscim.layers.GroupLayer;
import org.oscim.map.Map;

public class GlobalVars {
    public Activity mainActivity;
    public Context context;
    public Map map;
    public FspLocation airplaneLocation;
    public FspLocation mapCenterLocation;
    public FspLocation doDeviationLineFromLocation;
    public Route route;
    public AircraftLocationLayer mAircraftLocationLayer;
    public DeviationLineLayer deviationLineLayer;
    public DashboardFragment dashboardFragment;
    public BaseChartType baseChartType;
    public SettingsObject settingsObject;

    public Aircraft aircraft;

    public MapDirectionType mapDirectionType = MapDirectionType.north;

    public GroupLayer baseGroupLayer;

    public final int BASE_GROUP = 5;
    public final int ONLINETILES_GROUP = 10;
    public final int OVERLAYCHARTS_GROUP = 15;
    public final int AIRSPACE_GROUP = 20;
    public final int AVIATIONMARKERS_GROUP = 25;
    public final int ROUTE_GROUP = 40;
    public final int ROUTE_BUFFERS_GROUP = 30;  // 41
    public final int TRACK_GROUP = 50;
    public final int DEVIATIONLINE_GROUP = 55;
    public final int AIRPLANEMARKER_GROUP = 60;
}
