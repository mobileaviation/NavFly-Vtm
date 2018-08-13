package com.mobileaviationtools.airnavdata.Firebase;

import android.content.Context;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConnection {
    public static DatabaseReference getAirspacesFirebaseReference(Context context)
    {
        FirebaseOptions fbOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyBII0CEWciBmPSy7xceMpt8Znc6DrAOkF4")
                .setApplicationId("1:673977020964:android:9c9afb207af7615d")
                .setDatabaseUrl("https://flightsimplannerairspaces.firebaseio.com")
                .build();
        final FirebaseApp airspacesApp = FirebaseApp.initializeApp(context, fbOptions, "airspaces");
        return FirebaseDatabase.getInstance(airspacesApp).getReference();
    }

    public static DatabaseReference getNavigationFirebaseReference(Context context, String entity)
    {
        FirebaseOptions fbOptions = new FirebaseOptions.Builder()
                .setApiKey("AIzaSyAZoA41QRDAsIV7zQ8ZqM0JLDpX8aQ88-E")
                .setApplicationId("1:702285397223:android:9c9afb207af7615d")
                .setDatabaseUrl("https://flightsimplanner-202711.firebaseio.com")
                .build();
        FirebaseApp fixesApp = FirebaseApp.initializeApp(context, fbOptions, entity);
        return FirebaseDatabase.getInstance(fixesApp).getReference();
    }
}
