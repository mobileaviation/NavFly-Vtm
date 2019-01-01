package com.mobileaviationtools.nav_fly.Tracks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileaviationtools.airnavdata.Entities.TrackLogItem;
import com.mobileaviationtools.nav_fly.R;

import java.util.List;


public class PlaybackAdapter extends RecyclerView.Adapter<PlaybackItemViewHolder> {
    private List<TrackLogItem> items;

    public PlaybackAdapter(List<TrackLogItem> items)
    {
        this.items = items;
    }

    @NonNull
    @Override
    public PlaybackItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playback_items, viewGroup, false);
        PlaybackItemViewHolder viewHolder = new PlaybackItemViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaybackItemViewHolder playbackItemViewHolder, int i) {
        TrackLogItem item = items.get(i);
        playbackItemViewHolder.playbackItemText.setText(item.id.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
