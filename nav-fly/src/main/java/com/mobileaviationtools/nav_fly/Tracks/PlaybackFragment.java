package com.mobileaviationtools.nav_fly.Tracks;


import android.os.Bundle;

import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavTracklogDatabase;
import com.mobileaviationtools.airnavdata.Entities.TrackLog;
import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaybackFragment extends Fragment {

    public interface PlaybackFragmentEvents
    {
        public void OnCloseBtnClicked();
    }

    public PlaybackFragment() {
        // Required empty public constructor
    }

    private PlaybackAdapter playbackAdapter;
    private RecyclerView playbackRecyclerView;
    private Long trackLogId;
    private AirnavTracklogDatabase airnavTracklogDatabase;
    private GlobalVars vars;
    private ImageButton closeBtn;
    private TextView tracklogNameText;
    private ConstraintLayout playbackBaseLayout;
    private PlaybackFragmentEvents playbackFragmentEvents;

    public void setTrackLog(Long trackLogId, GlobalVars vars, PlaybackFragmentEvents playbackFragmentEvents)
    {
        this.vars = vars;
        this.playbackFragmentEvents = playbackFragmentEvents;
        airnavTracklogDatabase = AirnavTracklogDatabase.getInstance(vars.context);
        this.trackLogId = trackLogId;

        Long maxAltitudeFt = airnavTracklogDatabase.getTracklogItems().getMaxAltitude(this.trackLogId);
        TrackLog trackLog = airnavTracklogDatabase.getTrackLog().getTracklogByID(this.trackLogId);
        tracklogNameText.setText(trackLog.name);

        List<TrackLogItem> items = airnavTracklogDatabase.getTracklogItems().getTracklogItemByLogId(this.trackLogId);
        playbackAdapter = new PlaybackAdapter(items);
        playbackRecyclerView.setAdapter(playbackAdapter);
        playbackBaseLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_playback, container, false);
        playbackBaseLayout = (ConstraintLayout) view.findViewById(R.id.playbackBaselayout);
        playbackBaseLayout.setVisibility(View.GONE);

        playbackRecyclerView = (RecyclerView) view.findViewById(R.id.playbackListView);
        playbackRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        // get items
        playbackRecyclerView.setLayoutManager(linearLayoutManager);

        tracklogNameText = (TextView) view.findViewById(R.id.tracklogNameText);
        closeBtn = (ImageButton) view.findViewById(R.id.closeTracklogDialogBtn);
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playbackBaseLayout.setVisibility(View.GONE);
                if (playbackFragmentEvents != null) playbackFragmentEvents.OnCloseBtnClicked();
            }
        });

        return view;
    }

}
