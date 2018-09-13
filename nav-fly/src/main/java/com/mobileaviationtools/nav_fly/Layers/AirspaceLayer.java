package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
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
//        return db.getAirpaces().getAirspacesByPosition(box.getMinLongitude(),
//                box.getMaxLongitude(), box.getMinLatitude(), box.getMaxLatitude());

        return db.getAirpaces().getAirspacesByPositionAndCategory(box.getMinLongitude(),
                box.getMaxLongitude(), box.getMinLatitude(), box.getMaxLatitude(), "CTR");
    }

    private void drawAirspaces(Airspace[] airspaces)
    {
        Boolean added = false;

        for (Airspace a : airspaces){
            if (a.category.getVisible()) {
                if (!this.airspaces.contains(a)) {

                    Style lineStyle = Style.builder()
                            .strokeColor(a.category.getStrokeColor())
                            .strokeWidth(a.category.getStrokeWidth())
                            .fillColor(a.category.getFillColor())
                            .fillAlpha(Color.aToFloat(a.category.getFillColor()))
                            .build();
                    a.airspacePolygon1 = new PolygonDrawable(a.getAirspaceGeometry(), lineStyle);


                    Style lineStyle1 = Style.builder()
                            .strokeColor(a.category.getOutlineColor())
                            .strokeWidth(a.category.getOutlineWidth())
                            .stipple(10).stippleWidth(10).stippleColor(Color.WHITE)
                            .fillColor(a.category.getFillColor())
                            .fillAlpha(0)
                            .build();
                    a.airspacePolygon2 = new PolygonDrawable(a.getAirspaceGeometry(), lineStyle1);

                    vectorLayer.add(a.airspacePolygon2);
                    vectorLayer.add(a.airspacePolygon1);


                    this.airspaces.add(a);
                    added = true;
                }
            }
        }

        if (added) vectorLayer.update();
    }
}
