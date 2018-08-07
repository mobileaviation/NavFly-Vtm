package com.mobileaviationtools.nav_fly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import org.oscim.android.MapPreferences;
import org.oscim.android.MapView;
import org.oscim.android.cache.TileCache;
import org.oscim.android.tiling.mbtiles.MBTilesTileSource;
import org.oscim.backend.CanvasAdapter;
import org.oscim.core.MapPosition;
import org.oscim.layers.GroupLayer;
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
import org.oscim.tiling.source.oscimap4.OSciMap4TileSource;

import java.io.File;

public class MainActivity extends AppCompatActivity {
    final static boolean USE_CACHE = false;
    final static int REQUEST_EXTERNAL_STORAGE_ACCESS = 10;

    MapView mMapView;
    Map mMap;
    MapPreferences mPrefs;
    VectorTileLayer mBaseLayer;
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
        getMBTilesMapPerm();
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
            mMap.setMapPosition(53.08, 8.83, Math.pow(2, 16));
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
