package com.mobileaviationtools.nav_fly.Location;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.mobileaviationtools.nav_fly.MainActivity;

public class FspGPSLocationProvider implements IFspLocationProvider{
    final static int REQUEST_LOCATION_GPS = 12;
    final String TAG = "FspGPSLocationProvider";

    public FspGPSLocationProvider(Context context) {
        this.context = context;
    }

    private LocationEvents locationEvents;

    public boolean setup() {
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        gpsAvailable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsAvailable) {
            // Show some dialog and revert to settings page
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            context.startActivity(intent);
            return false;
        }
        else
            return true;
    }

    public boolean start(LocationEvents locationEvents)
    {
        this.locationEvents = locationEvents;
        if (gpsAvailable)
            return setupGpsLocation();
        else return false;
    }

    public boolean stop()
    {
        locationManager.removeUpdates(locationListener);
        return true;
    }

    private Boolean setupGpsLocation() {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (context instanceof MainActivity) {
                String[] perms = new String[2];
                perms[0] = Manifest.permission.ACCESS_FINE_LOCATION;
                perms[1] = Manifest.permission.ACCESS_COARSE_LOCATION;
                ActivityCompat.requestPermissions((MainActivity)context, perms, 12);
            }
            return false;
        }
        setupLocationListener();
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 0, locationListener);
        return true;
    }

    private Context context;
    private boolean gpsAvailable;
    private LocationManager locationManager;

    private LocationListener locationListener;
    private void setupLocationListener()
    {
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.gps,
                        FspLocation.getInstance(location), "New Location", true);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };
    }
}
