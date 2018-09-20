package com.mobileaviationtools.nav_fly.Layers;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.Toast;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.AirspaceCategory;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.nav_fly.Classes.AirspaceList;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.R;

import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Polygon;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Color;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.PolygonDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.map.Map;
import org.oscim.renderer.bucket.TextureItem;
import org.oscim.utils.GeoPointUtils;
import org.oscim.utils.async.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AirspaceLayer extends VectorLayer {
    public AirspaceLayer(Map map, Context context)
    {
        super(map);
        this.context = context;
        this.mMap = map;
        db = AirnavDatabase.getInstance(context);
        mMap.layers().add(this);
        airspaces = new AirspaceList();
    }

    private Map mMap;
    private Context context;
    private AirnavDatabase db;
    private AirspaceList airspaces;

    private String TAG = "AirspaceLayer";

    public void UpdateAirspaces()
    {
        BoundingBox box = mMap.getBoundingBox(0);
        Airspace[] airspaces = GetVisibleAirspaces(box);
        drawAirspaces(airspaces, box);
        int i = 0;
    }

    @Override
    public boolean onGesture(Gesture g, MotionEvent e) {
        if (g instanceof Gesture.Tap) {
            //if (contains(e.getX(), e.getY())) {

            //Get surrounded airspaces..
            GeoPoint p = mMap.viewport().fromScreenPoint(e.getX(), e.getY());
            Log.i(TAG,"AirspaceLayer tap\n" + p);
            Airspace[] airspaces = db.getAirpaces().getAirspacesSurroundedBy(p.getLatitude(), p.getLongitude());
            for(Airspace a : airspaces)
            {

                Log.i(TAG, "Found Airspace: " + a.name + " Category: " + a.category + " Bottom: "
                        + a.altLimit_bottom+a.altLimit_bottom_unit
                        + " Top: " + a.altLimit_top + a.altLimit_top_unit);
            }

            return true;
            //}
        }
        return false;
    }

    private Airspace[] GetVisibleAirspaces(BoundingBox box)
    {
        return db.getAirpaces().getAirspacesByPosition(box.getMinLongitude(),
                box.getMaxLongitude(), box.getMinLatitude(), box.getMaxLatitude());
//        return db.getAirpaces().getAirspacesByPositionAndCountry(box.getMinLongitude(),
//                box.getMaxLongitude(), box.getMinLatitude(), box.getMaxLatitude(), "Netherlands");
    }

    private void drawAirspace(Airspace a)
    {
        Geometry outerGeomety = a.getAirspaceGeometry();
        if (!outerGeomety.isEmpty()) {
            if (a.category.getTexture()) {

                TextureItem tex = getTextureItem(a.category);

                Style lineStyle = Style.builder()
                        .stippleColor(a.category.getOutlineColor())
                        .stipple(a.category.getOutlineWidth())
                        .stippleWidth(1)
                        .strokeWidth(a.category.getStrokeWidth())
                        .strokeColor(a.category.getStrokeColor())
                        .fixed(true)
                        .texture(tex)
                        .randomOffset(false)
                        .build();

                Style polygonStyle = Style.builder()
                        .strokeColor(Color.TRANSPARENT)
                        .strokeWidth(0)
                        .fillColor(a.category.getFillColor())
                        .fillAlpha(Color.aToFloat(a.category.getFillColor()))
                        .build();


                a.airspacePolygon1 = new PolygonDrawable(outerGeomety, polygonStyle);
                a.airspacePolygon2 = new LineDrawable(GeometryHelpers.getGeoPoints(outerGeomety), lineStyle);

                //add(a.airspacePolygon2);

                //add(a.airspacePolygon1);

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
                //add(a.airspacePolygon1);
            }

        }
    }

    private TextureItem getTextureItem(AirspaceCategory category)
    {
        int res = 0;
        switch (category)
        {
            case C: res = R.drawable.stroke8; break;
            case CTR: res = R.drawable.stroke8; break;
            case P: res = R.drawable.spoke1; break;
            case D: res = R.drawable.stroke8; break;
            case R: res = R.drawable.spoke1; break;
            case PROHIBITED: res = R.drawable.spoke1; break;
            case DANGER: res = R.drawable.spoke1; break;
            case RESTRICTED: res = R.drawable.spoke1; break;
        }

        if (res == 0) return null;
        else
        {
            TextureItem tex = null;
            tex = new TextureItem(AndroidGraphics.drawableToBitmap(context.getResources(), res));
            tex.mipmap = false;
            return tex;
        }
    }

    private void drawAirspaces(Airspace[] airspaces, BoundingBox box)
    {
        Boolean added = false;

        for (Airspace a : airspaces){
            if (a.category.getVisible()) {
                if (!this.airspaces.contains(a)) {
                    drawAirspace(a);
                    this.airspaces.add(a);
                    added = true;
                }

                if (a.category.getZoomLevel()>0) {
                    if (box.contains(new GeoPoint(a.lat_bottom_right, a.lot_bottom_right))
                            || box.contains(new GeoPoint(a.lat_top_left, a.lon_top_left))) {
                        if (mMap.getMapPosition().zoomLevel>a.category.getZoomLevel())
                        {
                            Airspace aa = this.airspaces.GetAirspace(a);
                            if (!aa.visible) {
                                if (aa.airspacePolygon1 != null) add(aa.airspacePolygon1);
                                if (aa.airspacePolygon2 != null) add(aa.airspacePolygon2);
                                aa.visible = true;
                            }
                        }
                        else
                        {
                            Airspace aa = this.airspaces.GetAirspace(a);
                            if (aa.visible)
                            {
                                if (aa.airspacePolygon1 != null) remove(aa.airspacePolygon1);
                                if (aa.airspacePolygon2 != null) remove(aa.airspacePolygon2);
                                aa.visible = false;
                            }
                        }
                    }
                }
                else
                {
                    if (a.airspacePolygon1 != null) add(a.airspacePolygon1);
                    if (a.airspacePolygon2 != null) add(a.airspacePolygon2);
                    a.visible = true;
                }
            }
        }

        //if (added)
            update();
    }
}
