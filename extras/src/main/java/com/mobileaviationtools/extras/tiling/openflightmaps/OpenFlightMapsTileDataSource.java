package com.mobileaviationtools.extras.tiling.openflightmaps;

import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;


import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.android.canvas.AndroidCanvas;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileCache;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.source.HttpEngine;
import org.oscim.tiling.source.ITileDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenFlightMapsTileDataSource implements ITileDataSource {
    static final Logger log = LoggerFactory.getLogger(OpenFlightMapsTileDataSource.class);

    //protected final ITileDecoder mTileDecoder;
    protected final OpenFlightMapsTileSource mTileSource;
    protected final HttpEngine mConn;
    protected final boolean mUseCache;
    protected final OkHttpClient mClient;
    protected final OpenFlightMapsTileDecoder mDecoder;

    public OpenFlightMapsTileDataSource(OpenFlightMapsTileSource tileSource, OkHttpClient client, HttpEngine conn) { //, ITileDecoder tileDecoder, HttpEngine conn) {
        //mTileDecoder = tileDecoder;
        mTileSource = tileSource;
        mUseCache = (tileSource.tileCache != null);
        mClient = client;
        mConn = conn;
        mDecoder = new OpenFlightMapsTileDecoder();
    }

    @Override
    public void query(MapTile tile, ITileDataSink mapDataSink) {
        com.mobileaviationtools.extras.Cache.TileCache cache = (com.mobileaviationtools.extras.Cache.TileCache) mTileSource.tileCache;
        android.graphics.Bitmap baseBitmap = null;
        QueryResult res = QueryResult.FAILED;

        if (mUseCache) {
            ITileCache.TileReader c = cache.getTile(tile);
            if (c != null) {
                try {
                    InputStream is = c.getInputStream();
                    baseBitmap = BitmapFactory.decodeStream(is);
                    mapDataSink.setTileImage(new AndroidBitmap(baseBitmap));
                    mapDataSink.completed(QueryResult.SUCCESS);
                    is.close();
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] urls = mTileSource.getUrls(tile);
//        ArrayList<android.graphics.Bitmap> bitmaps = new ArrayList<>();

//        for (String u : urls) {
//            //baseBitmap = download(u);
//            baseBitmap = mDecoder.DownloadTile(u, mClient, mTileSource);
//            bitmaps.add(baseBitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true));
////        }
//
//        android.graphics.Bitmap returnBitmap = combine(bitmaps);

        android.graphics.Bitmap returnBitmap = mDecoder.DownloadAndCombine(urls, mClient, mTileSource);

        if (returnBitmap != null) {
            if (mUseCache) {
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //returnBitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, stream);
                ByteArrayOutputStream stream = mDecoder.EncodeBitmap(returnBitmap);
                cache.saveTile(tile, stream, true);
            }

            mapDataSink.setTileImage(new AndroidBitmap(returnBitmap));
            res = QueryResult.SUCCESS;
        }

        mapDataSink.completed(res);
    }

//    private android.graphics.Bitmap combine(ArrayList<android.graphics.Bitmap> bitmaps) {
//        if (bitmaps.size()==2) {
//            android.graphics.Bitmap base = bitmaps.get(0);
//            Canvas canvas = new Canvas(base);
//            canvas.drawBitmap( bitmaps.get(1), 0,0, new Paint());
//            return base;
//        } else
//        {
//            return null;
//        }
//    }
//
//    private android.graphics.Bitmap download(String url) {
//        try {
//            URL u = new URL(url);
//            Request.Builder builder = new Request.Builder()
//                    .url(u);
//            for (Map.Entry<String, String> opt : mTileSource.getRequestHeader().entrySet())
//                builder.addHeader(opt.getKey(), opt.getValue());
//            Request request = builder.build();
//            Response response = mClient.newCall(request).execute();
//            byte bytes[] = response.body().bytes();
//            return android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        return null;
//    }
//
//    private Bitmap decode(InputStream is) {
//        Bitmap bitmap = null;
//        try {
//            bitmap = CanvasAdapter.decodeBitmap(is);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        return  bitmap;
//    }


    @Override
    public void dispose() {

    }

    @Override
    public void cancel() {

    }
}
