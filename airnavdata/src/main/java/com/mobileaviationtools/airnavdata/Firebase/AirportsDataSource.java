package com.mobileaviationtools.airnavdata.Firebase;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.ArrayList;

public class AirportsDataSource {
    public AirportsDataSource(Context context)
    {
        this.context = context;
    }

    private Context context;
    private static final String TAG = "AirportsDataSource";
    private DatabaseReference mDatabase;

    Integer start;
    Integer count;
    Query query;
    ValueEventListener dataListener;

    AirnavDatabase db;;

    public void ReadAirportData(final Integer airportCount)
    {
        mDatabase = FirebaseConnection.getNavFlyFirebaseReference(context, "airports");
        db = AirnavDatabase.getInstance(context);

        start = 0;
        count = 1000;

        query = mDatabase.child("airports").orderByChild("index").startAt(start).endAt(start + (count-1));


        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    ArrayList<Airport> airports = new ArrayList<>();
                    ArrayList<Runway> runways = new ArrayList<>();
                    ArrayList<Frequency> frequencies = new ArrayList<>();
                    for (DataSnapshot airportSnapshow : dataSnapshot.getChildren()) {

                        Airport airport = airportSnapshow.getValue(Airport.class);
                        airports.add(airport);

                        //db.getAirport().insertAirport(airport);

                        if (airport.runways != null) {
                            for (Runway runway : airport.runways) {
                                //db.getRunways().insertRunway(runway);
                                runways.add(runway);
                            }
                        }
                        if (airport.frequencies != null) {
                            for (Frequency frequency : airport.frequencies) {
                                //db.getFrequency().insertFrequency(frequency);
                                frequencies.add(frequency);
                            }
                        }
                    }

                    //db.beginTransaction();
                    db.getAirport().insertAirportTransaction(airports);
                    db.getFrequency().insertFrequenciesTransaction(frequencies);
                    db.getRunways().insertRunwaysTransaction(runways);
                    //db.setTransactionSuccessful();
                    //db.endTransaction();
                }
                catch (Exception ee)
                {
                    Log.e(TAG, "Insert airports Problem: " + ee.getMessage());
                }
                Log.i(TAG, "Read 1000 airports, get the next from: " + start.toString());
                Log.i(TAG, "Inserted 1000 airports into the database.");

                start = start + count;

                //if (progress != null) progress.onProgress(airportCount, start, FBTableType.airports);

                query = mDatabase.child("airports").orderByChild("index").startAt(start).endAt(start + (count-1));

                if (start<airportCount)
                    query.addListenerForSingleValueEvent(dataListener);
                else {
                    //if (progress != null) progress.OnFinished(FBTableType.airports);
                    Log.i(TAG, "Finished reading airports");

                    return;
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        query.addListenerForSingleValueEvent(dataListener);
    }

}
