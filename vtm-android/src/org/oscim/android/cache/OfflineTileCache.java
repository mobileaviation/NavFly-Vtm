package org.oscim.android.cache;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.oscim.core.BoundingBox;
import org.oscim.core.Tile;

import java.io.ByteArrayOutputStream;
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

    private class downloadTiles extends AsyncTask
    {
        public String url;
        public ArrayList<Tile> tiles;
        public OfflineTileDownloadEvent offlineTileDownloadEvent;
        private long progress;

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

                OkHttpClient client = GetHttpClient();
                try {
                    Response response = client.newCall(request).execute();
                    byte[] tilebytes = response.body().bytes();
                    double p = (t / c) * 100;
                    t = t + 1;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream(tilebytes.length);
                    baos.write(tilebytes, 0, tilebytes.length);
                    Boolean success = true;
                    saveTile(tile, baos, success );
                    progress = Math.round(p);
                    publishProgress(progress);
                    Log.i(TAG, "Downloading : " + url + " Progress: " + Math.round(p) + "%" + " " + success.toString()  );


                }
                catch (Exception e)
                {
                    log.error("Download error: " + e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            if (offlineTileDownloadEvent != null) offlineTileDownloadEvent.onFinished();
            super.onPostExecute(o);
        }

        @Override
        protected void onProgressUpdate(Object[] values) {
            if (offlineTileDownloadEvent != null) offlineTileDownloadEvent.onProgress((long)values[0]);
            super.onProgressUpdate(values);
        }
    }



    private OkHttpClient GetHttpClient()
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(10);

        return client;
    }
}
