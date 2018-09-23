package com.mobileaviationtools.nav_fly.Route;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

public class WaypointMarkerItem extends MarkerItem {
    public WaypointMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public WaypointMarkerItem(Waypoint waypoint, Context context) {
        this(waypoint.name, waypoint.name, waypoint.point);
        Coordinate coordinate = new Coordinate(waypoint.point.getLongitude(), waypoint.point.getLatitude());
        point = new GeometryFactory().createPoint(coordinate);
        this.waypoint = waypoint;
        this.context = context;
    }

    public void InitMarker() {
        MarkerSymbol symbol = null;
        symbol = WaypointSymbol.GetWaypointSymbol(waypoint,this.context);
        this.setMarker(symbol);
    }

    private Waypoint waypoint;
    private Context context;
    private Point point;
}
