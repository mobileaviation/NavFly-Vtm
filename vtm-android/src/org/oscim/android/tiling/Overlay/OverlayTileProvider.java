package org.oscim.android.tiling.Overlay;

import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import org.oscim.android.canvas.AndroidGraphics;
import org.oscim.backend.CanvasAdapter;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.backend.canvas.Canvas;
import org.oscim.core.BoundingBox;
import org.oscim.core.Box;
import org.oscim.core.GeoPoint;
import org.oscim.core.MapPosition;
import org.oscim.core.Point;
import org.oscim.core.Tile;
import org.oscim.map.ViewController;
import org.oscim.utils.MinHeap;

import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;

public class OverlayTileProvider {
    private String mErrorMessage;
    public String GetLastErrorMessage()
    {
        return mErrorMessage;
    }

    // ------------------------------------------------------------------------
    // Instance Variables
    // ------------------------------------------------------------------------

    private int mMinimumZoom = Integer.MIN_VALUE;

    private int mMaximumZoom = Integer.MAX_VALUE;

    private Box mBounds;
    private BoundingBox mImageOverlayBounds;

    private String mPathToFile;
    private android.graphics.Bitmap mBitmap;
    private Integer mWidth;
    private Integer mHeight;

    private MapPosition mapPosition;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    public OverlayTileProvider(String pathToFile, BoundingBox imageOverlayBounds) {
        mImageOverlayBounds = imageOverlayBounds;
        mPathToFile = pathToFile;
    }

    public Boolean open()
    {
        if (new File(mPathToFile).exists()) {

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
                mBitmap = BitmapFactory.decodeFile(mPathToFile, options);
            }
            catch (Exception e)
            {
                return false;
            }
            if (mBitmap != null)
            {
                mWidth = mBitmap.getWidth();
                mHeight = mBitmap.getHeight();

                mapPosition = new MapPosition();
                mapPosition.setByBoundingBox(mImageOverlayBounds, mWidth, mHeight);
                mapPosition.setPosition(mImageOverlayBounds.getMaxLatitude(), mImageOverlayBounds.getMinLongitude());

                this.calculateZoomConstraints();
                this.calculateBounds();
                return true;
            }



            return false;
        }
        else
        {
            mErrorMessage = "File: " + mPathToFile + " not present or accessible!";
            return false;
        }
    }

    // ------------------------------------------------------------------------
    // TileProvider Interface
    // ------------------------------------------------------------------------
    public byte[] getTile(int x, int y, int z) {
        byte[] tile = null;
        if (this.isZoomLevelAvailable(z)) {
            Tile t = new Tile(x, y, (byte)z);
            BoundingBox tbb = t.getBoundingBox();
            ViewController viewController = new ViewController();
            viewController.setViewSize(mWidth, mHeight);
            viewController.setMapPosition(mapPosition);

            Point p1  = new Point();
            viewController.toScreenPoint(new GeoPoint(tbb.getMaxLatitude(), tbb.getMinLongitude()), p1);

            Point p2  = new Point();
            //59.514808654785156 46.290672302246094
            viewController.toScreenPoint(new GeoPoint(tbb.getMinLatitude(), tbb.getMaxLongitude()), p2);

            android.graphics.Bitmap bbb = android.graphics.Bitmap.createBitmap(256, 256, android.graphics.Bitmap.Config.ARGB_8888);
            android.graphics.Canvas cc = new android.graphics.Canvas(bbb);

            cc.drawBitmap(mBitmap
                    , new Rect(Math.round((float)p1.x), Math.round((float)p1.y), Math.round((float)p2.x), Math.round((float)p2.y)),
                    new Rect(0,0,255,255), null);

            int size = 256 * 256;
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bbb.copyPixelsToBuffer(byteBuffer);
            tile = byteBuffer.array();

        }
        return tile;
    }

    // ------------------------------------------------------------------------
    // Closeable Interface
    // ------------------------------------------------------------------------

    /**
     * Closes the provider, cleaning up any background resources.
     *
     * <p>
     * You must call {@link #close()} when you are finished using an instance of
     * this provider. Failing to do so may leak resources, such as the backing
     * SQLiteDatabase.
     * </p>
     */
    public void close() {

    }

    // ------------------------------------------------------------------------
    // Public Methods
    // ------------------------------------------------------------------------

    /**
     * The minimum zoom level supported by this provider.
     *
     * @return the minimum zoom level supported or {//@link Integer.MIN_VALUE} if
     *         it could not be determined.
     */
    public int getMinimumZoom() {
        return this.mMinimumZoom;
    }

    /**
     * The maximum zoom level supported by this provider.
     *
     * @return the maximum zoom level supported or {//@link Integer.MAX_VALUE} if
     *         it could not be determined.
     */
    public int getMaximumZoom() {
        return this.mMaximumZoom;
    }

    /**
     * The geographic bounds available from this provider.
     *
     * @return the geographic bounds available or {@link null} if it could not
     *         be determined.
     */
    public Box getBounds() {
        return this.mBounds;
    }

    /**
     * Determines if the requested zoom level is supported by this provider.
     *
     * @param zoom The requested zoom level.
     * @return {@code true} if the requested zoom level is supported by this
     *         provider.
     */
    public boolean isZoomLevelAvailable(int zoom) {
        return (zoom >= this.mMinimumZoom) && (zoom <= this.mMaximumZoom);
    }

    // ------------------------------------------------------------------------
    // Private Methods
    // ------------------------------------------------------------------------

    private void calculateZoomConstraints() {
        this.mMinimumZoom = 2;
        this.mMaximumZoom = 20;
    }

    private void calculateBounds() {
        mBounds = new Box();
        mBounds.add(mImageOverlayBounds.getMinLongitude(), mImageOverlayBounds.getMaxLatitude());
        mBounds.add(mImageOverlayBounds.getMaxLongitude(), mImageOverlayBounds.getMinLatitude());
    }
}
