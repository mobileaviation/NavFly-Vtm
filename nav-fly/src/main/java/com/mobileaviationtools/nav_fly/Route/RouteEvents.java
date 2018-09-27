package com.mobileaviationtools.nav_fly.Route;

public interface RouteEvents {
    void NewWaypointInserted(Route route, Waypoint newWaypoint);
    void NewRouteCreated(Route route);
}
