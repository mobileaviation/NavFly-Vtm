package org.oscim.android.tiling.Overlay;

import org.oscim.layers.tile.MapTile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.QueryResult;
import org.oscim.tiling.source.ITileDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.oscim.tiling.QueryResult.FAILED;
import static org.oscim.tiling.QueryResult.SUCCESS;


public class OverlayTileDataSource implements ITileDataSource {
    static final Logger log = LoggerFactory.getLogger(OverlayTileSource.class);
    protected final ITileDecoder mTileDecoder;
    protected final OverlayTileSource mTileSource;
    protected final OverlayTileProvider mTilesProvider;

    public OverlayTileDataSource(OverlayTileSource tileSource, OverlayTileProvider tilesProvider, ITileDecoder tileDecoder)
    {
        mTileDecoder = tileDecoder;
        mTileSource = tileSource;
        mTilesProvider = tilesProvider;
    }

    @Override
    public void query(MapTile tile, ITileDataSink sink) {
        QueryResult res = FAILED;

        byte[] byte_tile = mTilesProvider.getTile(tile.tileX, tile.tileY, tile.zoomLevel);
        InputStream is = new ByteArrayInputStream(byte_tile);

        try {
            if (mTileDecoder.decode(tile, sink, is))
                res = SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }

        sink.completed(res);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void cancel() {

    }
}
