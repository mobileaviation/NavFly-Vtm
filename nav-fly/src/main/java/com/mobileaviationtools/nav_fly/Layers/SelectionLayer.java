package com.mobileaviationtools.nav_fly.Layers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerLayer;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.map.Map;

public class SelectionLayer extends ItemizedLayer {
    private String TAG = "SelectionLayer";

    public SelectionLayer(Map map, MarkerSymbol defaultMarker, Context context) {
        this(map, defaultMarker);
        this.context = context;
        this.mMap = map;
        mMap.layers().add(this);
    }

    private Context context;
    private Map mMap;
    private MarkerItem item;
    private MarkerSymbol symbol;

    private SelectionLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public void setAirportSelected(Airport airport)
    {
        //symbol = new MarkerSymbol(new AndroidBitmap(GetMarkerBitmap(airport)), MarkerSymbol.HotspotPlace.CENTER, false);
        //item.setMarker(symbol);
        //item = new MarkerItem()
        addItem(item);
        this.update();

        final ValueAnimator selectionAnimator = ValueAnimator.ofFloat(42f, 50f);
        selectionAnimator.setDuration(500);
        selectionAnimator.setRepeatMode(ValueAnimator.REVERSE);
        selectionAnimator.setRepeatCount(ValueAnimator.INFINITE);
        selectionAnimator.setInterpolator(new LinearInterpolator());

        selectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float animProgress = (Float) valueAnimator.getAnimatedValue();
                Log.i(TAG, "Selection Animator Test: " + animProgress.toString());
            }
        });
//        selectionAnimator.addListener(new AnimatorListenerAdapter() {
//            @Override
//            public void onAnimationEnd(Animator animation, boolean isReverse) {
//                if (!isReverse) selectionAnimator.start();
//                else selectionAnimator.reverse();
//            }
//        });

        selectionAnimator.start();
    }
}
