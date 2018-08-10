package com.mobileaviationtools.airnavdata.Firebase;

import android.content.Context;

//import com.google.firebase.database.DataSnapshot;
//import com.google.firebase.database.DatabaseError;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.Query;
//import com.google.firebase.database.ValueEventListener;
import com.mobileaviationtools.airnavdata.Models.Statistics;

public class FBStatistics {
    //private DatabaseReference mDatabase;

    public FBStatistics(Context context)
    {
        //mDatabase = FirebaseConnection.getNavigationFirebaseReference(context, "statistics");
    }

    public interface StatisticsEventListerner
    {
        public void OnStatistics(Statistics statistics);
    }

    public StatisticsEventListerner OnStatisticsEvent;

//    public void FillStatistics()
//    {
//        Query statisticsQuery = mDatabase.child("statistics");
//
//        statisticsQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                Statistics stats = dataSnapshot.getValue(Statistics.class);
//                if (OnStatisticsEvent != null) OnStatisticsEvent.OnStatistics(stats);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//    }

}
