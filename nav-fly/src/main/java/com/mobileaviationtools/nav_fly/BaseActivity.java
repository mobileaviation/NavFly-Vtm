package com.mobileaviationtools.nav_fly;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.example.aircraft.Types.PiperArcher;

import org.oscim.android.MapView;

import static com.mobileaviationtools.nav_fly.MainActivity.REQUEST_WAKELOCK_SETUPAPP;

public class BaseActivity extends AppCompatActivity {
    public GlobalVars vars;
    MapView mMapView;
    String TAG = "BaseActivity";

    final static boolean USE_CACHE = true;
    final static int REQUEST_EXTERNAL_STORAGE_ACCESS = 10;
    final static int REQUEST_INTERNET_ACCESS_SETUPAPP = 11;
    final static int REQUEST_LOCATION_GPS = 12;
    final static int REQUEST_WAKELOCK_SETUPAPP = 13;
    final static int REQUEST_SEARCH_DIALOG = 14;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vars = new GlobalVars();
        vars.mainActivity = this;
        vars.context = this;
        vars.aircraft = new PiperArcher();
        vars.appLocked = false;

        setContentView(R.layout.activity_main);
        mMapView = (MapView) findViewById(R.id.mapView);
        vars.map = mMapView.map();

        doSetupWakeLock();
        askReorderTaskPermission();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
        {
            if (requestCode == REQUEST_WAKELOCK_SETUPAPP)
                setupWakeLock();
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void askReorderTaskPermission()
    {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.REORDER_TASKS) == PackageManager.PERMISSION_GRANTED)
            Log.i(TAG, "Reorder Task Permission granted");
    }

    private void doSetupWakeLock()
    {
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

    private void setupWakeLock()
    {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }
}
