package com.mobileaviationtools.extras.Cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

    public OfflineTileCache(Context context, String cacheDirectory, String dbName) {
        super(context, cacheDirectory, dbName);
        tiles = new ArrayList<>();
    }

    public void DownloadTiles(BoundingBox box, String baseUrl)
    {
        this.box = box;
        this.baseUrl = baseUrl;
        prepareTiles();
    }

    private BoundingBox box;
    private String baseUrl;

    private OfflineTileDownloadEvent offlineTileDownloadEvent;
    public void SetOnOfflineTileDownloadEvent(OfflineTileDownloadEvent event)
    {
        offlineTileDownloadEvent = event;
    }

    private ArrayList<Tile> tiles;
    private void prepareTiles()
    {
        OfflineTile tt = new OfflineTile();
        for (int zoom = 6; zoom <= 13; zoom++) {

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
        dt.url = baseUrl;
        dt.offlineTileDownloadEvent = offlineTileDownloadEvent;
        dt.execute();
    }

    private class downloadTiles extends AsyncTask {
        public String url;
        public ArrayList<Tile> tiles;
        public OfflineTileDownloadEvent offlineTileDownloadEvent;
        private long progress;
        private OkHttpClient client;

        public downloadTiles() {
            client = GetHttpClient(1);
        }

        @Override
        protected Object doInBackground(Object[] objects) {
            Log.i(TAG, "Download Tiles Count: " + tiles.size());
            double c = tiles.size();
            double t = 0;
            for (Tile tile: tiles)
            {
                String url = baseUrl.replace("{Z}", Byte.toString(tile.zoomLevel));
                url = url.replace("{X}", Integer.toString(tile.tileX));
                url = url.replace("{Y}", Integer.toString(tile.tileY));

                Request request = new Request.Builder()
                        .url(url)
                        .build();

                try {
                    doDownload(request, tile);
                    double p = (t / c) * 100;
                    publishProgress(Math.round(p), false, request.url().toString());
                    t++;
                    Log.i(TAG, "Downloading : " + request.url().toString() + " Progress: " + Math.round(p) + "%" );
                }
                catch (Exception e)
                {
                    log.error("Download error: " + url + " : " + e.getMessage());
                    publishProgress(0d, true, e.getMessage());
                    //cancel(true);
                }
            }
            return null;
        }

        private void doDownload(Request request, Tile tile) throws IOException {
            deleteOldTile(tile);
            Response response = client.newCall(request).execute();
            byte[] tilebytes = response.body().bytes();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(tilebytes.length);
            baos.write(tilebytes, 0, tilebytes.length);
            Boolean success = true;
            saveTile(tile, baos, success );
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
