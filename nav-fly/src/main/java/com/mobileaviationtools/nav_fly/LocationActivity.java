package com.mobileaviationtools.nav_fly;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.mobileaviationtools.nav_fly.Classes.ConnectStage;
import com.mobileaviationtools.nav_fly.Instruments.InstrumentsFragment;
import com.mobileaviationtools.nav_fly.Location.FspLocation;
import com.mobileaviationtools.nav_fly.Location.FspLocationProvider;
import com.mobileaviationtools.nav_fly.Location.LocationEvents;
import com.mobileaviationtools.nav_fly.Location.LocationProviderType;
import com.mobileaviationtools.nav_fly.Location.Tracking;
import com.mobileaviationtools.nav_fly.Menus.MapDirectionType;
import com.mobileaviationtools.nav_fly.Menus.NavigationButtonFragment;

import org.oscim.core.MapPosition;

public class LocationActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        instrumentsFragment = (InstrumentsFragment) getSupportFragmentManager().findFragmentById(R.id.InstrumentsFragment);

        SharedPreferences databasePrefs = this.getSharedPreferences("Database", MODE_PRIVATE);
        Integer instrumentsVisibility = databasePrefs.getInt("instrumentsVisibility", View.VISIBLE);
        instrumentsFragment.SetVisibility(instrumentsVisibility);
    }

    protected FspLocationProvider locationProvider;
    protected boolean mapPosLockedToAirplanePos = true;
    protected ConnectStage connectStage;
    protected Tracking trackingLayer;
    protected InstrumentsFragment instrumentsFragment;

    public void addTrackingLayer()
    {
        trackingLayer = new Tracking(vars);
    }

    protected void setInstrumentsVisibility()
    {
        Integer visibility = instrumentsFragment.ToggleVisibility();
        SharedPreferences databasePrefs = this.getSharedPreferences("Database", MODE_PRIVATE);
        databasePrefs.edit().putInt("instrumentsVisibility", visibility).apply();
    }

    protected void ConnectionProcess(final NavigationButtonFragment menu)
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
                            Toast error = Toast.makeText(LocationActivity.this, message, Toast.LENGTH_LONG);
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

    protected void setLocation(FspLocation location)
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

        if (vars.mapDirectionType== MapDirectionType.flight)
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

        instrumentsFragment.setLocation(location);
        vars.map.setMapPosition(pos);
    }
}
