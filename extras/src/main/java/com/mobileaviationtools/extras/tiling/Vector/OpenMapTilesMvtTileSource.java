/*
 * Copyright 2013 Hannes Janetzek
 * Copyright 2016-2018 devemux86
 * Copyright 2018 boldtrn
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
package com.mobileaviationtools.extras.tiling.Vector;

import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;
import org.oscim.tiling.source.mvt.MvtTileDecoder;

public class OpenMapTilesMvtTileSource extends UrlTileSource {

    private static final String DEFAULT_URL = "https://api.maptiler.com/tiles/v3";
    private static final String DEFAULT_PATH = "/{Z}/{X}/{Y}.pbf";
    private static final String KEY_NAME = "key";
    private static final String API_KEY = "29WrAEe6HeyBZgOaLFda";

    public String getOpenTilesMapUrl()
    {
        return DEFAULT_URL + DEFAULT_PATH + getApiKeyStr();
    }

    private String getApiKeyStr()
    {
        StringBuilder sb = new StringBuilder();
        sb.append("?").append(KEY_NAME).append("=").append(API_KEY);
        return sb.toString();
    }



    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {
        private String locale = "";

        public Builder() {
            super(DEFAULT_URL, DEFAULT_PATH);
            overZoom(14);
        }

        public T locale(String locale) {
            this.locale = locale;
            return self();
        }

        public OpenMapTilesMvtTileSource build() {
            return new OpenMapTilesMvtTileSource(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    private final String locale;

    public OpenMapTilesMvtTileSource(Builder<?> builder) {
        super(builder);
        this.locale = builder.locale;
    }

    public OpenMapTilesMvtTileSource() {
        this(builder());
    }

    public OpenMapTilesMvtTileSource(String urlString) {
        this(builder().url(urlString));
    }

    @Override
    public ITileDataSource getDataSource() {
        return new OverzoomTileDataSource(new UrlTileDataSource(this, new MvtTileDecoder(locale), getHttpEngine()), mOverZoom);
    }
}
