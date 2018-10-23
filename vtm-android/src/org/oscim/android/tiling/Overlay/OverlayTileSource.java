package org.oscim.android.tiling.Overlay;

import org.oscim.android.tiling.mbtiles.MBTilesTileSource;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.BoundingBox;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.ITileDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class OverlayTileSource extends TileSource {
    static final Logger log = LoggerFactory.getLogger(MBTilesTileSource.class);
    private String mOverlayFile;
    private BoundingBox refBox;
    private OverlayTileProvider mOverlayTileProvider;

    public OverlayTileSource(String file, BoundingBox refBox)
    {
        mOverlayFile = file;
        this.refBox = refBox;
        mOverlayTileProvider = new OverlayTileProvider(file, refBox);
    }

    @Override
    public ITileDataSource getDataSource() {
        return
                new OverlayTileDataSource(this, mOverlayTileProvider, new OverlayTileDecoder());
    }

    @Override
    public OpenResult open() {
        if (mOverlayTileProvider.open()) {
            this.mZoomMin = mOverlayTileProvider.getMinimumZoom();
            this.mZoomMax = mOverlayTileProvider.getMaximumZoom();
            return OpenResult.SUCCESS;
        }
        else
            return new OpenResult(mOverlayTileProvider.GetLastErrorMessage());
    }

    @Override
    public void close() {
        mOverlayTileProvider.close();
    }

    public class OverlayTileDecoder implements ITileDecoder {

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
