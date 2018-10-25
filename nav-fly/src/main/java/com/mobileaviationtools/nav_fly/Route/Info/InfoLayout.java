package com.mobileaviationtools.nav_fly.Route.Info;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.airnavdata.Entities.Runway;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.oscim.core.GeoPoint;
import org.oscim.map.Map;
import org.w3c.dom.Text;

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

        visibleTypes = new ArrayList<>();
        visibleTypes.add(AirportType.large_airport.toString());
        visibleTypes.add(AirportType.medium_airport.toString());
        visibleTypes.add(AirportType.small_airport.toString());

        setButtonClickListeners();
        setListViewItemClickListener();
    }

    private Map map;
    private Route route;
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
    private StationsType stationsType;
    private ArrayList<String> visibleTypes;

    public enum StationsType
    {
        airports,
        navaids,
        fixes;
    }

    public void setMap(Map map) {
        this.map = map;
    }

    public void setRoute(Route route)
    {
        this.route = route;
    }

    public void init(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        stationsType = StationsType.airports;
    }

    private void setButtonClickListeners()
    {
        airportsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stationsType = StationsType.airports;
                LoadList();
            }
        });
    }

    public void LoadList()
    {
        loadItemsList(route, map.getMapPosition().getGeoPoint());
        switch (stationsType){
            case airports:{
                infoItemAdapter.setAirports(airportItems);
                itemsList.setAdapter(infoItemAdapter);
                break;
            }
            case fixes:
            {
                break;
            }
            case navaids:{
                break;
            }
        }
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
                checkBuf = GeometryHelpers.getCircle(curPosition, 200000d);
            }
        }
        else
        {
            checkBuf = GeometryHelpers.getCircle(curPosition, 200000d);
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

        if (stationsType==StationsType.airports) {
            Airport[] airports = db.getAirport().getAirportsWithinBoundsByTypes(c1.x, c2.x, c2.y, c1.y, visibleTypes);
            for (Airport a : airports) {
                if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(a.longitude_deg, a.latitude_deg)))) {
                    a.runways = db.getRunways().getRunwaysByAirport(a.id);
                    a.frequencies = db.getFrequency().getFrequenciesByAirport(a.id);
                    airportItems.add(a);
                }
            }
        }

        if (stationsType==StationsType.navaids) {
            Navaid[] navaids = db.getNavaids().getNavaidsWithinBoundsByTypes(c1.x, c2.x, c2.y, c1.y);
            for (Navaid n : navaids) {
                if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(n.longitude_deg, n.latitude_deg)))) {
                    navaidItems.add(n);
                }
            }
        }

        if (stationsType==StationsType.fixes) {
            Fix[] fixes = db.getFixes().getFixessWithinBoundsByTypes(c1.x, c2.x, c2.y, c1.y);
            for (Fix f : fixes) {
                if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(f.longitude_deg, f.latitude_deg)))) {
                    fixItems.add(f);
                }
            }
        }
    }

    private void setListViewItemClickListener()
    {
        itemsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                InfoItemAdapter adapter = (InfoItemAdapter)adapterView.getAdapter();
                Object item = adapter.getItem(i);
                if (item instanceof Airport)
                {
                    Airport a = (Airport) item;
                    String runways = "Runways: ";
                    for(Runway r: a.runways)
                    {
                        runways = runways + r.he_ident + "/" + r.le_ident + "(" + r.length_ft + "ft), ";
                    }
                    String frequencies = "Frequencies: ";
                    for (Frequency f: a.frequencies)
                    {
                        frequencies = frequencies + f.type + ": " + String.format ("%.3f", f.frequency_mhz) + ", ";
                    }

                    TextView rText = (TextView)findViewById(R.id.runwaysInfoText);
                    rText.setText(runways);
                    TextView fText = (TextView)findViewById(R.id.frequenciesInfoText);
                    fText.setText(frequencies);
                }
            }
        });
    }
}
