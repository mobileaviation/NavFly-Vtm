package com.mobileaviationtools.nav_fly.Tracks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;

public class PlaybackItemViewHolder extends RecyclerView.ViewHolder {
    public TextView playbackItemText;

    public PlaybackItemViewHolder(@NonNull View itemView) {
        super(itemView);
        playbackItemText = itemView.findViewById(R.id.playbackItemText);
    }
}
