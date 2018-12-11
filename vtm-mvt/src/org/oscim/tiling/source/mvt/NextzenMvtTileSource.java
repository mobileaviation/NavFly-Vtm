/*
 * Copyright 2018 devemux86
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
package org.oscim.tiling.source.mvt;

import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.OverzoomTileDataSource;
import org.oscim.tiling.source.UrlTileDataSource;
import org.oscim.tiling.source.UrlTileSource;

public class NextzenMvtTileSource extends UrlTileSource {

    private static final String DEFAULT_URL = "https://tile.nextzen.org/tilezen/vector/v1/all";
    private static final String DEFAULT_PATH = "/{Z}/{X}/{Y}.mvt";

    public String getNextzenUrl()
    {
        return DEFAULT_URL + DEFAULT_PATH + getApiKeyStr();
    }

    public static class Builder<T extends Builder<T>> extends UrlTileSource.Builder<T> {
        private String locale = "";

        public Builder() {
            super(DEFAULT_URL, DEFAULT_PATH);
            keyName("api_key");
            overZoom(16);
        }

        public T locale(String locale) {
            this.locale = locale;
            return self();
        }

        public NextzenMvtTileSource build() {
            return new NextzenMvtTileSource(this);
        }
    }

    @SuppressWarnings("rawtypes")
    public static Builder<?> builder() {
        return new Builder();
    }

    private final String locale;

    public NextzenMvtTileSource(Builder<?> builder) {
        super(builder);
        this.locale = builder.locale;
    }

    public NextzenMvtTileSource() {
        this(builder());
    }

    public NextzenMvtTileSource(String urlString) {
        this(builder().url(urlString));
    }

    @Override
    public ITileDataSource getDataSource() {
        return new OverzoomTileDataSource(new UrlTileDataSource(this, new MvtTileDecoder(locale), getHttpEngine()), mOverZoom);
    }
}
