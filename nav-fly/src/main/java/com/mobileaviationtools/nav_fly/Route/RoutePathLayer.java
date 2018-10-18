package com.mobileaviationtools.nav_fly.Route;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

public class RoutePathLayer extends PathLayer {
    private Route route;

    public RoutePathLayer(Map map, Style style) {
        super(map, style);
    }

    public RoutePathLayer(Map map, int lineColor, float lineWidth) {
        super(map, lineColor, lineWidth);
        createOutlinePathLayer(map, Color.DKGRAY, lineWidth+4);
        createLegMarkersLayer(map);
    }

    public RoutePathLayer(Map map, int lineColor) {
        super(map, lineColor);
    }

    public void AddLayer(Map map, Route route)
    {
        this.route = route;
        map.layers().add(outlinePathLayer);
        map.layers().add(this);
        map.layers().add(legMarkersLayer);
    }

    private PathLayer outlinePathLayer;
    private LegMarkersLayer legMarkersLayer;

    private void createOutlinePathLayer(Map map, int lineColor, float lineWidth)
    {
        outlinePathLayer = new PathLayer(map, lineColor, lineWidth);
    }

    private void createLegMarkersLayer(Map map)
    {
        legMarkersLayer = new LegMarkersLayer(map, null);
    }

    public void AddWaypoint(Waypoint waypoint)
    {
        this.addPoint(waypoint.point);
        outlinePathLayer.addPoint(waypoint.point);
    }

    @Override
    public void clearPath()
    {
        super.clearPath();
        outlinePathLayer.clearPath();
        legMarkersLayer.removeAllItems();
    }

    @Override
    public void update() {
        super.update();
        outlinePathLayer.update();
        legMarkersLayer.UpdateLegs(route);
    }
}
