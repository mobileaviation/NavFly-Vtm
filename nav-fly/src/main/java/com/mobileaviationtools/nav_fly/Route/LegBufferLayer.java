package com.mobileaviationtools.nav_fly.Route;

import com.mobileaviationtools.nav_fly.GlobalVars;

import org.oscim.backend.canvas.Color;
import org.oscim.layers.vector.VectorLayer;
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
    private ArrayList<PolygonDrawable> buffers;

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

    public void ClearLayer()
    {
        if (buffers != null)
            for(PolygonDrawable d : buffers) {
                this.remove(d);
                this.update();
            }
    }
}
