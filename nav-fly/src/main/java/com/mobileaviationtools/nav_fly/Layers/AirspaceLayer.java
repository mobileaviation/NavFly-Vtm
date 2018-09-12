package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

import org.oscim.backend.canvas.Color;
import org.oscim.core.BoundingBox;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PolygonDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

public class AirspaceLayer{
    public AirspaceLayer(Map map, Context context)
    {
        this.context = context;
        this.mMap = map;
        db = AirnavDatabase.getInstance(context);
        vectorLayer = new VectorLayer(this.mMap);
        mMap.layers().add(vectorLayer);
        airspaces = new ArrayList<>();
    }

    private Map mMap;
    private Context context;
    private AirnavDatabase db;
    private VectorLayer vectorLayer;
    private ArrayList<Airspace> airspaces;

    public void UpdateAirspaces()
    {
        Airspace[] airspaces = GetVisibleAirspaces();
        drawAirspaces(airspaces);
        int i = 0;
    }

    private Airspace[] GetVisibleAirspaces()
    {
        BoundingBox box = mMap.getBoundingBox(0);
        return db.getAirpaces().getAirspacesByPosition(box.getMinLongitude(),
                box.getMaxLongitude(), box.getMinLatitude(), box.getMaxLatitude(), "CTR");
    }

    private void drawAirspaces(Airspace[] airspaces)
    {
        Style lineStyle = Style.builder()
                .strokeColor(Color.BLACK)
                .strokeWidth(2).build();

        Boolean added = false;

        for (Airspace a : airspaces){
            if (a.category.getVisible()) {
                if (!this.airspaces.contains(a)) {
                    a.airspacePolygon = new PolygonDrawable(a.getAirspaceGeometry(), lineStyle);
                    vectorLayer.add(a.airspacePolygon);
                    this.airspaces.add(a);
                    added = true;
                }
            }
        }

        if (added) vectorLayer.update();
    }
}
