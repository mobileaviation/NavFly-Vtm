package com.mobileaviationtools.nav_fly.Location;

import android.content.Context;
import android.util.Log;

//import com.mobileaviationtools.nav_fly.Route;
import com.mobileaviationtools.airnavdata.AirnavTracklogDatabase;
import com.mobileaviationtools.airnavdata.Entities.TrackLog;
import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Route.Route;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Canvas;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.layers.PathLayer;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Tracking {
    public Tracking(GlobalVars vars)
    {
        this.vars = vars;
        //locationList = new ArrayList<>();
        database = AirnavTracklogDatabase.getInstance(vars.context);
        setupTrackLayer();
    }

    private String TAG = "Tracking";
    private GlobalVars vars;
    //private List<FspLocation> locationList;
    private FspLocation lastLocation;
    private Route route;
    private AirnavTracklogDatabase database;
    private TrackLog trackLog;

    private PathLayer trackLayer;
    private void setupTrackLayer()
    {
        int c = Color.get(0xF4, 0x6E, 0x8F ); //.parseColor("fff46e8f");
        trackLayer = new PathLayer(vars.map, c, 3 * CanvasAdapter.getScale());
        vars.map.layers().add(trackLayer, vars.TRACK_GROUP);
    }

    public void start(Route route)
    {
        this.route = route;
        trackLayer.clearPath();

        trackLog = new TrackLog();
        trackLog.logDate = new Date();
        if (route != null) {
            trackLog.routeId = route.id;
            trackLog.routeName = route.name;
        }

        trackLog.name = "FlightLog: " + trackLog.logDate.toString();

        trackLog.id = database.getTrackLog().InsertLog(trackLog);
    }

    public void setLocation(FspLocation location)
    {
        Log.i(TAG, "Location Changed: " + vars.airplaneLocation.getLatitude() + " "
                + vars.airplaneLocation.getLongitude() + " " + vars.airplaneLocation.getBearing() + " " + vars.airplaneLocation.getSpeed());

        if (trackLog != null) {
            //locationList.add(location);
            if (lastLocation == null) {
                trackLayer.addPoint(new GeoPoint(location.getLatitude(), location.getLongitude()));
                lastLocation = new FspLocation("TrackingLoc");
                lastLocation.Assign(location);
            } else {
                Log.i(TAG, "Distance: " + location.distanceTo(lastLocation));
                if (location.distanceTo(lastLocation) > 100) {
                    trackLayer.addPoint(new GeoPoint(location.getLatitude(), location.getLongitude()));
                    Log.i(TAG, "Add Trackpoint");
                    lastLocation.Assign(location);
                    addLocationToLog(location);
                }
            }
        }
        else
        {
            Log.i(TAG, "tracklog object not found");
        }
    }

    private void addLocationToLog(FspLocation location)
    {
        TrackLogItem item = new TrackLogItem();
        item.latitude_deg = location.getLatitude();
        item.longitude_deg = location.getLongitude();
        item.altitude_ft = location.getAltitude();
        item.ground_speed_kt = (double)location.getSpeed();
        item.true_heading_deg = (double)location.getBearing();
        item.logDate = new Date();
        item.trackLogId = trackLog.id;
        database.getTracklogItems().insertItem(item);
    }

    public void stop()
    {
        lastLocation = null;
        trackLog = null;
    }
}
