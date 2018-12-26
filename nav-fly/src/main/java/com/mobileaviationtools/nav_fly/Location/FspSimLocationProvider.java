package com.mobileaviationtools.nav_fly.Location;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Settings.Services.LocationProviderService;
import com.mobileaviationtools.nav_fly.SimConnect.FspWebApi;
import com.mobileaviationtools.nav_fly.SimConnect.Requests.ConnectRequest;
import com.mobileaviationtools.nav_fly.SimConnect.Requests.OffsetRequest;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.ConnectResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.OffsetResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.VersionResponse;
import com.mobileaviationtools.nav_fly.SimConnect.WebAPIEvents;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class FspSimLocationProvider implements IFspLocationProvider{
    private String ipAddress = "192.168.2.11";
    private int port = 81;
    private String dataGroup = "attitude";
    private FspWebApi fspWebApi;
    private String TAG = "FspSimLocationProvider";
    private Boolean connected;

    private Timer offsetsTimer;

    public FspSimLocationProvider(GlobalVars vars)
    {
        this.vars = vars;
        this.connected = false;
        LocationProviderService service = new LocationProviderService(vars);
        String[] network = service.getNetwerkInfo();
        ipAddress = network[0];
        port = Integer.parseInt(network[1]);
    }

    private GlobalVars vars;

    private LocationEvents locationEvents;

    public boolean setup()
    {
        setupConnection();
        return true;
    }

    public boolean start(LocationEvents locationEvents)
    {
        this.locationEvents = locationEvents;
        ConnectRequest req = new ConnectRequest();
        req.name = "fspConnection";
        fspWebApi.doConnectCall(req);
        return true;
    }

    public boolean stop()
    {
        connected = false;
        if (offsetsTimer != null) offsetsTimer.cancel();
        ConnectRequest req = new ConnectRequest();
        req.name = "fspConnection";
        fspWebApi.doDisconnectCall(req);
        return true;
    }

    private void setupTimer()
    {
        if (offsetsTimer != null)
            offsetsTimer.cancel();
        offsetsTimer = new Timer();
        offsetsTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                fspWebApi.doProcessOffsetsCall(dataGroup);
            }
        }, 500, 500);
    }

    private void setupConnection()
    {
        fspWebApi = new FspWebApi(ipAddress, port);
        ConnectRequest connectRequest = new ConnectRequest();
        connectRequest.name = "fspConnection";
        fspWebApi.SetWebAPIEvents(new WebAPIEvents() {
            @Override
            public void OnVersionRetrieved(VersionResponse version) {
                if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.simulator,
                        null, "Version : " + version.toString(), true);
            }

            @Override
            public void OnConnected(ConnectResponse response) {
                if (response.Message.contains("Error"))
                {
                    if (locationEvents != null)
                        locationEvents.OnLocationChanged(LocationProviderType.simulator,
                                null, "Error Connecting: " + response.Message, false);
                }
                else {
                    if (locationEvents != null)
                        locationEvents.OnLocationChanged(LocationProviderType.simulator,
                                null, "Connected : " + response.Message, true);
                    // add offsets
                    setOffsets();
                    // start timer
                    setupTimer();
                    connected = true;
                }
            }

            @Override
            public void OnClosed(String response) {
                if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.simulator,
                        null, "Closed : " + response, false);
            }

            @Override
            public void OnRetrievedOffsets(List<OffsetResponse> response) {
                HashMap<Integer, OffsetResponse> offsetMap = new HashMap<>();
                for(OffsetResponse resp: response)
                {
                    offsetMap.put(resp.Address, resp);
                }
                FspLocation location = caculateLocation(offsetMap);
                if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.simulator,
                        location, "Offsets : New Sim Location", connected);
            }

            @Override
            public void OnFailure(String message) {
                Log.e(TAG, "Connection Failure: " + message + " IP:"+ ipAddress + " Port:" + port);
                if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.simulator,
                        null, "Error : " + message, false);
            }
        });
    }

    private void setOffsets()
    {
        ArrayList<OffsetRequest> offsets = new ArrayList<>();
        // Add offset for pitch
        offsets.add(OffsetRequest.getInstance(0x0578, dataGroup, FSUIPCDataType.Int32));
        // Add offset for bank
        offsets.add(OffsetRequest.getInstance(0x057C, dataGroup, FSUIPCDataType.Int32));
        // Add offset for Speed
        offsets.add(OffsetRequest.getInstance(0x02BC, dataGroup, FSUIPCDataType.Int32));
        // Add offset for Latitude
        offsets.add(OffsetRequest.getInstance(0x0560, dataGroup, FSUIPCDataType.Int64));
        // Add offset for Longitude
        offsets.add(OffsetRequest.getInstance(0x0568, dataGroup, FSUIPCDataType.Int64));
        // Add offset for Aircrafttype
        offsets.add(OffsetRequest.getInstance(0x3D00, dataGroup, FSUIPCDataType.String));
        // Add offset for HeadingTrue
        offsets.add(OffsetRequest.getInstance(0x0580, dataGroup, FSUIPCDataType.Int32));
        // Add offgset for Magnetic variation
        offsets.add(OffsetRequest.getInstance(0x02A0, dataGroup, FSUIPCDataType.Int32));
        // Add offsets for "Wiskey compass" heading
        offsets.add(OffsetRequest.getInstance(0x02CC, dataGroup, FSUIPCDataType.Double));
        // Add offsets for Altitude in meters
        offsets.add(OffsetRequest.getInstance(0x0574, dataGroup, FSUIPCDataType.Int32));
        // Add offset for vertical speed
        offsets.add(OffsetRequest.getInstance(0x0842, dataGroup, FSUIPCDataType.Int16));
        // Turn coordinator Ball position
        offsets.add(OffsetRequest.getInstance(0x036E, dataGroup, FSUIPCDataType.Byte));
        // Turn coordinator plane position
        offsets.add(OffsetRequest.getInstance(0x037C, dataGroup, FSUIPCDataType.Int16));

        fspWebApi.doAddOffsetsCall(offsets);
    }

    private FspLocation caculateLocation(HashMap<Integer, OffsetResponse> offsetMap)
    {
        String PROVIDER = "simulator";
        FspLocation location = new FspLocation(PROVIDER);

        setSpeed(offsetMap, location);
        setLocation(offsetMap, location);
        setHeading(offsetMap, location);
        setAltitude(offsetMap, location);

        location.setTime((new Date()).getTime());

        return location;
    }

    private void setSpeed(HashMap<Integer, OffsetResponse> offsetMap, FspLocation location)
    {
        try
        {
            OffsetResponse o = offsetMap.get(0x02BC); //connection.ReadOffset(0x02BC);
            if (o != null)
            {
                // calculate knots
                double as = Double.parseDouble(o.Value) / 128d;
                // calculate meters/s
                as = as * 0.51444444444d;
                location.setSpeed((float)as);
            }
        }
        catch (Exception ee)
        {
            Log.e(TAG, "SetSpeed exception: " + ee.getMessage());
        }
    }

    private void setLocation(HashMap<Integer, OffsetResponse> offsetMap, FspLocation location)
    {
        try
        {
            OffsetResponse olat = offsetMap.get(0x0560); //  o = connection.ReadOffset(0x0560);
            double lat = 0, lon = 0;
            if (olat != null)
            {
                lat = Double.parseDouble(olat.Value);
                lat = lat * (90d/(10001750d*65536d*65536d));
            }
            OffsetResponse olon = offsetMap.get(0x0568); //  o = connection.ReadOffset(0x0568);
            if (olon != null)
            {
                lon = Double.parseDouble(olon.Value);
                lon = lon * (360d / (65536d*65536d*65536d*65536d));
            }
            location.setLatitude(lat);
            location.setLongitude(lon);
        }
        catch (Exception ee)
        {
            Log.e(TAG, "setLocation exception: " + ee.getMessage());
        }
    }

    private void setHeading(HashMap<Integer, OffsetResponse> offsetMap, FspLocation location)
    {
        try
        {
            OffsetResponse o = offsetMap.get(0x02CC);  //o = connection.ReadOffset(0x02CC);
            o.Value = o.Value.replace(",", ".");
            double h = (Double.parseDouble(o.Value));
            location.setBearing((float)h);
        }
        catch (Exception ee)
        {
            Log.e(TAG, "setHeading exception: " + ee.getMessage());
        }
    }

    private void setAltitude(HashMap<Integer, OffsetResponse> offsetMap, FspLocation location)
    {
        try {
            OffsetResponse o = offsetMap.get(0x0574);   //        o = connection.ReadOffset(0x0574);
            double he = 0d;
            if (o != null) {
                he = Double.parseDouble(o.Value);
                he = he * 3.2808399d; // meters to feet
                location.setAltitude(he);
            }
        }
        catch (Exception ee)
        {
            Log.e(TAG, "setAltitude exception: " + ee.getMessage());
        }
    }

//    try {
//        String PROVIDER = "simulator";
//        Location loc = new Location(PROVIDER);
//
//        Object o = connection.ReadOffset(0x02BC);
//        // Airspeed
//        if (o != null)
//        {
//            // calculate knots
//            double as = Double.parseDouble(o.toString()) / 128d;
//            // calculate meters/s
//            as = as * 0.51444444444d;
//            loc.setSpeed((float)as);
//            setAirspeed(as);
//
//        }
//
//        // Heading
//        o = connection.ReadOffset(0x02CC);
//        double h = (Double.parseDouble(o.toString()));
//        loc.setBearing((float)h);
//        setCompass(h);
//
//
//        // Location
//        o = connection.ReadOffset(0x0560);
//        double lat = 0, lon = 0;
//        if (o != null)
//        {
//            lat = Double.parseDouble(o.toString());
//            lat = lat * (90d/(10001750d*65536d*65536d));
//        }
//        o = connection.ReadOffset(0x0568);
//        if (o != null)
//        {
//            lon = Double.parseDouble(o.toString());
//            lon = lon * (360d / (65536d*65536d*65536d*65536d));
//        }
//
//        //LatLng curpos = new LatLng(lat, lon);
//
//
//        // Height in meters
//        o = connection.ReadOffset(0x0574);
//        double he = 0d;
//        if (o != null) {
//            he = Double.parseDouble(o.toString());
//            //he = he * 3.2808399d; // meters to feet
//            setAltimeter(he * 3.2808399d);
//        }
//
//        loc.setLatitude(lat);
//        loc.setLongitude(lon);
//        loc.setAltitude(he);
//        loc.setTime((new Date()).getTime());
//
//        onLocationChanged(loc);
//
//        o = connection.ReadOffset(0x0842);
//        if (o != null)
//        {
//            double vs = Double.parseDouble(o.toString());
//            vs = (vs * 3.2808399d)/100; // meters to feet
//            setVsi(vs);
//        }
//
//        o = connection.ReadOffset(0x037C);
//        if (o!=null)
//        {
//            double tcp = Double.parseDouble(o.toString());
//            o = connection.ReadOffset(0x036E);
//            if (o != null)
//            {
//                double tcb = Double.parseDouble(o.toString());
//                setTurnCoordinator(tcp, tcb);
//
//            }
//        }
//
//        o = connection.ReadOffset(0x057C);
//        if (o != null){
//            double bb = (Double.parseDouble(o.toString()) * 360d) / (65536d*65536d);
//            o = connection.ReadOffset(0x0578);
//            if (o != null){
//                double pi = (Double.parseDouble(o.toString()) * 360d) / (65536d*65536d);
//                setHorizon(bb, pi);
//            }
//        }
//
//    }
//                        catch (Exception e) {
//        Log.d(TAG, "Timer Execute exception: " + e.getMessage());
//    }
}
