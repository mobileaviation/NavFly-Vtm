package com.mobileaviationtools.nav_fly.Route.HeightMap;

import com.mobileaviationtools.airnavdata.AirnavTracklogDatabase;
import com.mobileaviationtools.airnavdata.Entities.TrackLog;
import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Route.HeightMap.Comparators.CompAltitude;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.core.GeoPoint;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackPoints extends ArrayList<ExtCoordinate> {
    public interface TrackPointsEvents
    {
        public void OnPointsLoaded(TrackPoints route);
    }

    private TrackLog trackLog;
    private List<TrackLogItem> trackLogItems;
    private Geometry routeGeom;
    public String TAG = "RoutePoints";
    private double totalDistance_meter;
    private GlobalVars vars;
    private TrackPointsEvents trackPointsEvents;
    private AirnavTracklogDatabase airnavTracklogDatabase;
    private Long trackLogId;

    public TrackPoints(GlobalVars vars)
    {
        this.vars = vars;
    }

    public double getTotalDistance_meter() {
        return totalDistance_meter;
    }

    public void SetupPoints(Long trackId, TrackPointsEvents trackPointsEvents)
    {
        this.trackPointsEvents = trackPointsEvents;
        // Retrieve the track and create ExtCoordinates from the trackpoints

        airnavTracklogDatabase = AirnavTracklogDatabase.getInstance(vars.context);
        this.trackLogId = trackId;

        trackLog = airnavTracklogDatabase.getTrackLog().getTracklogByID(this.trackLogId);

        trackLogItems = airnavTracklogDatabase.getTracklogItems().getTracklogItemByLogId(this.trackLogId);

        for (TrackLogItem item : trackLogItems)
        {
            ExtCoordinate c = new ExtCoordinate(item.longitude_deg, item.latitude_deg);
            c.altitude = item.altitude_ft;
            c.date = item.logDate;
            this.add(c);
        }

        calculateDistances();
    }

    private void calculateDistances()
    {
        for (Integer i=1; i<this.size(); i++) {
            ExtCoordinate c1 = this.get(i-1);
            ExtCoordinate c2 = this.get(i);

            GeoPoint p1 = new GeoPoint(c1.y, c1.x);
            GeoPoint p2 = new GeoPoint(c2.y, c2.x);

            c1.distanceToNext_meter = p1.sphericalDistance(p2);
            this.totalDistance_meter = this.totalDistance_meter + c1.distanceToNext_meter;
        }
    }

    public ArrayList<GeoPoint> getTrackPoints()
    {
        ArrayList<GeoPoint> points = new ArrayList<>();
        for (ExtCoordinate c : this)
        {
            GeoPoint p = new GeoPoint(c.y, c.x);
            points.add(p);
        }

        return points;
    }

    public Geometry getTrackGeom()
    {
        GeometryFactory factory = new GeometryFactory();
        return factory.createLinearRing((ExtCoordinate[])this.toArray());
    }

    public String getTrackLogName()
    {
        return trackLog.name;
    }

    public ExtCoordinate getMaxAltitude()
    {
        return Collections.max(this, new CompAltitude());
    }
}
