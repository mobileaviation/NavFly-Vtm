package com.mobileaviationtools.airnavdata.Firebase;

import android.content.Context;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.airnavdata.Entities.Runway;

public class NavaidDataSource {
    public NavaidDataSource(Context context)
    {
        this.context = context;
    }

    private Context context;
    private static final String TAG = "NavaidDataSource";
    private DatabaseReference mDatabase;

    Integer start;
    Integer count;
    Query query;
    ValueEventListener dataListener;

    AirnavDatabase db;

    public void ReadNavaidData(final Integer navaidsCount) {
        mDatabase = FirebaseConnection.getNavFlyFirebaseReference(context, "navaids");
        db = AirnavDatabase.getInstance(context);

        start = 0;
        count = 1000;

        query = mDatabase.child("navaids").orderByChild("index").startAt(start).endAt(start + (count-1));

        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                db.beginTransaction();
                for (DataSnapshot navaidSnapshow: dataSnapshot.getChildren()){

                    Navaid navaid = navaidSnapshow.getValue(Navaid.class);
                    try {
                        db.getNavaids().insertNavaid(navaid);
                    }
                    catch (Exception ee)
                    {
                        Log.e(TAG, "Insert navaid: " + navaid.ident + " Problem: " + ee.getMessage());
                    }
                }
                db.setTransactionSuccessful();
                db.endTransaction();
                Log.i(TAG, "Read 1000 navaids, get the next from: " + start.toString());
                Log.i(TAG, "Inserted 1000 navaids into the database.");

                start = start + count;

                //if (progress != null) progress.onProgress(airportCount, start, FBTableType.airports);

                query = mDatabase.child("navaids").orderByChild("index").startAt(start).endAt(start + (count-1));

                if (start<navaidsCount)
                    query.addListenerForSingleValueEvent(dataListener);
                else {
                    //if (progress != null) progress.OnFinished(FBTableType.airports);
                    Log.i(TAG, "Finished reading navaids");
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
