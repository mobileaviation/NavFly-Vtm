package com.mobileaviationtools.nav_fly.Route.Info;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.AirnavRouteDatabase;
import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Classes.ChartType;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.airnavdata.Entities.Runway;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.MainActivity;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.RouteLoadItemAdapter;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.modelmapper.internal.objenesis.ObjenesisHelper;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.map.Map;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
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
        airportChartsListView = (ListView) findViewById(R.id.airportChartsListView);

        airportsBtn = (ImageButton) findViewById(R.id.airportsInfoBtn);
        navaidsBtn = (ImageButton) findViewById(R.id.navaidsInfoBtn);
        fixesBtn = (ImageButton) findViewById(R.id.fixesInfoBtn);
        assignChartbtn = (ImageButton) findViewById(R.id.assignChartbtn);

        visibleTypes = new ArrayList<>();
        visibleTypes.add(AirportType.large_airport.toString());
        visibleTypes.add(AirportType.medium_airport.toString());
        visibleTypes.add(AirportType.small_airport.toString());

        setSelectedAirport(null);
        selectedFix = null;
        selectedNavaid = null;

        setButtonClickListeners();
        setListViewItemClickListener();
        setAssignChartBtnClickListener();
        setChartListViewItemClickListerner();

        setPosition = true;
    }

    private String TAG = "InfoLayout";
    private Map map;
    private Route route;
    private Context context;
    private Activity activity;
    private ListView itemsList;
    private ListView airportChartsListView;
    private InfoItemAdapter infoItemAdapter;
    private ChartItemAdapter chartItemAdapter;

    private List<Airport> airportItems;
    private List<Navaid> navaidItems;
    private List<Fix> fixItems;

    private ImageButton airportsBtn;
    private ImageButton navaidsBtn;
    private ImageButton fixesBtn;
    private StationsType stationsType;
    private ArrayList<String> visibleTypes;
    private ImageButton assignChartbtn;

    private boolean setPosition;

    private Airport selectedAirport;
    private Navaid selectedNavaid;
    private Fix selectedFix;

    private void setSelectedAirport(Airport airport)
    {
        selectedAirport = airport;
        assignChartbtn.setEnabled((airport != null));
        loadCharts();
    }

    public enum StationsType
    {
        airports,
        navaids,
        fixes;
    }



    private ChartEvents chartEvent;
    public void setChartEvent(ChartEvents chartEvent){
        this.chartEvent = chartEvent;
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
        setInfoTitleText();
    }

    private void setInfoTitleText()
    {
        TextView titleTextView = (TextView) findViewById(R.id.infoTitleTextView);
        switch (stationsType){
            case navaids:{
                titleTextView.setText("Navaids Info.");
                break;
            }
            case fixes:{
                titleTextView.setText("Fixes Info.");
                break;
            }
            case airports: {
                titleTextView.setText("Airports Info.");
                break;
            }
        }
    }

    private void setButtonClickListeners()
    {
        airportsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stationsType = StationsType.airports;
                LoadList();
                setInfoTitleText();
            }
        });
        navaidsBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stationsType = StationsType.navaids;
                LoadList();
                setInfoTitleText();
            }
        });
        fixesBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                stationsType = StationsType.fixes;
                LoadList();
                setInfoTitleText();
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
            case navaids:
            {
                infoItemAdapter.setNavaids(navaidItems);
                itemsList.setAdapter(infoItemAdapter);
                break;
            }
            case fixes:{
                infoItemAdapter.setFix(fixItems);
                itemsList.setAdapter(infoItemAdapter);
                break;
            }
        }

        infoItemAdapter.notifyDataSetChanged();
    }

    public void ShowAirportInfo(Airport airport)
    {
        if (!airportItems.contains(airport))
        {
            airportItems.add(airport);
            infoItemAdapter.setAirports(airportItems);
            itemsList.setAdapter(infoItemAdapter);
        }

        if (stationsType != StationsType.airports)
        {
            stationsType = StationsType.airports;
            infoItemAdapter.setAirports(airportItems);
            itemsList.setAdapter(infoItemAdapter);
        }

        setPosition = false;
        int index = airportItems.indexOf(airport);
        itemsList.requestFocusFromTouch();
        itemsList.setSelection(index);
        itemsList.performItemClick(itemsList.getAdapter().getView(index,null,null), index,
                itemsList.getAdapter().getItemId(index));

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
            airportItems.clear();
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
            navaidItems.clear();
            Navaid[] navaids = db.getNavaids().getNavaidsWithinBoundsByTypes(c1.x, c2.x, c2.y, c1.y);
            for (Navaid n : navaids) {
                if (checkBuf.contains(new GeometryFactory().createPoint(new Coordinate(n.longitude_deg, n.latitude_deg)))) {
                    navaidItems.add(n);
                }
            }
        }

        if (stationsType==StationsType.fixes) {
            fixItems.clear();
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
                    setAirportInfo((Airport)item);

                    if ((map != null) && setPosition) {
                        MapPosition position = map.getMapPosition();
                        position.setPosition(new GeoPoint(((Airport) item).latitude_deg, ((Airport) item).longitude_deg));
                        map.setMapPosition(position);
                    }
                    else
                    {
                        setPosition = true;
                    }

                }
                if (item instanceof Navaid)
                {
                    setNavaidInfo((Navaid) item);

                    if ((map != null) && setPosition) {
                        MapPosition position = map.getMapPosition();
                        position.setPosition(new GeoPoint(((Navaid) item).latitude_deg, ((Navaid) item).longitude_deg));
                        map.setMapPosition(position);
                    }
                    else
                    {
                        setPosition = true;
                    }
                }

                if (item instanceof Fix)
                {
                    // setFixInfo

                    if ((map != null) && setPosition) {
                        MapPosition position = map.getMapPosition();
                        position.setPosition(new GeoPoint(((Fix) item).latitude_deg, ((Fix) item).longitude_deg));
                        map.setMapPosition(position);
                    }
                    else
                    {
                        setPosition = true;
                    }
                }
            }
        });
    }

    private void setNavaidInfo(Navaid navaid)
    {
        String nav = navaid.name + " (" + navaid.ident + ") " + navaid.type;
        String nav2 = "Frequency: " + Double.toString(navaid.frequency_khz);
        TextView rText = (TextView)findViewById(R.id.runwaysInfoText);
        rText.setText(nav);
        TextView fText = (TextView)findViewById(R.id.frequenciesInfoText);
        fText.setText(nav2);
    }

    private void setAirportInfo(Airport airportInfo)
    {
        String runways = "Runways: ";
        for(Runway r: airportInfo.runways)
        {
            runways = runways + r.he_ident + "/" + r.le_ident + "(" + r.length_ft + "ft), ";
        }
        String frequencies = "Frequencies: ";
        for (Frequency f: airportInfo.frequencies)
        {
            frequencies = frequencies + f.type + ": " + String.format ("%.3f", f.frequency_mhz) + ", ";
        }
        setSelectedAirport(airportInfo);

        TextView rText = (TextView)findViewById(R.id.runwaysInfoText);
        rText.setText(runways);
        TextView fText = (TextView)findViewById(R.id.frequenciesInfoText);
        fText.setText(frequencies);
    }

    private void setChartListViewItemClickListerner()
    {
        airportChartsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ChartItemAdapter chartItemAdapter = (ChartItemAdapter)adapterView.getAdapter();
                if (chartEvent != null) chartEvent.OnChartSelected((Chart)chartItemAdapter.getItem(i));
            }
        });
    }

    public static class MyFileNameFilter implements FilenameFilter {

        private ArrayList<String> ext;

        public MyFileNameFilter(String[] extentions) {
            this.ext = new ArrayList();
            for (String e: extentions)
            {
                this.ext.add(e.toLowerCase());
            }
        }

        @Override
        public boolean accept(File dir, String name) {
            boolean ret = false;
            for (String e : ext)
            {
                ret = ret || name.toLowerCase().endsWith(e);
            }
            return ret;
        }

    }

    private void loadCharts()
    {
        if (selectedAirport != null)
        {
            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
            Chart[] charts = db.getCharts().getChartsByAirportRef(selectedAirport.id);
            chartItemAdapter = new ChartItemAdapter(context, charts);
            chartItemAdapter.setChartCheckedEvent(new ChartEvents() {
                @Override
                public void OnChartCheckedEvent(Chart chart, Boolean checked) {
                    if (chartEvent != null) chartEvent.OnChartCheckedEvent(chart, checked);
                }

                @Override
                public void OnChartSelected(Chart chart) {

                }
            });
            airportChartsListView.setAdapter(chartItemAdapter);
        }
    }



    private void setAssignChartBtnClickListener()
    {
        assignChartbtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
//                File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//                File[] files = downloadFolder.listFiles(new MyFileNameFilter(new String[]{".png", ".pdf"}));
//
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("Assign Chart");
//                View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.chart_assign_list, (ViewGroup) InfoLayout.this, false);
//                final ListView filesList = (ListView) viewInflated.findViewById(R.id.list);
//                final EditText latSText = (EditText) viewInflated.findViewById(R.id.latitudeSText);
//                final EditText latNText = (EditText) viewInflated.findViewById(R.id.latitudeNText);
//                final EditText lonEText = (EditText) viewInflated.findViewById(R.id.longitudeEText);
//                final EditText lonWText = (EditText) viewInflated.findViewById(R.id.longitudeWText);
//
//                final ChartLoadItemAdapter chartLoadItemAdapter = new ChartLoadItemAdapter(files, getContext());
//                filesList.setAdapter(chartLoadItemAdapter);
//
//                filesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                    @Override
//                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                        filesList.setItemChecked(i, true);
//                    }
//                });
//                builder.setView(viewInflated);
//                builder.setIcon(android.R.drawable.ic_input_get);
//                builder.setMessage("Select Chart from list!");
//                builder.setPositiveButton("Assign", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        int pos = filesList.getCheckedItemPosition();
//                        File file = (File)chartLoadItemAdapter.getItem(pos);
//                        try {
//                            FileInputStream fis = new FileInputStream(file);
//                            byte[] fileBytes = new byte[(int)file.length()];
//                            fis.read(fileBytes, 0, (int)file.length());
//                            Chart chart = new Chart();
//                            chart.filelocation = file.getAbsolutePath();
//                            chart.name = file.getName();
//                            chart.airport_ref = selectedAirport.id;
//                            chart.chart = fileBytes;
//                            chart.type = ChartType.getTypeByExtention(file);
//                            chart.latitude_deg_n = Double.parseDouble(latNText.getText().toString());
//                            chart.latitude_deg_s = Double.parseDouble(latSText.getText().toString());
//                            chart.longitude_deg_e = Double.parseDouble(lonEText.getText().toString());
//                            chart.longitude_deg_w = Double.parseDouble(lonWText.getText().toString());
//                            chart.active = false;
//
//                            AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(context);
//                            db.getCharts().InsertChart(chart);
//                            loadCharts();
//                        }
//                        catch (Exception e)
//                        {
//                            Log.e(TAG, "problems reading " + file.getAbsolutePath());
//                        }
//
//                    }
//                });
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.cancel();
//                    }
//                });
//                builder.show();
                SelectChartDialog dialog = SelectChartDialog.getInstance(context);
                dialog.show(((MainActivity)context).getSupportFragmentManager(), "");
            }
        });
    }
}
