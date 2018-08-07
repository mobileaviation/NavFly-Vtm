package org.oscim.android.tiling.mbtiles;

import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.Tile;
import org.oscim.tiling.ITileDataSink;
import org.oscim.tiling.ITileDataSource;
import org.oscim.tiling.TileSource;
import org.oscim.tiling.source.ITileDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

public class MBTilesTileSource extends TileSource {
    static final Logger log = LoggerFactory.getLogger(MBTilesTileSource.class);
    private String mMbTilesFile;
    private MBTilesProvider mMbTileProvider;

    public MBTilesTileSource(String mbtiles_file)
    {
        super();
        //super(zoomMin, zoomMax);
        mMbTilesFile = mbtiles_file;
        mMbTileProvider = new MBTilesProvider(mMbTilesFile);
    }

    @Override
    public ITileDataSource getDataSource() {
        return new MBTilesTileDataSource(this, mMbTileProvider, new MBTileDecoder());
    }

    @Override
    public OpenResult open() {
        if (mMbTileProvider.open()) {
            this.mZoomMin = mMbTileProvider.getMinimumZoom();
            this.mZoomMax = mMbTileProvider.getMaximumZoom();
            return OpenResult.SUCCESS;
        }
        else
            return new OpenResult(mMbTileProvider.GetLastErrorMessage());
    }

    @Override
    public void close() {
        mMbTileProvider.close();
    }

    public class MBTileDecoder implements ITileDecoder {

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
