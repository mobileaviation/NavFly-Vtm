package com.mobileaviationtools.nav_fly.Route.Info;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.oscim.map.Map;

public class InfoLayout extends LinearLayout {
    public InfoLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    private Map map;
    private Context context;
    private Activity activity;

    public void setMap(Map map) {
        this.map = map;
    }

    public void init(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }
}
