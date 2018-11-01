package com.mobileaviationtools.nav_fly.Layers;

import android.animation.Animator;
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
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.ItemizedLayer;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;
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
        setAnimationListeners();
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
        selectionAnimator.setRepeatCount(10);
        selectionAnimator.setInterpolator(new LinearInterpolator());
    }

    private void setAnimationListeners()
    {
        selectionAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                if (symbol != null) {
                    Float animProgress = (Float) valueAnimator.getAnimatedValue();
                    symbol = new MarkerSymbol(new AndroidBitmap(drawSelectionCircle(animProgress)), MarkerSymbol.HotspotPlace.CENTER, false);
                    item.setMarker(symbol);
                    SelectionLayer.this.update();
                    mMap.render();
                }
            }

        });
        selectionAnimator.addListener(new Animator.AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

                Log.i(TAG, "Animator End: ");
                unSelectItem();
                SelectionLayer.this.update();
                mMap.render();
            }

            @Override
            public void onAnimationCancel(Animator animator) {
                Log.i(TAG, "Animator Cancel");
            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    public void unSelectItem()
    {
        if (symbol != null) symbol = null;
        if (item != null){
            this.removeItem(item);
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
        this.addItem(item);
        this.update();

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
