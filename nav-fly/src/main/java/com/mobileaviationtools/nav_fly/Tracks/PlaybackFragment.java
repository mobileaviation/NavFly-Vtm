package com.mobileaviationtools.nav_fly.Tracks;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileaviationtools.airnavdata.AirnavTracklogDatabase;
import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaybackFragment extends Fragment {


    public PlaybackFragment() {
        // Required empty public constructor
    }

    private PlaybackAdapter playbackAdapter;
    private RecyclerView playbackRecyclerView;
    private Long trackLogId;
    private AirnavTracklogDatabase airnavTracklogDatabase;
    private GlobalVars vars;

    public void setTrackLog(Long trackLogId, GlobalVars vars)
    {
        this.vars = vars;
        airnavTracklogDatabase = AirnavTracklogDatabase.getInstance(vars.context);
        this.trackLogId = trackLogId;
        List<TrackLogItem> items = airnavTracklogDatabase.getTracklogItems().getTracklogItemByLogId(this.trackLogId);
        playbackAdapter = new PlaybackAdapter(items);
        playbackRecyclerView.setAdapter(playbackAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_playback, container, false);

        playbackRecyclerView = (RecyclerView) view.findViewById(R.id.playbackListView);
        playbackRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // get items
        playbackRecyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

}
