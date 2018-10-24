package com.mobileaviationtools.nav_fly.Route.Info;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.core.GeoPoint;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

public class InfoLayout extends LinearLayout {
    public InfoLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
        items = new ArrayList();
    }

    private Map map;
    private Context context;
    private Activity activity;
    private ListView itemsList;
    private InfoItemAdapter infoItemAdapter;
    private List<InfoItem> items;

    public void setMap(Map map) {
        this.map = map;
    }

    public void init(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    public void FillItemsList(Route route, GeoPoint geoPoint)
    {
        loadItemsList(route, geoPoint);
        int i=0;
    }

    private Geometry getAreaCheckBuffer(Route route, GeoPoint curPosition)
    {
        Geometry checkBuf = null;
        if (route != null)
        {
            if (route.size()>1)
            {
                GeometryFactory fac = new GeometryFactory();
                Coordinate[] cc = new Coordinate[route.size()];
                int i=0;
                for (Waypoint w : route) {
                    cc[i++] = new Coordinate(w.point.getLongitude(), w.point.getLatitude());
                }
                checkBuf = fac.createLineString(cc).buffer(0.5d);
            }
            else
            {
                checkBuf = GeometryHelpers.getCircle(curPosition, 100000d);
            }
        }
        else
        {
            checkBuf = GeometryHelpers.getCircle(curPosition, 100000d);
        }

        return checkBuf;
    }

    private void loadItemsList(Route route, GeoPoint curPosition)
    {
        Geometry checkBuf = getAreaCheckBuffer(route, curPosition);
        Geometry envelop = checkBuf.getEnvelope();

        AirnavDatabase db = AirnavDatabase.getInstance(context);
        Coordinate[] corners = envelop.getCoordinates();
        Coordinate c1 = corners[0];
        Coordinate c2 = corners[2];

        Airport[] airports = db.getAirport().getAirportsWithinBounds(c1.x, c2.x, c1.y, c2.y);
        for (Airport a : airports) {
            InfoItem i = new InfoItem();
            i.airport = a;
            items.add(i);
        }

    }
}
