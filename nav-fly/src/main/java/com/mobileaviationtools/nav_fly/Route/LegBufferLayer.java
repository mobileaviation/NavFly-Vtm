package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Route.HeightMap.RoutePoints;

import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.CircleDrawable;
import org.oscim.layers.vector.geometries.JtsDrawable;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.PointDrawable;
import org.oscim.layers.vector.geometries.PolygonDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

import java.util.ArrayList;

public class LegBufferLayer extends VectorLayer {
    public LegBufferLayer(Map map) {
        super(map);
    }

    public LegBufferLayer(GlobalVars vars, Route route, Boolean visible)
    {
        super(vars.map);
        this.route = route;
        if (visible) vars.map.layers().add(this, vars.ROUTE_BUFFERS_GROUP );
    }

    private Route route;
    private ArrayList<JtsDrawable> buffers;

    public void ShowRouteBuffers()
    {
        if (buffers == null)
        {
            buffers = new ArrayList<>();
        }

        ClearLayer();

        Style s = Style.builder()
                .fixed(true)
                .strokeColor(Color.RED)
                .strokeWidth(2)
                .build();
        for (Leg l: route.getLegs())
        {
            PolygonDrawable p = new PolygonDrawable(l.getLegBuffer(), s);
            buffers.add(p);
            this.add(p);
        }

        this.update();
    }

    public void showRoutePoints()
    {
        Style s = Style.builder()
                .fixed(true)
                .strokeColor(Color.RED)

                .strokeWidth(5)
                .build();

        if (buffers == null)
        {
            buffers = new ArrayList<>();
        }

        RoutePoints points = route.getRoutePoints();



        if (points != null)
        {
//            LineDrawable lineDrawable = new LineDrawable(points.getRoutePoints(), s);
//            buffers.add(lineDrawable);
//            this.add(lineDrawable);
            for (GeoPoint p : points.getRoutePoints())
            {
                CircleDrawable pd = new CircleDrawable(p, .2, s);
                buffers.add(pd);
                this.add(pd);
            }
        }
    }

    public void ClearLayer()
    {
        if (buffers != null)
            for(JtsDrawable d : buffers) {
                this.remove(d);
                this.update();
            }
    }
}
