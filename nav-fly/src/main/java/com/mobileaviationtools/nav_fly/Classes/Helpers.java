package com.mobileaviationtools.nav_fly.Classes;

import android.util.Log;

import java.net.HttpURLConnection;
import java.net.URL;

public class Helpers {
    public static boolean checkInternetAvailabilityAdress(String address) {
        try {
            HttpURLConnection urlConnection = (HttpURLConnection)
                    (new URL(address)
                            .openConnection());
            urlConnection.setRequestProperty("User-Agent", "Android");
            urlConnection.setRequestProperty("Connection", "close");
            urlConnection.setConnectTimeout(1500);
            urlConnection.connect();
            Integer respCode = urlConnection.getResponseCode();
            Integer contentLength = urlConnection.getContentLength();
            if ((respCode == 204 && contentLength == 0) || (respCode == 200 && contentLength > 0)) {
                Log.d("Network Checker", "Successfully connected to internet");
                return true;
            }
            else
            {
                Log.d("Network Checker", "clients3.google.com not accessible");
                return false;
            }
        } catch (Exception e) {
            Log.e("Network Checker", "Error checking internet connection", e);
            return false;
        }
    }
}
