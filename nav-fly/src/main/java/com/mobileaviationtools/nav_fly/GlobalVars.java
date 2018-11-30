package com.mobileaviationtools.nav_fly;

import android.app.Activity;
import android.content.Context;

import com.mobileaviationtools.nav_fly.Dashboard.DashboardFragment;
import com.mobileaviationtools.nav_fly.Layers.AircraftLocationLayer;
import com.mobileaviationtools.nav_fly.Layers.DeviationLineLayer;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Route.Route;

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
}
