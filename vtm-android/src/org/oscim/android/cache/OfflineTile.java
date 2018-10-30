package org.oscim.android.cache;

import org.locationtech.jts.geom.Coordinate;

import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

public class OfflineTile {
    public int x;
    public int y;
    public int z;
    public Coordinate tile;
    private String url;

    private String TAG = "TileDownload";

    public OfflineTile()
    {

    }

    public OfflineTile(String url, int x, int y, int z)
    {
        this.url = url;
        this.x = x;
        this.y = y;
        this.z = z;
        this.tile = new Coordinate(this.x, this.y, this.z);
    }

    public String getUrl(String base)
    {
        return base;
    }

    public void getTileNumber(final double lat, final double lon, final int zoom)
    {
        int xtile = (int)Math.floor( (lon + 180) / 360 * (1<<zoom) );
        int ytile = (int)Math.floor( (1 - Math.log(Math.tan(Math.toRadians(lat)) + 1 / Math.cos(Math.toRadians(lat))) / Math.PI)  / 2 * (1<<zoom));

        if (xtile < 0) xtile = 0;
        if (xtile >= (1<<zoom)) xtile = ((1<<zoom) -1 );
        if (ytile < 0) ytile = 0;
        if (ytile >= (1<<zoom)) ytile = ((1<<zoom) - 1 );

        this.x = xtile;
        this.y = ytile;
        this.z = zoom;

        this.tile = new Coordinate(this.x, this.y, this.z);
    }
}
