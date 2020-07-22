package com.mobileaviationtools.extras.tiling.openflightmaps;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.UrlTileSource;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OpenFlightMapsTileDecoder {
    private Bitmap[] bitmaps;
    public Bitmap DownloadTile(String url, OkHttpClient client, UrlTileSource tileSource) {
        try {
            URL u = new URL(url);
            Request.Builder builder = new Request.Builder()
                    .url(u);
            if (tileSource != null)
                for (Map.Entry<String, String> opt : tileSource.getRequestHeader().entrySet())
                    builder.addHeader(opt.getKey(), opt.getValue());
            Request request = builder.build();
            Response response = client.newCall(request).execute();
            byte bytes[] = response.body().bytes();
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Bitmap CombineBitmaps(Bitmap[] bitmaps) {
        if (bitmaps.length==2) {
            Bitmap base = bitmaps[0];
            Canvas canvas = new Canvas(base);
            canvas.drawBitmap( bitmaps[1], 0,0, new Paint());
            return base;
        } else
        {
            return null;
        }
    }

    public ByteArrayOutputStream EncodeBitmap(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream;
    }

    public Bitmap DownloadAndCombine(String[] urls, OkHttpClient client, UrlTileSource tileSource) {
        if (bitmaps == null)
            bitmaps = new Bitmap[2];

        for (int i=0; i<urls.length; i++) {
            Bitmap baseBitmap = DownloadTile(urls[i], client, tileSource);
            bitmaps[i] = baseBitmap.copy(android.graphics.Bitmap.Config.ARGB_8888, true);
        }
        Bitmap ret = CombineBitmaps(bitmaps);

        return ret;
    }
}
