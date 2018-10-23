package com.mobileaviationtools.nav_fly.Test;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.locationtech.jts.shape.GeometricShapeBuilder;
import org.locationtech.jts.util.GeometricShapeFactory;
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
import java.nio.ByteBuffer;

public class BitmapToTile {
    public BitmapToTile()
    {
        viewController = new ViewController();
    }

    private ViewController viewController;

    public void TransformTest()
    {
        GeometryFactory f = new GeometryFactory();
        Coordinate[] cc = new Coordinate[2];
        cc[0] = new Coordinate(5.38673401, 52.31267664);
        cc[1] = new Coordinate(5.71289063, 52.58511188);
        Geometry g = f.createLineString(cc);
        Envelope wBox = new Envelope(cc[0], cc[1]);
        Envelope pBox = new Envelope(0,1075, 0, 1435);
        AffineTransformation wp = getWorldToRectangle(wBox, pBox);

        Tile t = getTile(52.4, 5.4, (byte)11);
        BoundingBox tbb = t.getBoundingBox();

        Coordinate testC1 = new Coordinate(tbb.getMinLongitude(), tbb.getMaxLatitude());
        Coordinate newC1 = wp.transform(testC1, new Coordinate());

        Coordinate testC2 = new Coordinate(tbb.getMaxLongitude(), tbb.getMinLatitude());
        Coordinate newC2 = wp.transform(testC2, new Coordinate());



        int I=0;
    }

    private AffineTransformation getWorldToRectangle(Envelope worldEnvelop, Envelope pixelsEnvelop)
    {
        int cols = (int) pixelsEnvelop.getWidth();
        int rows = (int) pixelsEnvelop.getHeight();

        double worldWidth = worldEnvelop.getWidth();
        double worldHeight = worldEnvelop.getHeight();

        double x = -worldEnvelop.getMinX();
        double y = -worldEnvelop.getMinY();
        AffineTransformation translate = AffineTransformation.translationInstance(x,y);

        double xScale = cols / worldWidth;
        double yScale = rows / worldHeight;
        AffineTransformation scale = AffineTransformation.scaleInstance(xScale, yScale);

        AffineTransformation mirror_y = new AffineTransformation(1, 0, 0, 0, -1, rows);

        AffineTransformation world2pixel = new AffineTransformation(translate);
        world2pixel.compose(scale);
        world2pixel.compose(mirror_y);

        return world2pixel;
    }


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

        Tile t = getTile(52.58511188, 5.38673401, (byte)11);
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
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
            android.graphics.Bitmap bitmap = BitmapFactory.decodeFile(filename, options);

            options.inMutable = true;

            android.graphics.Bitmap bbb = android.graphics.Bitmap.createBitmap(256, 256, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas cc = new android.graphics.Canvas(bbb);

            cc.drawBitmap(bitmap, new Rect(Math.round((float)p1.x), Math.round((float)p1.y), Math.round((float)p2.x), Math.round((float)p2.y)),
                    new Rect(0,0,255,255), null);

            int size = 256 * 256;
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bbb.copyPixelsToBuffer(byteBuffer);
            //byteArray = byteBuffer.array();

//            Bitmap m = CanvasAdapter.newBitmap(256, 256, 0);
//            Canvas c = CanvasAdapter.newCanvas();
//            c.setBitmap(m);
//            c.drawBitmap(bitTest, (float)p2.x, (float)p2.y);
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
