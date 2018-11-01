package com.mobileaviationtools.nav_fly.Layers;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.animation.LinearInterpolator;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.Classes.BitmapHelpers;

import org.locationtech.jts.geom.impl.PackedCoordinateSequence;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerLayer;
import org.oscim.layers.marker.MarkerSymbol;
import org.oscim.layers.vector.VectorLayer;
import org.oscim.map.Map;

public class SelectionLayer extends ItemizedLayer {
    private String TAG = "SelectionLayer";

    private SelectionLayer(Map map, MarkerSymbol defaultMarker) {
        super(map, defaultMarker);
    }

    public SelectionLayer(Map map, MarkerSymbol defaultMarker, Context context) {
        this(map, defaultMarker);
        this.context = context;
        this.mMap = map;
        selected = false;
        createAnimator();
    }

    private Context context;
    private Map mMap;
    private MarkerItem item;
    private MarkerSymbol symbol;
    private ValueAnimator selectionAnimator;

    private boolean selected;
    public boolean getSelected()
    { return  selected; }

    private void createAnimator()
    {
        selectionAnimator = ValueAnimator.ofFloat(42f, 50f);
        selectionAnimator.setDuration(500);
        selectionAnimator.setRepeatMode(ValueAnimator.REVERSE);
        selectionAnimator.setRepeatCount(ValueAnimator.INFINITE);
        selectionAnimator.setInterpolator(new LinearInterpolator());
    }

    public void unSelectItem()
    {
        selectionAnimator.end();
        if (symbol != null) symbol = null;
        if (item != null){
            this.mItemList.remove(item);
            item = null;
        }
        selected = false;
    }

    public void setAirportSelected(Airport airport)
    {
        unSelectItem();

        symbol = new MarkerSymbol(new AndroidBitmap(drawSelectionCircle(50f)), MarkerSymbol.HotspotPlace.CENTER, false);
        item = new MarkerItem("Selection", "Selection", new GeoPoint(airport.latitude_deg, airport.longitude_deg));
        item.setMarker(symbol);
        //item = new MarkerItem()
        this.addItem(item);
        this.update();

        selectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                Float animProgress = (Float) valueAnimator.getAnimatedValue();
                symbol = new MarkerSymbol(new AndroidBitmap(drawSelectionCircle(animProgress)), MarkerSymbol.HotspotPlace.CENTER, false);
                item.setMarker(symbol);
                SelectionLayer.this.update();
                mMap.render();

                Log.i(TAG, "Selection Animator Test: " + animProgress.toString());
            }
        });

        if (!selectionAnimator.isStarted())selectionAnimator.start();
        selected = true;
    }

    private Bitmap drawSelectionCircle(Float radius)
    {
        int size = 60;
        Bitmap baseBitmap = android.graphics.Bitmap.createBitmap(size, size,
                android.graphics.Bitmap.Config.ARGB_8888);
        Canvas baseCanvas = new Canvas(baseBitmap);

        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setStrokeWidth(4);
        p.setStyle(Paint.Style.STROKE);
        p.setColor(Color.argb(255,0,136,186));

        baseCanvas.drawCircle(size/2,size/2, radius/2, p);

        return BitmapHelpers.getScaledBitmap(baseBitmap);
    }
}
