/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2016 Izumi Kawashima
 *
 * This file is part of the OpenScienceMap project (http://www.opensciencemap.org).
 *
 * This program is free software: you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.oscim.tiling.source;

import org.oscim.core.Tile;
import org.oscim.map.Viewport;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.LwHttp.LwHttpFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;

public abstract class UrlTileSource extends TileSource {

    public abstract static class Builder<T extends Builder<T>> extends TileSource.Builder<T> {
        protected String tilePath;
        protected String url;
        private HttpEngine.Factory engineFactory;
        private String keyName = "key";
        private String apiKey;

        protected Builder() {
        }

        protected Builder(String url, String tilePath) {
            this.url = url;
            this.tilePath = tilePath;
        }

        protected Builder(String url, String tilePath, int zoomMin, int zoomMax) {
            this(url, tilePath);
            this.zoomMin = zoomMin;
            this.zoomMax = zoomMax;
        }

        public T keyName(String keyName) {
            this.keyName = keyName;
            return self();
        }

        public T apiKey(String apiKey) {
            this.apiKey = apiKey;
            return self();
        }

        public T tilePath(String tilePath) {
            this.tilePath = tilePath;
            return self();
        }

        public T url(String url) {
            this.url = url;
            return self();
        }

        public T httpFactory(HttpEngine.Factory factory) {
            this.engineFactory = factory;
            return self();
        }

    }

    public static final TileUrlFormatter URL_FORMATTER = new DefaultTileUrlFormatter();
    private final URL mUrl;
    private final String[] mTilePath;

    private HttpEngine.Factory mHttpFactory;
    private Map<String, String> mRequestHeaders = Collections.emptyMap();
    private TileUrlFormatter mTileUrlFormatter = URL_FORMATTER;
    private String mKeyName = "key";
    private String mApiKey;

    public interface TileUrlFormatter {
        String formatTilePath(UrlTileSource tileSource, Tile tile);
    }

    protected UrlTileSource(Builder<?> builder) {
        super(builder);
        mKeyName = builder.keyName;
        mApiKey = builder.apiKey;
        mUrl = makeUrl(builder.url);
        mTilePath = builder.tilePath.split("\\{|\\}");
        mHttpFactory = builder.engineFactory;
    }

    protected UrlTileSource(String urlString, String tilePath) {
        this(urlString, tilePath, Viewport.MIN_ZOOM_LEVEL, Viewport.MAX_ZOOM_LEVEL);
    }

    protected UrlTileSource(String urlString, String tilePath, int zoomMin, int zoomMax) {
        super(zoomMin, zoomMax);
        mUrl = makeUrl(urlString);
        mTilePath = makeTilePath(tilePath);
    }

    private String[] makeTilePath(String tilePath) {
        if (tilePath == null)
            throw new IllegalArgumentException("tilePath cannot be null.");

        return tilePath.split("\\{|\\}");
    }

    private URL makeUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e);
        }
        return url;
    }

    @Override
    public OpenResult open() {
        return OpenResult.SUCCESS;
    }

    @Override
    public void close() {

    }

    public void setApiKey(String apiKey) {
        mApiKey = apiKey;
    }

    public URL getUrl() {
        return mUrl;
    }

    public String getTileUrl(Tile tile) {
        StringBuilder sb = new StringBuilder();
        sb.append(mUrl).append(mTileUrlFormatter.formatTilePath(this, tile));
        if (mApiKey != null) {
            sb.append("?").append(mKeyName).append("=").append(mApiKey);
        }
        return sb.toString();
    }

    public String getApiKeyStr()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("?").append(mKeyName).append("=").append(mApiKey);
        return sb.toString();
    }

    public void setHttpEngine(HttpEngine.Factory httpFactory) {
        mHttpFactory = httpFactory;
    }

    public void setHttpRequestHeaders(Map<String, String> options) {
        mRequestHeaders = options;
    }

    public Map<String, String> getRequestHeader() {
        return mRequestHeaders;
    }

    public String[] getTilePath() {
        return mTilePath;
    }

    public void setUrlFormatter(TileUrlFormatter formatter) {
        mTileUrlFormatter = formatter;
    }

    public TileUrlFormatter getUrlFormatter() {
        return mTileUrlFormatter;
    }

    public HttpEngine getHttpEngine() {
        if (mHttpFactory == null) {
            mHttpFactory = new LwHttpFactory();
        }
        return mHttpFactory.create(this);
    }

    public int tileXToUrlX(int tileX) {
        return tileX;
    }

    public int tileYToUrlY(int tileY) {
        return tileY;
    }

    public int tileZToUrlZ(int tileZ) {
        return tileZ;
    }

    private static class DefaultTileUrlFormatter implements TileUrlFormatter {
        @Override
        public String formatTilePath(UrlTileSource tileSource, Tile tile) {

            StringBuilder sb = new StringBuilder();
            for (String b : tileSource.getTilePath()) {
                if (b.length() == 1) {
                    switch (b.charAt(0)) {
                        case 'X':
                            sb.append(tileSource.tileXToUrlX(tile.tileX));
                            continue;
                        case 'Y':
                            sb.append(tileSource.tileYToUrlY(tile.tileY));
                            continue;
                        case 'Z':
                            sb.append(tileSource.tileZToUrlZ(tile.zoomLevel));
                            continue;
                        default:
                            break;
                    }
                }
                sb.append(b);
            }
            return sb.toString();
        }
    }
}
