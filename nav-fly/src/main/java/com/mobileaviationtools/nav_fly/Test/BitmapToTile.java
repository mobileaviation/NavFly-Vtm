package com.mobileaviationtools.nav_fly.Test;

import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.oscim.android.MapView;
import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.map.Map;
import org.oscim.map.ViewController;
import org.oscim.map.Viewport;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BitmapToTile {
    public BitmapToTile()
    {
        viewController = new ViewController();
    }

    private ViewController viewController;

    public void Test(MapView view, Map map, String filename)
    {
        MapPosition pos = map.getMapPosition();
        ViewController v = new ViewController(); //map.viewport();
        v.setViewSize(2048, 1536);
        //v.setViewSize(1075, 1435);
        //v.setMapLimit(0, 0, 1000,1000);


        MapPosition newPos = new MapPosition();
        BoundingBox bb = new BoundingBox(52.31267664,5.38673401, 52.58511188, 5.71289063);
        newPos.setByBoundingBox(bb, 1075, 1435);
        newPos.setPosition(new GeoPoint(52.58511188, 5.38673401));

        Tile t = getTile(52.58511188, 5.38673401, (byte)14);
        BoundingBox tbb = t.getBoundingBox();

        v.setMapPosition(newPos);
        Point p1  = new Point();
        //-11.023995399475098 -24.24791717529297
        v.toScreenPoint(new GeoPoint(tbb.getMaxLatitude(), tbb.getMinLongitude()), p1);

        Point p2  = new Point();
        //59.514808654785156 46.290672302246094
        v.toScreenPoint(new GeoPoint(tbb.getMinLatitude(), tbb.getMaxLongitude()), p2);

        File file = new File(filename);
        Bitmap bitTest = null;
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            bitTest = AndroidGraphics.decodeBitmap(fileInputStream);

            Bitmap m = CanvasAdapter.newBitmap(256, 256, 0);
            Canvas c = CanvasAdapter.newCanvas();
            c.setBitmap(m);
            c.drawBitmap(bitTest, (float)p2.x, (float)p2.y);
            int i=0;

        } catch (Exception e) {
            e.printStackTrace();
        }


//        chart
//        noord west
//        52.58511188
//        5.38673401
//
//        zuid oost
//        52.31267664
//        5.71289063

        int i = 1;
        //[X:0.5153533333333333, Y:0.3282275108868431, Z:14] lat:52.46030000000001, lon:5.527199999999999
        //Point(2048, 1536)


    }

    public Tile getTile(final double lat, final double lon, final byte zoom)
    {
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) );
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI)  / 2 * (1<<zoom));

        if (xtile < 0) xtile = 0;
        if (xtile >= (1<<zoom)) xtile = ((1<<zoom) -1 );
        if (ytile < 0) ytile = 0;
        if (ytile >= (1<<zoom)) ytile = ((1<<zoom) - 1 );

        Tile tile = new Tile(xtile, ytile, zoom);

        return tile;
    }
}
