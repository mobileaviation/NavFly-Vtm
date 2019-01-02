package com.mobileaviationtools.nav_fly.Tracks;

import android.graphics.Typeface;
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
    private Integer selectedPosition = -1;

    public PlaybackAdapter(List<TrackLogItem> items)
    {
        this.items = items;
    }

    @NonNull
    @Override
    public PlaybackItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.playback_items, viewGroup, false);
        final PlaybackItemViewHolder viewHolder = new PlaybackItemViewHolder(view);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PlaybackItemViewHolder playbackItemViewHolder, int i) {
        TrackLogItem item = items.get(i);
        playbackItemViewHolder.playbackItemSpeedText.setText(((Long) Math.round(item.ground_speed_kt)).toString() + "kt");
        playbackItemViewHolder.playbackItemAltText.setText(((Long) Math.round(item.altitude_ft)).toString()+"ft");
        playbackItemViewHolder.playbackCompassImage.setRotation(item.true_heading_deg.floatValue());

        if (i==selectedPosition)
        {
            playbackItemViewHolder.playbackItemAltText.setTypeface(playbackItemViewHolder.playbackItemAltText.getTypeface(),
                    Typeface.BOLD);
            playbackItemViewHolder.playbackItemSpeedText.setTypeface(playbackItemViewHolder.playbackItemSpeedText.getTypeface(),
                    Typeface.BOLD);
        }
        else
        {
            playbackItemViewHolder.playbackItemAltText.setTypeface(playbackItemViewHolder.playbackItemAltText.getTypeface(),
                    Typeface.NORMAL);
            playbackItemViewHolder.playbackItemSpeedText.setTypeface(playbackItemViewHolder.playbackItemSpeedText.getTypeface(),
                    Typeface.NORMAL);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
