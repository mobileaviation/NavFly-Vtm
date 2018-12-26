package com.mobileaviationtools.nav_fly;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.AirnavUserSettingsDatabase;
import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.Database;
import com.mobileaviationtools.nav_fly.Classes.CheckMap;
import com.mobileaviationtools.nav_fly.Classes.ConnectStage;
import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.Dashboard.DashboardFragment;
import com.mobileaviationtools.nav_fly.Info.Airspace.AirspacesInfoFragment;
import com.mobileaviationtools.nav_fly.Layers.AircraftLocationLayer;
import com.mobileaviationtools.nav_fly.Layers.AirspaceLayer;
import com.mobileaviationtools.nav_fly.Layers.DeviationLineLayer;
import com.mobileaviationtools.nav_fly.Layers.SelectionLayer;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Location.FspLocationProvider;
import com.mobileaviationtools.nav_fly.Location.LocationEvents;
import com.mobileaviationtools.nav_fly.Location.LocationProviderType;
import com.mobileaviationtools.nav_fly.Location.Tracking;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkersLayer;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportSelected;
import com.mobileaviationtools.nav_fly.Markers.Navaids.NaviadMarkersLayer;
import com.mobileaviationtools.nav_fly.Menus.MapDirectionType;
import com.mobileaviationtools.nav_fly.Menus.MenuItemType;
import com.mobileaviationtools.nav_fly.Menus.NavigationButtonFragment;
import com.mobileaviationtools.nav_fly.Menus.OnNavigationMemuItemClicked;
import com.mobileaviationtools.nav_fly.Route.Info.ChartEvents;
import com.mobileaviationtools.nav_fly.Route.Notams.NotamRetrieval;
import com.mobileaviationtools.nav_fly.Route.RouteListFragment;
import com.mobileaviationtools.nav_fly.Route.Weather.Station;
import com.mobileaviationtools.nav_fly.Route.Weather.WeatherStations;
import com.mobileaviationtools.nav_fly.Search.SearchDialog;
import com.mobileaviationtools.nav_fly.Settings.Database.DatabaseDownloadDialog;
import com.mobileaviationtools.nav_fly.Settings.GeneralSettingsDialog;
import com.mobileaviationtools.nav_fly.Settings.Services.HomeAirportService;
import com.mobileaviationtools.nav_fly.Settings.HomeAirport.SelectedAirport;
import com.mobileaviationtools.nav_fly.Settings.ChartSettingsDialog;
import com.mobileaviationtools.nav_fly.Settings.SettingsObject;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;
import com.mobileaviationtools.weater_notam_data.notams.NotamCount;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;
import com.mobileaviationtools.weater_notam_data.notams.NotamResponseEvent;
import com.mobileaviationtools.weater_notam_data.notams.Notams;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.map.Map;
import org.oscim.renderer.BitmapRenderer;
import org.oscim.renderer.GLViewport;
import org.oscim.scalebar.DefaultMapScaleBar;
import org.oscim.scalebar.ImperialUnitAdapter;
import org.oscim.scalebar.MapScaleBar;
import org.oscim.scalebar.MapScaleBarLayer;
import org.oscim.scalebar.MetricUnitAdapter;
import org.oscim.theme.VtmThemes;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.OkHttpEngine;
import org.oscim.tiling.source.mvt.NextzenMvtTileSource;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MainActivity extends AppCompatActivity implements DialogInterface.OnDismissListener{
    final static boolean USE_CACHE = true;
    final static int REQUEST_EXTERNAL_STORAGE_ACCESS = 10;
    final static int REQUEST_INTERNET_ACCESS_SETUPAPP = 11;
    final static int REQUEST_LOCATION_GPS = 12;
    final static int REQUEST_WAKELOCK_SETUPAPP = 13;
    final static int REQUEST_SEARCH_DIALOG = 14;
    final String TAG = "MainActivity";

    public GlobalVars vars;

    MapView mMapView;
    CheckMap checkMap;
    MapPreferences mPrefs;
    VectorTileLayer mBaseLayer;
    AirportMarkersLayer mAirportMarkersLayer;
    NaviadMarkersLayer mNavaidsMarkersLayer;
    SelectionLayer mAirportSelectionLayer;
    AirspaceLayer mAirspaceLayer;
    Tracking trackingLayer;
    Boolean fromMenu;

    Boolean mapPosLockedToAirplanePos = true;

    Timer clockTimer;
    Timer weatherTimer;

    //private OfflineTileCache mCache;
    private SettingsObject settingsObject;
    TileSource mTileSource;
    protected BitmapTileLayer mBitmapLayer;

    private DefaultMapScaleBar mapScaleBar;

    private RouteListFragment routeListFragment;
    private NavigationButtonFragment menu;


    private ConnectStage connectStage;

    private WeatherStations stations;
    private NotamRetrieval notamRetrieval;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AirnavClient.deleteDatabaseFile(this, "room_airnav_chart.db");
        //AirnavClient.deleteDatabaseFile(this, "room_airnav.db");
        //AirnavClient.deleteDatabaseFile(this, "room_airnav_settings.db");

        super.onCreate(savedInstanceState);

        vars = new GlobalVars();
        vars.mainActivity = this;
        vars.context = this;

        Boolean test = false;

        if ((!Helpers.DatabaseExists(this, AirnavDatabase.DB_NAME)) || test) {
            fromMenu = false;
            StartupDialog startupDialog = StartupDialog.getInstance(vars);
            startupDialog.show(getSupportFragmentManager(), "Startup");
        }
        else {
            startApp();
        }
    }

    private void testDB()
    {
//        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
//        Airport airport = db.getAirport().getAirportByIdent("EHLE");
//        Log.i(TAG, "Found Airport: " + airport.name);
        AirnavChartsDatabase db = AirnavChartsDatabase.getInstance(vars.context);
        Chart[] charts = db.getCharts().getAllCharts();
        Log.i(TAG, "Retrieved " + charts.length + " charts");
        AirnavUserSettingsDatabase db1 = AirnavUserSettingsDatabase.getInstance(vars.context);
        List<Database> databases = db1.getDatabase().getLatestDownloadedDatabases();
        Log.i(TAG, "Downloaded databases: " + databases.size());
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        Log.i(TAG, dialog.toString());
        if (Helpers.DatabaseExists(this, AirnavDatabase.DB_NAME)) {

//            if (dialog instanceof Dialog)
//            {
//                Dialog d = (Dialog)dialog;
//                int i=0;
//                d.g
//            }

            if (!fromMenu)
                startApp();
        }
        else
        {
            // Show warning database not present
            startApp();
        }
    }

    private void startApp()
    {
        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapView);

        vars.map = mMapView.map();
        //mPrefs = new MapPreferences(MainActivity.class.getName(), this);
        //mPrefs.load(mMapView.map());

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED )
        {
            setupApp();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_ACCESS_SETUPAPP );
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED )
        {
            // set wakelock
            setupWakeLock();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WAKE_LOCK}, REQUEST_WAKELOCK_SETUPAPP );
        }


    }

    private void setupApp()
    {
        connectStage = ConnectStage.disconnected;
        vars.doDeviationLineFromLocation = new FspLocation("DeviationFromLocation");

        createSettingsObject();

        menu = (NavigationButtonFragment)getSupportFragmentManager().findFragmentById(R.id.menuFragment);
        setupMenuListerners();

        vars.dashboardFragment = (DashboardFragment)getSupportFragmentManager().findFragmentById(R.id.dashboardFragment);

        vars.map.layers().addGroup(vars.BASE_GROUP);

        setupMap();

        //setupInitialLocation();

        createLayers();

        // Temp test code

        vars.map.layers().addGroup(vars.ONLINETILES_GROUP);
        vars.map.layers().addGroup(vars.OVERLAYCHARTS_GROUP);
        vars.map.layers().addGroup(vars.AVIATIONMARKERS_GROUP);
        vars.map.layers().addGroup(vars.AIRSPACE_GROUP);
        vars.map.layers().addGroup(vars.ROUTE_GROUP);
        vars.map.layers().addGroup(vars.TRACK_GROUP);
        vars.map.layers().addGroup(vars.DEVIATIONLINE_GROUP);
        vars.map.layers().addGroup(vars.AIRPLANEMARKER_GROUP);

        // add overlay layers
        settingsObject.setupChartsOverlayLayers();
        settingsObject.setupOnlineTileProviders();

        setupAirspacesLayer();
        addMarkerLayers();
        addTrackingLayer();
        addAircraftLocationLayer();
        setupHomeLocation();
        addDeviationLineLayer();

        setupRouteFragment();
        setupWeatherStations();
        setupNotamsRetrieval();
        setupTimers();


        vars.map.events.bind(new Map.UpdateListener() {
            @Override
            public void onMapEvent(Event e, MapPosition mapPosition) {
                if (vars.mapCenterLocation == null) vars.mapCenterLocation = new FspLocation(mapPosition.getGeoPoint(),
                        "MapCenterLocation");
                else vars.mapCenterLocation.setGeopoint(mapPosition.getGeoPoint());

                if (checkMap == null) checkMap = new CheckMap(vars.map);
                if(checkMap.Changed()) {
                    mAirportMarkersLayer.UpdateAirports();
                    mNavaidsMarkersLayer.UpdateNavaids();
                    mAirspaceLayer.UpdateAirspaces();
                }

                vars.deviationLineLayer.drawDeviationLine(vars.doDeviationLineFromLocation, vars.mapCenterLocation);

                setMapDirectoinType(vars.mapDirectionType);

            }
        });
    }

    private void setMapDirectoinType(MapDirectionType type)
    {
        vars.mapDirectionType = type;
        MapPosition pos = vars.map.getMapPosition();
        switch (type)
        {
            case north: {
                pos.setBearing(0);
                vars.map.setMapPosition(pos);
                break;
            }
            case flight: {
                pos.setBearing(360-vars.airplaneLocation.getBearing());
                vars.map.setMapPosition(pos);
                break;
            }
        }
    }

    private void setupHomeLocation() {
        HomeAirportService homeAirportService = new HomeAirportService(vars);
        SelectedAirport airport = homeAirportService.getSelectedHomeAirport();
        if (airport != null)
        {
            Location location = new Location("StartLocation");
            if (airport.runway == null) {
                location.setLatitude(airport.airport.latitude_deg);
                location.setLongitude(airport.airport.longitude_deg);
            }
            else
            {
                String ident = airport.runwayIdent;
                boolean le = airport.runway.le_ident.equals(ident);
                location.setLatitude(le ? airport.runway.le_latitude_deg : airport.runway.he_latitude_deg);
                location.setLongitude(le ? airport.runway.le_longitude_deg : airport.runway.he_longitude_deg);
                location.setBearing((le ? airport.runway.le_heading_degT : airport.runway.he_heading_degT).floatValue());
            }
            FspLocation loc = new FspLocation(location);
            vars.airplaneLocation.Assign(loc);
            vars.mAircraftLocationLayer.UpdateLocation(vars.airplaneLocation);

            MapPosition position = vars.map.getMapPosition();
            position.setPosition(loc.getGeopoint());
            position.setZoom(9);
            vars.map.setMapPosition(position);
        }
    }

    public void setupTimers()
    {
        clockTimer = new Timer();
        clockTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vars.dashboardFragment.setZuluTime();
                    }
                });
            }
        }, 30000, 30000);

        weatherTimer = new Timer();
        weatherTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                FspLocation location = new FspLocation(vars.map.getMapPosition().getGeoPoint(), "Weatherlocation");
                if (Helpers.isConnected(MainActivity.this))
                    stations.getWeatherData(location, 100l);
                else
                    stations.getDatabaseWeather(location.getGeopoint(), 100l);

                notamRetrieval.startNotamRetrieval(false);

            }
        }, 1800000, 1800000);
        vars.dashboardFragment.setZuluTime();
    }

    private void setupWeatherStations()
    {
        routeListFragment.ToggleWeatherProgressVisibility(true);

        stations = new WeatherStations(this);

        stations.SetWeatherDataReceivedEvent(new WeatherStations.WeatherDataReceivedEvent() {
            @Override
            public void Received(WeatherStations stations) {
                routeListFragment.setWeatherStations(vars, stations);
                routeListFragment.ToggleWeatherProgressVisibility(false);
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        vars.dashboardFragment.setQnh(MainActivity.this.stations.getQNHInfo(vars.map.getMapPosition().getGeoPoint()));
                    }
                });

            }
        });
        FspLocation loc = new FspLocation(vars.map.getMapPosition().getGeoPoint(), "weatherLoc");
        if (Helpers.isConnected(this))
            stations.getWeatherData(loc, 100l);
        else
            stations.getDatabaseWeather(loc.getGeopoint(), 100l);
        // TODO Add code to retrieve notams
    }

    private void setupNotamsRetrieval()
    {
        routeListFragment.ToggleNotamsProgressVisibility(true);
        notamRetrieval = new NotamRetrieval(vars);
        notamRetrieval.setNotamsRetrievedResponseEvent(new NotamResponseEvent() {
            @Override
            public void OnNotamsResponse(Notams notams, NotamCount count, String message) {

            }

            @Override
            public void OnNotamsCountResponse(NotamCounts counts, String message) {
                routeListFragment.ToggleNotamsProgressVisibility(false);

            }

            @Override
            public void OnFailure(String message) {
                routeListFragment.ToggleNotamsProgressVisibility(false);
            }
        });
        notamRetrieval.startNotamRetrieval(false);
    }

    private void createSettingsObject()
    {
        settingsObject = new SettingsObject(vars);
        settingsObject.SetSettingsEvent(new SettingsObject.SettingsEvent() {
            @Override
            public void OnSettingChanged(SettingsObject.SettingType type, SettingsObject object) {

            }

            @Override
            public void OnSettingsProgress(SettingsObject.SettingType type, SettingsObject object, String message) {

            }

            @Override
            public void OnSettingsSaved(SettingsObject object) {

            }

            @Override
            public void OnSettingsLoaded(SettingsObject object) {

            }
        });
    }

    private void setupRouteFragment() {
        routeListFragment = (RouteListFragment)getSupportFragmentManager().findFragmentById(R.id.routeListFragment);
        routeListFragment.setGlobalVars(vars);

        routeListFragment.setChartEvents(new ChartEvents() {
            @Override
            public void OnChartCheckedEvent(Chart chart, Boolean checked) {
                settingsObject.chartsOverlayLayers.setChart(chart);
                settingsObject.updateChartsFromDB();
            }

            @Override
            public void OnChartSelected(Chart chart) {

            }
        });
    }

    public void addMarkerLayers() {
        mAirportMarkersLayer = new AirportMarkersLayer(vars.map, null, this);
        mAirportMarkersLayer.SetOnAirportSelected(new AirportSelected() {
            @Override
            public void Selected(Airport airport, GeoPoint geoPoint) {
                Log.i(TAG, "Airport selected: " + airport.ident);
                mAirportSelectionLayer.setAirportSelected(airport);
                if (vars.route != null)
                {
                    if (!vars.route.isStartAirportSet())
                    {
                        vars.route.setSelectedStartAirport(airport);
                        Log.i(TAG, "Route Start Airport selected: " + airport.ident);
                        return;
                    }

                    if (!vars.route.isEndAirportSet())
                    {
                        vars.route.setSelectedEndAirport(airport);
                        Log.i(TAG, "Route Start Airport selected: " + airport.ident);
                        vars.route.DrawRoute(vars.map);
                        return;
                    }

                    routeListFragment.ShowAirportInfo(airport);

                }

                routeListFragment.ShowAirportInfo(airport);
            }
        });
        vars.map.layers().add(mAirportMarkersLayer);

        mNavaidsMarkersLayer = new NaviadMarkersLayer(vars.map, null, this);
        vars.map.layers().add(mNavaidsMarkersLayer);

        mAirportSelectionLayer = new SelectionLayer(vars.map, null, this);
        vars.map.layers().add(mAirportSelectionLayer);
    }

    public void addTrackingLayer()
    {
        trackingLayer = new Tracking(vars);
    }

    public void addAircraftLocationLayer()
    {
        vars.airplaneLocation = new FspLocation(vars.map.getMapPosition().getGeoPoint(), "AirplaneLocation");
        vars.mAircraftLocationLayer = AircraftLocationLayer.createNewAircraftLayer(vars.map,
                this, vars.airplaneLocation);

        vars.map.layers().add(vars.mAircraftLocationLayer, vars.AIRPLANEMARKER_GROUP);
    }

    public void addDeviationLineLayer()
    {
        vars.doDeviationLineFromLocation = new FspLocation(vars.airplaneLocation);
        vars.deviationLineLayer = new DeviationLineLayer(vars.map);
        vars.deviationLineLayer.setupLayers(vars);
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item)
    {
        return true;
    }

    private enum  requestType
    {
        mbtiles,
        bitmap
    }
    private requestType request_Type;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (requestCode == REQUEST_INTERNET_ACCESS_SETUPAPP)
                setupApp();

            if (requestCode == REQUEST_WAKELOCK_SETUPAPP)
                setupWakeLock();

            if (requestCode == REQUEST_SEARCH_DIALOG)
                showSearchDialog();

//            if (requestCode == REQUEST_LOCATION_GPS)
//                setupInitialLocation();

            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void setupAirspacesLayer()
    {
        mAirspaceLayer = new AirspaceLayer(vars);
        mAirspaceLayer.SetFoundAirspacesEvent(new AirspaceLayer.FoundAirspacesEvent() {
            @Override
            public void OnAirspaces(Airspace[] airspaces) {
                AirspacesInfoFragment airspacesInfoFragment =
                        (AirspacesInfoFragment)getSupportFragmentManager().findFragmentById(R.id.airspacesInfoFragment);
                airspacesInfoFragment.ShowAirspacesInfo(airspaces);
            }
        });
    }

    private void setupWakeLock()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


//    void getMBTilesMapPerm()
//    {
//
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//        {
//            request_Type = requestType.mbtiles;
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_ACCESS );
//        }
//        else {
//            getMBTilesMap();
//        }
//    }
//
//    void getBitmapOverlayPerm()
//    {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
//        {
//            request_Type = requestType.bitmap;
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_ACCESS );
//        }
//        else {
//            getBitmapOverlay();
//        }
//    }
//
//    void getBitmapOverlay()
//    {
//        BoundingBox bb = new BoundingBox(52.31267664,5.38673401, 52.58511188, 5.71289063);
//        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String folder = downloadFolder.getAbsolutePath() + "/VAC_EHLE.png";
//
//        OverlayTileSource overlayTileSource = new OverlayTileSource(folder, bb);
//        overlayTileSource.open();
//        mBitmapLayer = new BitmapTileLayer(vars.map, overlayTileSource);
//        vars.map.layers().add(mBitmapLayer);
//    }
//
//    void viewportTest()
//    {
//        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String folder = downloadFolder.getAbsolutePath() + "/VAC_EHLE.png";
//        BitmapToTile bitmapToTile = new BitmapToTile();
//        //bitmapToTile.Test(mMapView, mMap, folder);
//        bitmapToTile.TransformTest();
//    }

//    void getMBTilesMap()
//    {
//        // ehaa_256@2x.mbtiles
//        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String folder = downloadFolder.getAbsolutePath() + "/ehaa_256@2x.mbtiles";
//
//        File f = new File(folder);
//        if (f.exists()) {
//            MBTilesTileSource mbTilesTileSource = new MBTilesTileSource(folder);
//            mbTilesTileSource.open();
//            mBitmapLayer = new BitmapTileLayer(vars.map, mbTilesTileSource);
//            vars.map.layers().add(mBitmapLayer);
//
//        }
//    }

//    void setupChartsOverlayLayers()
//    {
////        chartsGroupLayer = new GroupLayer(mMap);
////        mMap.layers().add(chartsGroupLayer);
//        int index = mMap.layers().size()-1;
//
//        chartsOverlayLayers = new ChartsOverlayLayers(this, mMap, index);
//        chartsOverlayLayers.InitChartsFromDB();
//    }

    void setupMap()
    {
//        mTileSource = OSciMap4TileSource.builder()
//                .httpFactory(new OkHttpEngine.OkHttpFactory())
//                .build();
//

        mTileSource = NextzenMvtTileSource.builder()
            .apiKey("X9Iq4O_GTZeKHy4_w-_q8w") // Put a proper API key
            .httpFactory(new OkHttpEngine.OkHttpFactory())
            //.locale("en")
            .build();

//        mTileSource = MapilionMvtTileSource.builder()
//                .httpFactory(new OkHttpEngine.OkHttpFactory())
//                .build();
//        String url = ((MapilionMvtTileSource) mTileSource).getUrl().toString();

        String url = ((NextzenMvtTileSource) mTileSource).getNextzenUrl();
        settingsObject.setBaseCache(mTileSource, url);
        mBaseLayer = vars.map.setBaseMap(mTileSource);

        vars.map.setTheme(VtmThemes.MAPZEN);

        vars.map.layers().add(new LabelLayer(vars.map, mBaseLayer), vars.BASE_GROUP);
    }

    void setupInitialLocation()
    {


        //TODO Setup for initial location..
        /* set initial position on first run */
        MapPosition pos = new MapPosition();
        vars.map.getMapPosition(pos);
        if (pos.x == 0.5 && pos.y == 0.5)
            vars.map.setMapPosition(52.4603, 5.5272, Math.pow(2, 14));
    }

    void createLayers() {
        mapScaleBar = new DefaultMapScaleBar(vars.map);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(vars.map, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5 * CanvasAdapter.getScale(), 0);
        vars.map.layers().add(mapScaleBarLayer, vars.BASE_GROUP);

    }

    private void setupMenuListerners()
    {
        menu.SetOnButtonClicked(new OnNavigationMemuItemClicked() {
            @Override
            public Boolean OnMenuItemClicked(View button, MenuItemType itemType) {
                switch (itemType){
                    case downloaddatabase:
                    {
//                        Intent dbDonwloadIntent = new Intent(MainActivity.this, DatabaseDownloadActivity.class);
//                        MainActivity.this.startActivity(dbDonwloadIntent);
                        DatabaseDownloadDialog databaseDownloadDialog = DatabaseDownloadDialog.getInstance(vars);
                        fromMenu = true;
                        databaseDownloadDialog.show(getSupportFragmentManager(), "DatabaseDownload");
                        break;
                    }
                    case maptype:
                    {
                        ChartSettingsDialog chartSettingsDialog = ChartSettingsDialog.getInstance(MainActivity.this, settingsObject);
                        fromMenu = true;
                        chartSettingsDialog.show(getSupportFragmentManager(), "ChartSettings");
                        break;
                    }
                    case settings:
                    {
                        GeneralSettingsDialog generalSettingsDialog = GeneralSettingsDialog.getInstance(MainActivity.this.vars);
                        fromMenu = true;
                        generalSettingsDialog.show(getSupportFragmentManager(), "GeneralSettings");
                        break;
                    }
                    case search:
                    {
                        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED )
                        {
                            showSearchDialog();
                        }
                        else
                        {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_SEARCH_DIALOG );
                        }
                        break;
                    }
                    case connectDisconnect:
                    {
                        ConnectionProcess();
                        break;
                    }

                    case tracking:
                    {
                        mapPosLockedToAirplanePos = !mapPosLockedToAirplanePos;
                        menu.SetTrackingItemIcon(mapPosLockedToAirplanePos);
                        break;
                    }
                    case mapDirection:
                    {
                        setMapDirectoinType(MapDirectionType.getNextDirectionType(vars.mapDirectionType));
                        menu.setDirectionBtnIcon(vars.mapDirectionType);
                        break;
                    }
                }
                return false;
            }
        });
    }

    private SearchDialog searchDialog;
    private void showSearchDialog()
    {
        searchDialog = SearchDialog.getInstance(vars, new SearchDialog.OnSearch() {
            @Override
            public void FoundStation(Station station) {
                GeoPoint g = null;
                if (station.airport != null) g = new GeoPoint(station.airport.latitude_deg, station.airport.longitude_deg);
                if (station.navaid != null) g = new GeoPoint(station.navaid.latitude_deg, station.navaid.longitude_deg);
                if (station.fix != null) g = new GeoPoint(station.fix.latitude_deg, station.fix.longitude_deg);
                if (g != null)
                {
                    MapPosition pos = vars.map.getMapPosition();
                    pos.setPosition(g);
                    vars.map.setMapPosition(pos);
                }
                searchDialog.dismiss();
            }
        });
        searchDialog.show(getSupportFragmentManager(), "Search");
    }

    private FspLocationProvider locationProvider;
    private void ConnectionProcess()
    {
        if (connectStage == ConnectStage.disconnected) {
            connectStage = ConnectStage.connecting;
            menu.SetConnectingIcon();
            locationProvider = new FspLocationProvider(vars);
            locationProvider.Start(new LocationEvents() {
                @Override
                public void OnLocationChanged(LocationProviderType type, FspLocation location, String message, Boolean success) {
                    if(success)
                    {
                        if (connectStage == ConnectStage.connecting) {
                            menu.SetConnectDisConnectIcon(true);
                            trackingLayer.start(vars.route);
                            Log.i(TAG, "Location tracking started");
                        }
                        connectStage = ConnectStage.connected;
                        Log.i("OnLocationChanged", "Success Message: " + message);
                        if (location != null) {
                            vars.airplaneLocation.Assign(location);
                            vars.doDeviationLineFromLocation.Assign(location);
//                            Log.i("OnLocationChanged", "Location Changed: " + vars.airplaneLocation.getLatitude() + " "
//                                    + vars.airplaneLocation.getLongitude() + " " + vars.airplaneLocation.getBearing() + " " + vars.airplaneLocation.getSpeed());
                            //location.setBearing(90);
                            trackingLayer.setLocation(vars.airplaneLocation);
                            vars.mAircraftLocationLayer.UpdateLocation(vars.airplaneLocation);
                            vars.dashboardFragment.setLocation(vars.airplaneLocation);
                            //mMap.render();

                            MapPosition pos = vars.map.getMapPosition();
                            if (mapPosLockedToAirplanePos)
                            {
                                pos.setPosition(vars.airplaneLocation.getGeopoint());
                                vars.map.setMapPosition(pos);
                            }

                            if (vars.mapDirectionType==MapDirectionType.flight)
                            {
                                pos.setBearing(360-vars.airplaneLocation.getBearing());
                            }

                            vars.map.setMapPosition(pos);
                        }
                    }
                    else
                    {
                        menu.SetConnectDisConnectIcon(false);
                        connectStage = ConnectStage.disconnected;
                        trackingLayer.stop();
                        Log.i("OnLocationChanged", "Error Message: " + message);
                        if (message.startsWith("Error")) {
                            Toast error = Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG);
                            error.show();
                        }
                    }
                }
            });
        }
        if (connectStage == ConnectStage.connected)
        {
            menu.SetConnectDisConnectIcon(false);
            if (locationProvider != null)
            {
                locationProvider.Stop();
            }
            connectStage = ConnectStage.disconnected;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            //mPrefs.load(mMapView.map());
            mMapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        //mPrefs.save(mMapView.map());
        mMapView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (locationProvider != null && connectStage==ConnectStage.connected)
            locationProvider.Stop();

        if (mapScaleBar != null)
            mapScaleBar.destroy();

        settingsObject.dispose();
        mMapView.onDestroy();
        super.onDestroy();
    }
}
