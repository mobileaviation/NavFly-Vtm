package com.mobileaviationtools.nav_fly.Classes;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.RectF;

import org.oscim.backend.CanvasAdapter;

public class BitmapHelpers {
    public static Bitmap getScaledBitmap(Bitmap bitmap)
    {
        int width = bitmap.getWidth() * java.lang.Math.round(CanvasAdapter.getScale());
        int heigth = bitmap.getHeight() * java.lang.Math.round(CanvasAdapter.getScale());

        Bitmap dstBitmap = Bitmap.createBitmap(width,
                heigth,
                Bitmap.Config.ARGB_8888);

        Canvas dstCanvas = new Canvas(dstBitmap);
        Rect scrR = new Rect(0, 0, (int)bitmap.getWidth(), (int)bitmap.getHeight());
        RectF dstR = new RectF(0f, 0f, (float)dstBitmap.getWidth(), (float)dstBitmap.getHeight());
        dstCanvas.drawBitmap(bitmap, scrR, dstR, null);

        return dstBitmap;
    }
}
