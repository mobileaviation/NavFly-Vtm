package com.mobileaviationtools.extras.tiling.openflightmaps;

import org.locationtech.jts.util.StringUtil;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.Tile;
import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.source.ITileDecoder;
import org.oscim.tiling.source.LwHttp;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

import okhttp3.OkHttpClient;

public class OpenFlightMapsTileSource extends UrlTileSource {
    public enum Type {
        base,
        aero
    };
    public enum Extention {
        jpg,
        png
    };

    static final Logger log = LoggerFactory.getLogger(LwHttp.class);

    private static final String DEFAULT_URL = "https://snapshots.openflightmaps.org/live/{airac}/tiles/world/epsg3857/{type}/512/latest";
    private static final String DEFAULT_PATH = "/{Z}/{X}/{Y}.{extention}";

    public String getOpenFlightMapsUrl()
    {
        return DEFAULT_URL + DEFAULT_PATH;
    }

    public String[] getUrls(MapTile tile) {
        String[] urls = getBaseUrls();
        String[] ret = new String[urls.length];

        for (int i = 0; i<urls.length; i++) {
            ret[i] = urls[i].replace("{Z}", Integer.toString(tile.zoomLevel))
                    .replace("{X}", Integer.toString(tile.tileX))
                    .replace("{Y}", Integer.toString(tile.tileY));
        }

        return ret;
    }

    //TODO buils a Airac cycle generator
    public String[] getBaseUrls() {
        String base = DEFAULT_URL.replace("{type}", "base")
                .replace("{airac}", "2007") +
                DEFAULT_PATH.replace("{extention}", "jpg");
        String aero = DEFAULT_URL.replace("{type}", "aero")
                .replace("{airac}", "2007") +
                DEFAULT_PATH.replace("{extention}", "png");

        String[] ret = new String[2];
        ret[0] = base;
        ret[1] = aero;

        return ret;
    }

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {

        public Builder() {
            super(DEFAULT_URL, DEFAULT_PATH);
            overZoom(12);
        }

        @Override
        public OpenFlightMapsTileSource build() {
            return new OpenFlightMapsTileSource(this);
        }
    }

    protected OpenFlightMapsTileSource(Builder<?> builder) {
        super(builder);
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    /**
     * Create BitmapTileSource for 'url'
     * <p/>
     * By default path will be formatted as: url/z/x/y.png
     * Use e.g. setExtension(".jpg") to overide ending or
     * implement getUrlString() for custom formatting.
     */
    public OpenFlightMapsTileSource(int zoomMin, int zoomMax) {
        this(DEFAULT_URL, DEFAULT_PATH, zoomMin, zoomMax);
    }

    public OpenFlightMapsTileSource(String url, int zoomMin, int zoomMax) {
        this(url, "/{Z}/{X}/{Y}.png", zoomMin, zoomMax);
    }

    public OpenFlightMapsTileSource(String url, int zoomMin, int zoomMax, String extension) {
        this(url, "/{Z}/{X}/{Y}" + extension, zoomMin, zoomMax);
    }

    public OpenFlightMapsTileSource(String url, String tilePath, int zoomMin, int zoomMax) {
        super(builder()
                .url(url)
                .tilePath(tilePath)
                .zoomMin(zoomMin)
                .zoomMax(zoomMax));
    }

    @Override
    public ITileDataSource getDataSource() {
        return new OpenFlightMapsTileDataSource(this, new OkHttpClient(), getHttpEngine());//, new TileDecoder(), getHttpEngine());
    }

    public static class TileDecoder implements ITileDecoder {

        @Override
        public boolean decode(Tile tile, ITileDataSink sink, InputStream is)
                throws IOException {

            Bitmap bitmap = CanvasAdapter.decodeBitmap(is);
            if (!bitmap.isValid()) {
                log.debug("{} invalid bitmap", tile);
                return false;
            }
            sink.setTileImage(bitmap);

            return true;
        }
    }
}
