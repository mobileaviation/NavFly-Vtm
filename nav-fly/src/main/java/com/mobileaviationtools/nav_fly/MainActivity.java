package com.mobileaviationtools.nav_fly;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.aircraft.Types.PiperArcher;
import com.mobileaviationtools.airnavdata.AirnavChartsDatabase;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.AirnavUserSettingsDatabase;
import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.airnavdata.Entities.Database;
import com.mobileaviationtools.nav_fly.Base.BaseChart;
import com.mobileaviationtools.nav_fly.Classes.BaseChartType;
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
import com.mobileaviationtools.nav_fly.Route.HeightMap.HeightMapFragment;
import com.mobileaviationtools.nav_fly.Route.HeightMap.TrackPoints;
import com.mobileaviationtools.nav_fly.Route.Info.ChartEvents;
import com.mobileaviationtools.nav_fly.Route.Notams.NotamRetrieval;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.RouteEvents;
import com.mobileaviationtools.nav_fly.Route.RouteListFragment;
import com.mobileaviationtools.nav_fly.Route.Waypoint;
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
import com.mobileaviationtools.nav_fly.Tracks.LoadLogDialog;
import com.mobileaviationtools.nav_fly.Tracks.PlaybackLayer;
import com.mobileaviationtools.weater_notam_data.notams.NotamCount;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;
import com.mobileaviationtools.weater_notam_data.notams.NotamResponseEvent;
import com.mobileaviationtools.weater_notam_data.notams.Notams;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
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
import org.oscim.tiling.source.mapfile.MapFileTileSource;
import org.oscim.tiling.source.mvt.NextzenMvtTileSource;
import org.oscim.tiling.source.mvt.OpenMapTilesMvtTileSource;
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MainActivity extends BaseActivity implements DialogInterface.OnDismissListener{
    final String TAG = "MainActivity";

    CheckMap checkMap;
    AirportMarkersLayer mAirportMarkersLayer;
    NaviadMarkersLayer mNavaidsMarkersLayer;
    SelectionLayer mAirportSelectionLayer;
    AirspaceLayer mAirspaceLayer;
    Tracking trackingLayer;
    PlaybackLayer playbackLayer;

    Boolean fromMenu;

    Boolean mapPosLockedToAirplanePos = true;

    Timer clockTimer;
    Timer weatherTimer;

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
        //AirnavClient.deleteDatabaseFile(this, "room_airnav_route.db");
        super.onCreate(savedInstanceState);

        Boolean test = false;


        SharedPreferences databasePrefs = this.getSharedPreferences("Database", MODE_PRIVATE);
        Boolean databaseInitialized = databasePrefs.getBoolean("databaseInitialized", false);
        databaseInitialized = databaseInitialized && Helpers.DatabaseExists(this, AirnavDatabase.DB_NAME);

        //if ((!Helpers.DatabaseExists(this, AirnavDatabase.DB_NAME)) || test) {
        if(!databaseInitialized){
            fromMenu = false;
            StartupDialog startupDialog = StartupDialog.getInstance(vars);
            startupDialog.show(getSupportFragmentManager(), "Startup");
            vars.baseChartType = BaseChartType.opensciencemaps;
            databasePrefs.edit().putString("BaseChartType",vars.baseChartType.toString()).apply();
        }
        else {
            String baseChartTypeStr = databasePrefs.getString("BaseChartType", "unk");
            if (baseChartTypeStr.equals("unk"))
            {
                vars.baseChartType = BaseChartType.opensciencemaps;
                databasePrefs.edit().putString("BaseChartType",vars.baseChartType.toString()).apply();
            }
            else
                vars.baseChartType = BaseChartType.valueOf(baseChartTypeStr);
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED )
        {
            setupApp();
        }
        else
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.INTERNET}, REQUEST_INTERNET_ACCESS_SETUPAPP );
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

        createLayers();

        vars.map.layers().addGroup(vars.ONLINETILES_GROUP);
        vars.map.layers().addGroup(vars.OVERLAYCHARTS_GROUP);
        vars.map.layers().addGroup(vars.AVIATIONMARKERS_GROUP);
        vars.map.layers().addGroup(vars.AIRSPACE_GROUP);
        vars.map.layers().addGroup(vars.ROUTE_BUFFERS_GROUP);
        vars.map.layers().addGroup(vars.ROUTE_GROUP);
        vars.map.layers().addGroup(vars.TRACK_GROUP);
        vars.map.layers().addGroup(vars.DEVIATIONLINE_GROUP);
        vars.map.layers().addGroup(vars.AIRPLANEMARKER_GROUP);

        // add overlay layers
        vars.settingsObject.setupChartsOverlayLayers();
        vars.settingsObject.setupOnlineTileProviders();

        setupAirspacesLayer();
        addMarkerLayers();
        addTrackingLayer();
        addPlaybackLayer();
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
        vars.settingsObject = new SettingsObject(vars);
        vars.settingsObject.SetSettingsEvent(new SettingsObject.SettingsEvent() {
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
                vars.settingsObject.chartsOverlayLayers.setChart(chart);
                vars.settingsObject.updateChartsFromDB();
            }

            @Override
            public void OnChartSelected(Chart chart) {

            }
        });

        routeListFragment.setRouteEvents(new RouteEvents() {
            @Override
            public void NewWaypointInserted(Route route, Waypoint newWaypoint) {
                setHeightMapFragment(route, true);
            }

            @Override
            public void WaypointUpdated(Route route, Waypoint updatedWaypoint) {
                setHeightMapFragment(route, true);
            }

            @Override
            public void NewRouteCreated(Route route) {

            }

            @Override
            public void RouteUpdated(Route route) {
                setHeightMapFragment(route, true);
            }

            @Override
            public void RouteOpened(Route route) {
                setHeightMapFragment(route, false);
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
                        return;
                    }

                    routeListFragment.ShowAirportInfo(airport);

                }

                routeListFragment.ShowAirportInfo(airport);
            }
        });
        vars.map.layers().add(mAirportMarkersLayer);//, vars.AVIATIONMARKERS_GROUP );

        mNavaidsMarkersLayer = new NaviadMarkersLayer(vars.map, null, this);
        vars.map.layers().add(mNavaidsMarkersLayer);//, vars.AVIATIONMARKERS_GROUP );

        mAirportSelectionLayer = new SelectionLayer(vars.map, null, this);
        vars.map.layers().add(mAirportSelectionLayer);//, vars.AVIATIONMARKERS_GROUP );
    }

    public void addTrackingLayer()
    {
        trackingLayer = new Tracking(vars);
    }

    public void addPlaybackLayer()
    {
        int c = Color.get(0xF4, 0x6E, 0x8F ); //.parseColor("fff46e8f");
        playbackLayer = new PlaybackLayer(vars.map, c, 3 * CanvasAdapter.getScale() );
        vars.map.layers().add(playbackLayer, vars.TRACK_GROUP);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (requestCode == REQUEST_INTERNET_ACCESS_SETUPAPP)
                setupApp();

            if (requestCode == REQUEST_SEARCH_DIALOG)
                showSearchDialog();

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



    void setupMap()
    {
        vars.baseChart = new BaseChart(vars);
        vars.baseChart.setBaseCharts(vars.baseChartType);

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
                        ChartSettingsDialog chartSettingsDialog = ChartSettingsDialog.getInstance(MainActivity.this, vars.settingsObject, vars);
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
                    case loadTrack:
                    {
                        View view = findViewById(R.id.main);
                        LoadLogDialog loadLogDialog = new LoadLogDialog(vars, view, new LoadLogDialog.LoadLog() {
                            @Override
                            public void LogSelected(Long TrackLogId) {
                                setTracklogFragment(TrackLogId);
                            }
                        });
                        loadLogDialog.show();
                        break;
                    }
                    case appLocking:
                    {
                        vars.appLocked = !vars.appLocked;
                        menu.SetApplockedIcon(vars.appLocked);
                        break;
                    }
                }
                return false;
            }
        });
    }

    private void setTracklogFragment(Long trackLogId)
    {
        HeightMapFragment heightMapFragment = (HeightMapFragment)getSupportFragmentManager().findFragmentById(R.id.heightMapFragment);
        heightMapFragment.setVisibility(View.VISIBLE);


        HeightMapFragment.HeightMapFragmentEvents events = new HeightMapFragment.HeightMapFragmentEvents() {
            @Override
            public void OnCloseBtnClicked() {
                playbackLayer.ClearTrack();
            }

            @Override
            public void OnLocationChanged(FspLocation location) {
                // Setup the location of the aircraft and chart
                setLocation(location);
            }

            @Override
            public void OnTrackLoaded(TrackPoints points) {
                // Draw layer met track
                playbackLayer.DrawTrack(points);
            }
        };

        heightMapFragment.heightMapFragmentEvents = events;
        heightMapFragment.setupTrackHeightMap(vars, trackLogId);
    }

    private void setHeightMapFragment(Route route, Boolean parseFromService)
    {
        vars.route = route;
        vars.heightMapFragment = (HeightMapFragment)getSupportFragmentManager().findFragmentById(R.id.heightMapFragment);
        vars.heightMapFragment.setVisibility(View.VISIBLE);

        vars.heightMapFragment.setupRouteHeightMap(vars, null, parseFromService);
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
                            setLocation(location);
                            trackingLayer.setLocation(vars.airplaneLocation);
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

    private void setLocation(FspLocation location)
    {
        vars.airplaneLocation.Assign(location);
        vars.doDeviationLineFromLocation.Assign(location);

        vars.mAircraftLocationLayer.UpdateLocation(vars.airplaneLocation);

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

        if (vars.route != null)
        {
            if (vars.route.getLegs().size()>0)
            {
                vars.route.setAirplaneLocation(location);
            }
            vars.dashboardFragment.setLocation(vars.airplaneLocation, vars.route.getIndicatedAirspeed());
        }
        else
        {
            vars.dashboardFragment.setLocation(vars.airplaneLocation, 100d);
        }

        if (vars.heightMapFragment != null)
        {
            vars.heightMapFragment.setLocation(location);
        }

        vars.map.setMapPosition(pos);
    }

    @Override
    public boolean onKeyDown(final int keyCode, final KeyEvent event)
    {
        Log.i(TAG, "Key Pressed: " + keyCode);
        if (keyCode==4) {
            if (vars.appLocked)
                return false;
            else {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Are you sure you want to stop navigating?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                MainActivity.this.finish();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        }
        return super.onKeyDown(keyCode, event);
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
        super.onPause();
        if (vars.appLocked)
        {
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
        else {

            if (mMapView != null)
                mMapView.onPause();
        }
    }

    @Override
    protected void onDestroy() {
        if (locationProvider != null && connectStage==ConnectStage.connected)
            locationProvider.Stop();

        if (mapScaleBar != null)
            mapScaleBar.destroy();

        vars.settingsObject.dispose();
        mMapView.onDestroy();
        super.onDestroy();
    }
}
