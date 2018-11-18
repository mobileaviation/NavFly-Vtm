package com.mobileaviationtools.nav_fly.Network;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

public class NetworkDiscovery {
    private final NsdManager mNsdManager;
    //To find all the available networks
    //public static final String SERVICE_TYPE = "_services._dns-sd._udp";
    public static final String SERVICE_TYPE = "_xservice._tcp.";

    public NetworkDiscovery(Context context)
    {
        mNsdManager = (NsdManager)context.getSystemService(Context.NSD_SERVICE);
        initializeDiscoveryListener();
        initializeResolveListener();

        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener);
    }


    NsdManager.DiscoveryListener discoveryListener;
    String TAG = "NetworkDiscovery";

    public void initializeDiscoveryListener()
    {
        discoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String s, int i) {
                Log.d(TAG, "Service Discovery Start Failed " + s);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String s, int i) {
                Log.d(TAG, "Service Discovery Stop failed " + s);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onDiscoveryStarted(String s) {
                Log.d(TAG, "Service Discovery started: " + s);
            }

            @Override
            public void onDiscoveryStopped(String s) {
                Log.d(TAG, "Service Discovery stopped: " + s);
            }

            @Override
            public void onServiceFound(NsdServiceInfo nsdServiceInfo) {
                Log.d(TAG, "Service discovery success " + nsdServiceInfo);
                if (nsdServiceInfo.getServiceName().contains("fsp_server"))
                {
                    mNsdManager.resolveService(nsdServiceInfo, resolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo nsdServiceInfo) {
                Log.d(TAG, "Service discovery Service Lost " + nsdServiceInfo);
            }
        };
    }

    NsdManager.ResolveListener resolveListener;
    public void initializeResolveListener()
    {
        resolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo nsdServiceInfo, int i) {
                Log.e(TAG, "Resolve error: " + i);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo nsdServiceInfo) {
                Log.d(TAG, "Resolved Succeeded. " + nsdServiceInfo);
            }
        };
    }
}
