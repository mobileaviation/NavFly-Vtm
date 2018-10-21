package com.mobileaviationtools.nav_fly.Info;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.mobileaviationtools.nav_fly.R;

public class AirportInfoLayout extends LinearLayout {
    public AirportInfoLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater.from(getContext()).inflate(
                R.layout.airport_info, this);
    }
}
