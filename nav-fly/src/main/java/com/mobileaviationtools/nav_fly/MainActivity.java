package com.mobileaviationtools.nav_fly;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Environment;
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

import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Chart;
import com.mobileaviationtools.nav_fly.Classes.CheckMap;
import com.mobileaviationtools.nav_fly.Layers.AirspaceLayer;
import com.mobileaviationtools.nav_fly.Layers.ChartsOverlayLayers;
import com.mobileaviationtools.nav_fly.Layers.SelectionLayer;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkersLayer;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportSelected;
import com.mobileaviationtools.nav_fly.Markers.Navaids.NaviadMarkersLayer;
import com.mobileaviationtools.nav_fly.Menus.MenuItemType;
import com.mobileaviationtools.nav_fly.Menus.NavigationButtonFragment;
import com.mobileaviationtools.nav_fly.Menus.OnNavigationMemuItemClicked;
import com.mobileaviationtools.nav_fly.Route.Info.ChartEvents;
import com.mobileaviationtools.nav_fly.Route.Route;
import com.mobileaviationtools.nav_fly.Route.RouteListFragment;
import com.mobileaviationtools.nav_fly.Test.BitmapToTile;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.android.cache.OfflineTileCache;
import org.oscim.android.cache.TileCache;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.android.tiling.Overlay.OverlayTileSource;
import org.oscim.android.tiling.mbtiles.MBTilesTileSource;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.layers.BitmapLayer;
import org.oscim.layers.BitmapLocationLayer;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.VectorLayer;
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
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;
import org.oscim.utils.math.MathUtils;

import java.io.File;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;


public class MainActivity extends AppCompatActivity {
    final static boolean USE_CACHE = true;
    final static int REQUEST_EXTERNAL_STORAGE_ACCESS = 10;
    final String TAG = "MainActivity";

    MapView mMapView;
    Map mMap;
    CheckMap checkMap;
    MapPreferences mPrefs;
    VectorTileLayer mBaseLayer;
    AirportMarkersLayer mAirportMarkersLayer;
    NaviadMarkersLayer mNavaidsMarkersLayer;
    AirspaceLayer mAirspaceLayer;
    GroupLayer chartsGroupLayer;
    ChartsOverlayLayers chartsOverlayLayers;

    private OfflineTileCache mCache;
    TileSource mTileSource;
    protected BitmapTileLayer mBitmapLayer;

    private DefaultMapScaleBar mapScaleBar;

    private RouteListFragment routeListFragment;
    private NavigationButtonFragment menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //AirnavClient.deleteDatabaseFile(this, "room_airnav_chart.db");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMap = mMapView.map();

        menu = (NavigationButtonFragment)getSupportFragmentManager().findFragmentById(R.id.menuFragment);
        setupMenuListerners();

        mPrefs = new MapPreferences(MainActivity.class.getName(), this);

        setupRouteFragment();

        setupMap();
        createLayers();

        //getMBTilesMapPerm();
        //getBitmapOverlayPerm();
        setupChartsOverlayLayers();
        //viewportTest();

        setupAirspacesLayer();
        addMarkerLayers();

        mMap.events.bind(new Map.UpdateListener() {
            @Override
            public void onMapEvent(Event e, MapPosition mapPosition) {
                if (checkMap == null) checkMap = new CheckMap(mMap);
                if(checkMap.Changed()) {
                    mAirportMarkersLayer.UpdateAirports();
                    mNavaidsMarkersLayer.UpdateNavaids();
                    mAirspaceLayer.UpdateAirspaces();
                }
            }
        });
    }

    private void setupRouteFragment() {
        routeListFragment = (RouteListFragment)getSupportFragmentManager().findFragmentById(R.id.routeListFragment);
        routeListFragment.setMap(mMap);
        routeListFragment.setChartEvents(new ChartEvents() {
            @Override
            public void OnChartCheckedEvent(Chart chart, Boolean checked) {
                chartsOverlayLayers.setChart(chart);
            }

            @Override
            public void OnChartSelected(Chart chart) {

            }
        });
    }

    public void addMarkerLayers() {
        mAirportMarkersLayer = new AirportMarkersLayer(mMap, null, this);
        mAirportMarkersLayer.SetOnAirportSelected(new AirportSelected() {
            @Override
            public void Selected(Airport airport, GeoPoint geoPoint) {
                Log.i(TAG, "Airport selected: " + airport.ident);
                Route route = routeListFragment.getRoute();
                if (route != null)
                {
                    if (!route.isStartAirportSet())
                    {
                        route.setSelectedStartAirport(airport);
                        Log.i(TAG, "Route Start Airport selected: " + airport.ident);
                        return;
                    }

                    if (!route.isEndAirportSet())
                    {
                        route.setSelectedEndAirport(airport);
                        Log.i(TAG, "Route Start Airport selected: " + airport.ident);
                        route.DrawRoute(mMap);
                        return;
                    }

                    routeListFragment.ShowAirportInfo(airport);

                }

                routeListFragment.ShowAirportInfo(airport);
            }
        });
        mMap.layers().add(mAirportMarkersLayer);

        mNavaidsMarkersLayer = new NaviadMarkersLayer(mMap, null, this);
        mMap.layers().add(mNavaidsMarkersLayer);
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
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_ACCESS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    if (request_Type==requestType.mbtiles) getMBTilesMap();
                    if (request_Type==requestType.bitmap) getBitmapOverlay();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void setupAirspacesLayer()
    {
        mAirspaceLayer = new AirspaceLayer(mMap, this);
    }


    void getMBTilesMapPerm()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            request_Type = requestType.mbtiles;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_ACCESS );
        }
        else {
            getMBTilesMap();
        }
    }

    void getBitmapOverlayPerm()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            request_Type = requestType.bitmap;
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_ACCESS );
        }
        else {
            getBitmapOverlay();
        }
    }

    void getBitmapOverlay()
    {
        BoundingBox bb = new BoundingBox(52.31267664,5.38673401, 52.58511188, 5.71289063);
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String folder = downloadFolder.getAbsolutePath() + "/VAC_EHLE.png";

        OverlayTileSource overlayTileSource = new OverlayTileSource(folder, bb);
        overlayTileSource.open();
        mBitmapLayer = new BitmapTileLayer(mMap, overlayTileSource);
        mMap.layers().add(mBitmapLayer);
    }

    void viewportTest()
    {
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String folder = downloadFolder.getAbsolutePath() + "/VAC_EHLE.png";
        BitmapToTile bitmapToTile = new BitmapToTile();
        //bitmapToTile.Test(mMapView, mMap, folder);
        bitmapToTile.TransformTest();
    }

    void getMBTilesMap()
    {
        // ehaa_256@2x.mbtiles
        File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        String folder = downloadFolder.getAbsolutePath() + "/ehaa_256@2x.mbtiles";

        File f = new File(folder);
        if (f.exists()) {
            MBTilesTileSource mbTilesTileSource = new MBTilesTileSource(folder);
            mbTilesTileSource.open();
            mBitmapLayer = new BitmapTileLayer(mMap, mbTilesTileSource);
            mMap.layers().add(mBitmapLayer);

        }
    }

    void setupChartsOverlayLayers()
    {
//        chartsGroupLayer = new GroupLayer(mMap);
//        mMap.layers().add(chartsGroupLayer);
        int index = mMap.layers().size()-1;

        chartsOverlayLayers = new ChartsOverlayLayers(this, mMap, index);
        chartsOverlayLayers.InitChartsFromDB();
    }

    void setupMap()
    {
        mTileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();

        if (USE_CACHE) {
            mCache = new OfflineTileCache(this, null, "airnav_tiles_cache.db");
            long s = 512 * (1 << 10);
            mCache.setCacheSize(512 * (1 << 10));
            mTileSource.setCache(mCache);

        }

        mBaseLayer = mMap.setBaseMap(mTileSource);

        /* set initial position on first run */
        MapPosition pos = new MapPosition();
        mMap.getMapPosition(pos);
        if (pos.x == 0.5 && pos.y == 0.5)
            mMap.setMapPosition(52.4603, 5.5272, Math.pow(2, 14));
    }

    void createLayers() {
        GroupLayer groupLayer = new GroupLayer(mMap);
        groupLayer.layers.add(new BuildingLayer(mMap, mBaseLayer));
        groupLayer.layers.add(new LabelLayer(mMap, mBaseLayer));
        mMap.layers().add(groupLayer);

        mapScaleBar = new DefaultMapScaleBar(mMap);
        mapScaleBar.setScaleBarMode(DefaultMapScaleBar.ScaleBarMode.BOTH);
        mapScaleBar.setDistanceUnitAdapter(MetricUnitAdapter.INSTANCE);
        mapScaleBar.setSecondaryDistanceUnitAdapter(ImperialUnitAdapter.INSTANCE);
        mapScaleBar.setScaleBarPosition(MapScaleBar.ScaleBarPosition.BOTTOM_LEFT);

        MapScaleBarLayer mapScaleBarLayer = new MapScaleBarLayer(mMap, mapScaleBar);
        BitmapRenderer renderer = mapScaleBarLayer.getRenderer();
        renderer.setPosition(GLViewport.Position.BOTTOM_LEFT);
        renderer.setOffset(5 * CanvasAdapter.getScale(), 0);
        mMap.layers().add(mapScaleBarLayer);


        mMap.setTheme(VtmThemes.DEFAULT);
    }

    private void setupMenuListerners()
    {
        menu.SetOnButtonClicked(new OnNavigationMemuItemClicked() {
            @Override
            public Boolean OnMenuItemClicked(View button, MenuItemType itemType) {
                switch (itemType){
                    case downloaddatabase:
                    {
                        Intent dbDonwloadIntent = new Intent(MainActivity.this, DatabaseDownloadActivity.class);
                        MainActivity.this.startActivity(dbDonwloadIntent);
                        break;
                    }
                    case maptype:
                    {
                        String url = "http://opensciencemap.org/tiles/vtm/{Z}/{X}/{Y}.vtm";
                        BoundingBox box = mMap.getBoundingBox(5);
                        OfflineTileCache offlineTileCache = new OfflineTileCache(MainActivity.this, null, "testTile.db");
                        offlineTileCache.DownloadTiles(box, url);
                        break;
                    }
                    case search:
                    {
                        SelectionLayer s = new SelectionLayer(mMap, MainActivity.this);
                        s.setAirportSelected(null);
                    }

                }

                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        mPrefs.load(mMapView.map());
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        mPrefs.save(mMapView.map());
        mMapView.onPause();

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (mapScaleBar != null)
            mapScaleBar.destroy();

        if (mCache != null)
            mCache.dispose();

        mMapView.onDestroy();

        super.onDestroy();
    }
}
