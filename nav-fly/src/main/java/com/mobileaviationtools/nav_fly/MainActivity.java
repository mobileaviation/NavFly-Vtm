package com.mobileaviationtools.nav_fly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
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
import android.widget.Toast;

import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Api.AirportsAPIDataSource;
import com.mobileaviationtools.airnavdata.Api.NavaidAPIDataSource;
import com.mobileaviationtools.airnavdata.Api.RetrofitTest;
import com.mobileaviationtools.airnavdata.Firebase.AirportsDataSource;
import com.mobileaviationtools.airnavdata.Firebase.FBStatistics;
import com.mobileaviationtools.airnavdata.Firebase.NavaidDataSource;
import com.mobileaviationtools.airnavdata.Models.Statistics;
import com.mobileaviationtools.nav_fly.Classes.CheckMap;
import com.mobileaviationtools.nav_fly.Markers.Airport.AirportMarkersLayer;
import com.mobileaviationtools.nav_fly.Markers.Navaids.NaviadMarkersLayer;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.android.cache.TileCache;
import org.oscim.android.tiling.mbtiles.MBTilesTileSource;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Color;
import org.oscim.core.MapPosition;
import org.oscim.event.Event;
import org.oscim.event.Gesture;
import org.oscim.event.MotionEvent;
import org.oscim.layers.GroupLayer;
import org.oscim.layers.tile.bitmap.BitmapTileLayer;
import org.oscim.layers.tile.buildings.BuildingLayer;
import org.oscim.layers.tile.vector.VectorTileLayer;
import org.oscim.layers.tile.vector.labeling.LabelLayer;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.Style;
import org.oscim.layers.vector.PathLayer;
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

import java.io.File;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    final static boolean USE_CACHE = false;
    final static int REQUEST_EXTERNAL_STORAGE_ACCESS = 10;
    final String TAG = "MainActivity";

    MapView mMapView;
    Map mMap;
    CheckMap checkMap;
    MapPreferences mPrefs;
    VectorTileLayer mBaseLayer;
    AirportMarkersLayer mAirportMarkersLayer;
    NaviadMarkersLayer mNavaidsMarkersLayer;

    private TileCache mCache;
    TileSource mTileSource;
    protected BitmapTileLayer mBitmapLayer;

    private DefaultMapScaleBar mapScaleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMapView = (MapView) findViewById(R.id.mapView);
        mMap = mMapView.map();



        mPrefs = new MapPreferences(MainActivity.class.getName(), this);



        setupMap();
        createLayers();
        //getMBTilesMapPerm();
        //setupPathLayer();
        //setupVectorLayer();
        testMarkerLayerGestures();

//        android.view.MotionEvent e = android.view.MotionEvent.obtain(0,0, android.view.MotionEvent.ACTION_MOVE,0,0,0);
//        mMap.input.fire(null, new AndroidMotionEvent().wrap(e));
        mMap.events.bind(new Map.UpdateListener() {
            @Override
            public void onMapEvent(Event e, MapPosition mapPosition) {
                //Log.i(TAG, "OnMapEvent + " + e.toString());
                if (checkMap == null) checkMap = new CheckMap(mMap);
                if(checkMap.Changed()) {
                    mAirportMarkersLayer.UpdateAirports();
                    mNavaidsMarkersLayer.UpdateNavaids();
                }
            }
        });
    }

    public void testMarkerLayerGestures() {
        mAirportMarkersLayer = new AirportMarkersLayer(mMap, null, this);
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
        int id = item.getItemId();

        if (id == R.id.firebase_test_menuitem)
        {
            FBStatistics statistics = new FBStatistics(this);
            statistics.OnStatisticsEvent = new FBStatistics.StatisticsEventListerner() {
                @Override
                public void OnStatistics(Statistics statistics) {
                    Log.i(TAG, "Statistics :");
                    AirportsDataSource airportsDataSource = new AirportsDataSource(MainActivity.this);
                    airportsDataSource.ReadAirportData(statistics.AirportsCount);

                    NavaidDataSource navaidDataSource = new NavaidDataSource(MainActivity.this);
                    navaidDataSource.ReadNavaidData(statistics.NavaidsCount);
                }
            };

            statistics.FillStatistics();
        }

        if (id == R.id.api_test_menuitem)
        {

            AirnavClient client = new AirnavClient(this);
            client.StartDownload();

        }

        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_STORAGE_ACCESS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    getMBTilesMap();
                }
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    void setupVectorLayer()
    {
        Style lineStyle = Style.builder()
                .strokeColor(Color.BLUE)
                .strokeWidth(5).build();

        VectorLayer vectorLayer = new VectorLayer(mMap)
        {
            @Override
            public boolean onGesture(Gesture g, MotionEvent e) {
                if (g instanceof Gesture.Tap) {
                    if (contains(e.getX(), e.getY())) {
                        Toast.makeText(MainActivity.this, "PathLayer tap\n" + mMap.viewport().fromScreenPoint(e.getX(), e.getY()), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        };

        double[] packedCoordinates = new double[4];
        packedCoordinates[0] = 5.5272;
        packedCoordinates[1] = 52.4603;
        packedCoordinates[2] = 4.8336;
        packedCoordinates[3] = 53.1153;
        LineDrawable line = new LineDrawable(packedCoordinates, lineStyle);



        mMap.layers().add(vectorLayer);
        vectorLayer.add(line);
        vectorLayer.update();

    }

    void setupPathLayer()
    {
        Style lineStyle = Style.builder()
                .fillColor(Color.BLUE)
                .strokeColor(Color.BLACK)
                .strokeWidth(2).build();

        PathLayer pathLayer = new PathLayer(mMap, lineStyle){
            @Override
            public boolean onGesture(Gesture g, MotionEvent e) {
                if (g instanceof Gesture.Tap) {
                    if (contains(e.getX(), e.getY())) {
                        Toast.makeText(MainActivity.this, "PathLayer tap\n" + mMap.viewport().fromScreenPoint(e.getX(), e.getY()), Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }
                return false;
            }
        };
        mMap.layers().add(pathLayer);

        drawlineLelystadTexel(pathLayer);
    }

    void drawlineLelystadTexel(PathLayer layer)
    {
        double[] packedCoordinates = new double[4];
        packedCoordinates[0] = 5.5272;
        packedCoordinates[1] = 52.4603;
        packedCoordinates[2] = 4.8336;
        packedCoordinates[3] = 53.1153;


        layer.setLineString(packedCoordinates);
    }

    void getMBTilesMapPerm()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE_ACCESS );
        }
        else {
            getMBTilesMap();
        }
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

    void setupMap()
    {
        mTileSource = OSciMap4TileSource.builder()
                .httpFactory(new OkHttpEngine.OkHttpFactory())
                .build();

        if (USE_CACHE) {
            mCache = new TileCache(this, null, "tile.db");
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
