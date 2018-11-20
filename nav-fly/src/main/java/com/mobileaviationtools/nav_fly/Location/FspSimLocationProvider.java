package com.mobileaviationtools.nav_fly.Location;

import android.content.Context;

import com.mobileaviationtools.nav_fly.SimConnect.FspWebApi;
import com.mobileaviationtools.nav_fly.SimConnect.Requests.ConnectRequest;
import com.mobileaviationtools.nav_fly.SimConnect.Requests.OffsetRequest;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.ConnectResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.OffsetResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.VersionResponse;
import com.mobileaviationtools.nav_fly.SimConnect.WebAPIEvents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FspSimLocationProvider {
    private String ipAddress = "192.168.210.60";
    private int port = 81;
    private String dataGroup = "attitude";
    private FspWebApi fspWebApi;


    public FspSimLocationProvider(Context context)
    {
        this.context = context;
    }

    private Context context;

    private LocationEvents locationEvents;
    public void SetLocationEvents(LocationEvents locationEvents)
    {
        this.locationEvents = locationEvents;
    }

    private void setupConnection()
    {
        fspWebApi = new FspWebApi(ipAddress, port);
        ConnectRequest connectRequest = new ConnectRequest();
        connectRequest.name = "fspConnection";
        fspWebApi.SetWebAPIEvents(new WebAPIEvents() {
            @Override
            public void OnVersionRetrieved(VersionResponse version) {

            }

            @Override
            public void OnConnected(ConnectResponse response) {

            }

            @Override
            public void OnClosed(String response) {

            }

            @Override
            public void OnRetrievedOffsets(List<OffsetResponse> response) {
                HashMap<Integer, OffsetResponse> offsetMap = new HashMap<>();
                for(OffsetResponse resp: response)
                {
                    offsetMap.put(resp.Address, resp);
                    FspLocation location = caculateLocation(offsetMap);
                    if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.simulator,
                            location, "New Sim Location", true);
                }
            }

            @Override
            public void OnFailure(String message) {
                if (locationEvents != null) locationEvents.OnLocationChanged(LocationProviderType.simulator,
                        null, message, false);
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

        return  null;
    }

    private void setSpeed(HashMap<Integer, OffsetResponse> offsetMap, FspLocation location)
    {

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
