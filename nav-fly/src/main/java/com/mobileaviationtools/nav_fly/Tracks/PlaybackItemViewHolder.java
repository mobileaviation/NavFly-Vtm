package com.mobileaviationtools.nav_fly.Tracks;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;

public class PlaybackItemViewHolder extends RecyclerView.ViewHolder {
    public TextView playbackItemAltText;
    public TextView playbackItemSpeedText;
    public ImageView playbackCompassImage;

    public PlaybackItemViewHolder(@NonNull View itemView) {
        super(itemView);
        playbackItemAltText = itemView.findViewById(R.id.playbackItemAltText);
        playbackCompassImage = itemView.findViewById(R.id.trackLogItemCompassImage);
        playbackItemSpeedText = itemView.findViewById(R.id.playbackItemSpeedText);
    }
}
