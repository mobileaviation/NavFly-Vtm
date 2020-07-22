package com.mobileaviationtools.extras.Cache;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.extras.Classes.BaseChartType;
import com.mobileaviationtools.extras.tiling.openflightmaps.OpenFlightMapsTileDecoder;

import org.oscim.core.BoundingBox;
import org.oscim.core.Tile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OfflineTileCache extends TileCache {
    private String TAG = "OfflineTileCache";
    private OkHttpClient client;
    private OpenFlightMapsTileDecoder openFlightMapsTileDecoder;


    public OfflineTileCache(Context context, String cacheDirectory, String dbName) {
        super(context, cacheDirectory, dbName);
        tiles = new ArrayList<>();
        client = GetHttpClient(1);
        this.openFlightMapsTileDecoder = new OpenFlightMapsTileDecoder();
    }

    public void DownloadTiles(BoundingBox box, String[] baseUrls, BaseChartType baseChartType)
    {
        this.box = box;
        this.baseUrls = baseUrls;
        this.baseChartType = baseChartType;
        prepareTiles();
    }

    private BoundingBox box;
    private String[] baseUrls;
    private BaseChartType baseChartType;

    private OfflineTileDownloadEvent offlineTileDownloadEvent;
    public void SetOnOfflineTileDownloadEvent(OfflineTileDownloadEvent event)
    {
        offlineTileDownloadEvent = event;
    }

    private ArrayList<Tile> tiles;
    private void prepareTiles()
    {
        OfflineTile tt = new OfflineTile();
        int max_zoom  = (baseChartType==BaseChartType.openflightmaps) ? 12 : 13;
        for (int zoom = 6; zoom <= max_zoom; zoom++) {

            tt.getTileNumber(box.getMaxLatitude(), box.getMinLongitude(), zoom);
            int xBegin = tt.x;
            int yBegin = tt.y;
            tt.getTileNumber(box.getMinLatitude(), box.getMaxLongitude(), zoom);
            int xEnd = tt.x;
            int yEnd = tt.y;

            for (int x = xBegin; x<=xEnd; x++) {
                for (int y = yBegin; y <= yEnd; y++) {
                    Tile t = new Tile(x,y, (byte)zoom);
                    tiles.add(t);
                }
            }
        }

        downloadTiles dt = new downloadTiles();


        dt.tiles = tiles;
        dt.urls = baseUrls;
        dt.offlineTileDownloadEvent = offlineTileDownloadEvent;
        dt.baseChartType = baseChartType;
        dt.client = this.client;
        dt.openFlightMapsTileDecoder = this.openFlightMapsTileDecoder;
        dt.execute();
    }

    private class downloadTiles extends AsyncTask {
        public String[] urls;
        public ArrayList<Tile> tiles;
        public OfflineTileDownloadEvent offlineTileDownloadEvent;
        public BaseChartType baseChartType;
        private long progress;
        public OkHttpClient client;
        public OpenFlightMapsTileDecoder openFlightMapsTileDecoder;

        public downloadTiles() {

        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i(TAG, "Download Tiles Count: " + tiles.size());
            double c = tiles.size();
            double t = 0;

            for (Tile tile: tiles)
            {
                String url = setupBaseUrl(tile, baseUrls[0]);
                if (!checkTileNotAvialableOrExpired(tile)) {
                    try {
                        if (baseChartType == BaseChartType.openflightmaps) {
                            String[] urls = new String[2];
                            urls[0] = setupBaseUrl(tile, baseUrls[0]);
                            urls[1] = setupBaseUrl(tile, baseUrls[1]);

                            Bitmap bitmap = openFlightMapsTileDecoder.DownloadAndCombine(urls, client, null);
                            ByteArrayOutputStream encodedStream = openFlightMapsTileDecoder.EncodeBitmap(bitmap);
                            writeBytesToCache(encodedStream, tile);
                            encodedStream.close();
                        } else {

                            Request request = new Request.Builder()
                                    .url(url)
                                    .build();

                            doDownload(request, tile);
                        }

                        double p = setProgress(t,c,url);
                        t++;
                        Log.i(TAG, "Downloading : " + url + " Progress: " + Math.round(p) + "%");

                    } catch (Exception e) {
                        log.error("Download error: " + url + " : " + e.getMessage());
                        publishProgress(0d, true, e.getMessage());
                        //cancel(true);
                    }
                } else {
                    double p = setProgress(t,c,url);
                    t++;
                    Log.i(TAG, "Ignoring : " + url + " Progress: " + Math.round(p) + "%");
                }
            }
            return null;
        }

        private double setProgress(double t, double c, String url) {
            double p = (t / c) * 100;
            publishProgress(Math.round(p), false, url);
            return p;
        }

        private String setupBaseUrl(Tile tile, String url) {
            String ret = url.replace("{Z}", Byte.toString(tile.zoomLevel));
            ret = ret.replace("{X}", Integer.toString(tile.tileX));
            ret = ret.replace("{Y}", Integer.toString(tile.tileY));
            return ret;
        }

        private void doDownload(Request request, Tile tile) throws IOException {
            deleteOldTile(tile);
            Response response = client.newCall(request).execute();
            byte[] tilebytes = response.body().bytes();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(tilebytes.length);
            baos.write(tilebytes, 0, tilebytes.length);
            writeBytesToCache(baos, tile);
        }

        private void writeBytesToCache(ByteArrayOutputStream baos, Tile tile) {
            //Boolean success = true;
            saveDownloadedTile(tile, baos);
            //saveTile(tile, baos, success );
        }

        @Override
        protected void onPostExecute(Object o) {
            if (offlineTileDownloadEvent != null) offlineTileDownloadEvent.onFinished();
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {

            if (offlineTileDownloadEvent != null) {

                if (!(boolean)values[1]) offlineTileDownloadEvent.onProgress((long) values[0], (String)values[2]);
                if ((boolean)values[1]) offlineTileDownloadEvent.onError((String)values[2]);
            }

            super.onProgressUpdate(values);
        }
    }

    private OkHttpClient GetHttpClient(int requestMax)
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(requestMax);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(requestMax);

        return client;
    }
}
