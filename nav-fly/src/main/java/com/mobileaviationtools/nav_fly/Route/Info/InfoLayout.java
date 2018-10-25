package com.mobileaviationtools.nav_fly.Route.Info;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.core.GeoPoint;
import org.oscim.map.Map;

import java.util.ArrayList;
import java.util.List;

public class InfoLayout extends LinearLayout {
    public InfoLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(
                R.layout.info_layout, this);

        airportItems = new ArrayList();
        navaidItems = new ArrayList<>();
        fixItems = new ArrayList<>();
        infoItemAdapter = new InfoItemAdapter(getContext());
        itemsList = (ListView) findViewById(R.id.infoItemsListView);

        airportsBtn = (ImageButton) findViewById(R.id.airportsInfoBtn);
        navaidsBtn = (ImageButton) findViewById(R.id.navaidsInfoBtn);
        fixesBtn = (ImageButton) findViewById(R.id.fixesInfoBtn);

        setButtonClickListeners();
    }

    private Map map;
    private Context context;
    private Activity activity;
    private ListView itemsList;
    private InfoItemAdapter infoItemAdapter;
    private List<Airport> airportItems;
    private List<Navaid> navaidItems;
    private List<Fix> fixItems;
    private ImageButton airportsBtn;
    private ImageButton navaidsBtn;
    private ImageButton fixesBtn;

    public void setMap(Map map) {
        this.map = map;
    }

    public void init(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    private void setButtonClickListeners()
    {
        airportsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (airportItems.size()>0)
                {
                    infoItemAdapter.setAirports(airportItems);
                    itemsList.setAdapter(infoItemAdapter);
                }
            }
        });
    }

    public void FillItemsList(Route route, GeoPoint geoPoint)
    {
        loadItemsList(route, geoPoint);
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
                checkBuf = fac.createLineString(cc).buffer(0.3d);
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

        Airport[] airports = db.getAirport().getAirportsWithinBounds(c1.x, c2.x, c2.y, c1.y);
        for (Airport a : airports) {
            if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(a.longitude_deg, a.latitude_deg)))) {
                airportItems.add(a);
            }
        }

        Navaid[] navaids = db.getNavaids().getNavaidsWithinBoundsByTypes(c1.x,c2.x,c2.y,c1.y);
        for (Navaid n : navaids) {
            if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(n.longitude_deg, n.latitude_deg)))) {
                navaidItems.add(n);
            }
        }

        Fix[] fixes = db.getFixes().getFixessWithinBoundsByTypes(c1.x,c2.x,c2.y,c1.y);
        for (Fix f : fixes) {
            if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(f.longitude_deg, f.latitude_deg)))) {
                fixItems.add(f);
            }
        }
    }
}
