package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.nav_fly.GlobalVars;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.PathLayer;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.Drawable;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

import java.util.ArrayList;

public class RoutePathLayer extends PathLayer {
    private Route route;
    private Integer selectedLegColor = 0xFF7277d8;// 0xFFF43D6B;//0xFFFFBC00;
    private float lineWidth;
    private GlobalVars vars;

    public RoutePathLayer(Map map, Style style) {
        super(map, style);
    }

    public RoutePathLayer(Map map, int lineColor, float lineWidth, GlobalVars vars) {
        super(map, lineColor, lineWidth);
        this.vars = vars;
        this.lineWidth = lineWidth;
        createOutlinePathLayer(map, Color.DKGRAY, lineWidth+4);
        createSelectedLegLayer(map);
        createLegMarkersLayer(map);
    }

    public RoutePathLayer(Map map, int lineColor) {
        super(map, lineColor);
    }

    public void AddLayer(Map map, Route route)
    {
        this.route = route;
        map.layers().add(outlinePathLayer, vars.ROUTE_GROUP);
        map.layers().add(this, vars.ROUTE_GROUP);
        map.layers().add(selectedLegLayer, vars.ROUTE_GROUP);
        map.layers().add(legMarkersLayer, vars.ROUTE_GROUP);
    }

    public void SelectLeg(Leg selectedLeg)
    {
        UnselectLeg();

        this.selectedLeg = selectedLeg;
        Style selectedLineStyle = Style.builder()
                .fixed(true)
                .strokeColor(selectedLegColor)
                .strokeWidth(lineWidth)
                .build();
        ArrayList<GeoPoint> points = new ArrayList<>();
        points.add(this.selectedLeg.startWaypoint.point);
        points.add(this.selectedLeg.endWaypoint.point);
        selectedLegDrawable = new LineDrawable(points, selectedLineStyle);
        selectedLegLayer.add(selectedLegDrawable);
        selectedLegLayer.update();
    }

    public void UnselectLeg()
    {
        if (this.selectedLeg != null) {
            selectedLegLayer.remove(selectedLegDrawable);
            selectedLeg = null;
            selectedLegDrawable = null;
        }
    }

    private Leg selectedLeg;
    private Drawable selectedLegDrawable;
    private PathLayer outlinePathLayer;
    private VectorLayer selectedLegLayer;
    private LegMarkersLayer legMarkersLayer;

    private void createOutlinePathLayer(Map map, int lineColor, float lineWidth)
    {
        outlinePathLayer = new PathLayer(map, lineColor, lineWidth);
    }

    private void createLegMarkersLayer(Map map)
    {
        legMarkersLayer = new LegMarkersLayer(map, null);
    }

    private void createSelectedLegLayer(Map map)
    {
        selectedLegLayer = new VectorLayer(map);
    }

    public void AddWaypoint(Waypoint waypoint)
    {
        UnselectLeg();
        this.addPoint(waypoint.point);
        outlinePathLayer.addPoint(waypoint.point);
    }

    @Override
    public void clearPath()
    {
        super.clearPath();
        UnselectLeg();
        outlinePathLayer.clearPath();
        legMarkersLayer.removeAllItems();
    }

    @Override
    public void update() {
        super.update();
        selectedLegLayer.update();
        outlinePathLayer.update();
        legMarkersLayer.UpdateLegs(route);
    }
}
