package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.oscim.backend.canvas.Color;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.PolygonDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;
import org.oscim.utils.GeoPointUtils;
import org.oscim.utils.async.AsyncTask;

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

    private String TAG = "AirspaceLayer";

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

        return db.getAirpaces().getAirspacesByPosition(box.getMinLongitude(),
                box.getMaxLongitude(), box.getMinLatitude(), box.getMaxLatitude());
    }

    private class drawAirspaceAsync extends android.os.AsyncTask
    {
        public Airspace airspace;
        @Override
        protected Object doInBackground(Object[] objects) {
            drawAirspace(airspace);
            return null;
        }

    }

    private void drawAirspace(Airspace a)
    {
        Geometry outerGeomety = a.getAirspaceGeometry();
        if (!outerGeomety.isEmpty()) {
            if (a.category.getBufferWidth()!=0) {

                Style lineStyle = Style.builder()
                        .strokeColor(a.category.getStrokeColor())
                        .strokeWidth(a.category.getStrokeWidth())
                        .fillColor(a.category.getFillColor())
                        .fillAlpha(0)
                        .build();

                Style lineStyle1 = Style.builder()
                        .strokeColor(a.category.getOutlineColor())
                        .strokeWidth(a.category.getOutlineWidth())
                        .fillColor(a.category.getFillColor())
                        .fillAlpha(Color.aToFloat(a.category.getFillColor()))
                        .build();

                Geometry innerBuffer = outerGeomety.buffer(-0.005);

                a.airspacePolygon1 = new PolygonDrawable(outerGeomety, lineStyle1);
                if (!innerBuffer.isEmpty()) {
                    a.airspacePolygon2 = new PolygonDrawable(innerBuffer, lineStyle);
                    vectorLayer.add(a.airspacePolygon2);
                }

                vectorLayer.add(a.airspacePolygon1);

            }
            else
            {
                Style lineStyle = Style.builder()
                        .strokeColor(a.category.getStrokeColor())
                        .strokeWidth(a.category.getStrokeWidth())
                        .fillColor(a.category.getFillColor())
                        .fillAlpha(Color.aToFloat(a.category.getFillColor()))
                        .build();
                a.airspacePolygon1 = new PolygonDrawable(outerGeomety, lineStyle);
                vectorLayer.add(a.airspacePolygon1);
            }

        }
    }

    private void drawAirspaces(Airspace[] airspaces)
    {
        Boolean added = false;

        for (Airspace a : airspaces){
            if (a.category.getVisible()) {
                if (!this.airspaces.contains(a)) {
                    drawAirspace(a);
                    this.airspaces.add(a);
                    added = true;
                }
//                drawAirspaceAsync drawAirspaceAsync = new drawAirspaceAsync();
//                drawAirspaceAsync.airspace = a;
//                drawAirspaceAsync.executeOnExecutor(android.os.AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        if (added) vectorLayer.update();
    }
}
