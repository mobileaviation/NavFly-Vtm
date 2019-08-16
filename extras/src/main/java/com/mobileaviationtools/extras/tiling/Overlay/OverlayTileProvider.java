package com.mobileaviationtools.extras.tiling.Overlay;

import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.util.AffineTransformation;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.backend.canvas.Bitmap;
import org.oscim.core.BoundingBox;
import org.oscim.core.Box;
import org.oscim.core.Tile;
import java.io.File;

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
    private byte[] mFileBytes;
    private android.graphics.Bitmap mBitmap;
    private Integer mWidth;
    private Integer mHeight;


    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    public OverlayTileProvider(String pathToFile, BoundingBox imageOverlayBounds) {
        mImageOverlayBounds = imageOverlayBounds;
        mPathToFile = pathToFile;
    }

    public OverlayTileProvider(byte[] fileBytes, BoundingBox imageOverlayBounds) {
        mImageOverlayBounds = imageOverlayBounds;
        mFileBytes = fileBytes;
    }

    public Boolean open()
    {
        if (mFileBytes == null) {
            return openFile();
        }
        else
        {
            return openFileBytes();
        }
    }

    private Boolean openFileBytes()
    {
        if (mFileBytes != null)
            if (mFileBytes.length>0) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = android.graphics.Bitmap.Config.ARGB_8888;
                mBitmap = BitmapFactory.decodeByteArray(mFileBytes, 0, mFileBytes.length, options);
                if (mBitmap != null)
                {
                    mWidth = mBitmap.getWidth();
                    mHeight = mBitmap.getHeight();

                    this.calculateZoomConstraints();
                    this.calculateBounds();
                    return true;
                }
                else {
                    return false;
                }
                }
            else return false;
        else return false;
    }

    private Boolean openFile()
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
            if (this.isWithinBounds(t)) {
                BoundingBox tbb = t.getBoundingBox();

                Coordinate cc1 = new Coordinate(mImageOverlayBounds.getMinLongitude(), mImageOverlayBounds.getMinLatitude());
                Coordinate cc2 = new Coordinate(mImageOverlayBounds.getMaxLongitude(), mImageOverlayBounds.getMaxLatitude());
                Envelope wBox = new Envelope(cc1, cc2);
                Envelope pBox = new Envelope(0, mWidth, 0, mHeight);
                AffineTransformation wp = getWorldToRectangle(wBox, pBox);

                Coordinate testC1 = new Coordinate(tbb.getMinLongitude(), tbb.getMaxLatitude());
                Coordinate p1 = wp.transform(testC1, new Coordinate());

                Coordinate testC2 = new Coordinate(tbb.getMaxLongitude(), tbb.getMinLatitude());
                Coordinate p2 = wp.transform(testC2, new Coordinate());

                android.graphics.Bitmap tempbb = android.graphics.Bitmap.createBitmap(
                        (int)Math.abs(p2.x-p1.x), (int)Math.abs(p2.y-p1.y),
                        android.graphics.Bitmap.Config.ARGB_8888);

                android.graphics.Canvas cc = new android.graphics.Canvas(tempbb);

                cc.drawBitmap(mBitmap
                        , new Rect(Math.round((float) p1.x), Math.round((float) p1.y), Math.round((float) p2.x), Math.round((float) p2.y)),
                        new Rect(0, 0, (int)Math.abs(p2.x-p1.x), (int)Math.abs(p2.y-p1.y)), null);

                Bitmap b = new AndroidBitmap(getResizedBitmap(tempbb, 256, 256, false));
                tile = b.getPngEncodedData();
            }

        }
        return tile;
    }

    private static android.graphics.Bitmap getResizedBitmap(android.graphics.Bitmap bm, int newWidth, int newHeight, boolean isNecessaryToKeepOrig) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        android.graphics.Bitmap scaledBitmap = android.graphics.Bitmap.createBitmap(newWidth, newHeight,
                android.graphics.Bitmap.Config.ARGB_8888);

        android.graphics.Canvas canvas = new android.graphics.Canvas(scaledBitmap);
        canvas.setMatrix(matrix);
        canvas.drawBitmap(bm, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;
    }

    private AffineTransformation getWorldToRectangle(Envelope worldEnvelop, Envelope pixelsEnvelop)
    {
        int cols = (int) pixelsEnvelop.getWidth();
        int rows = (int) pixelsEnvelop.getHeight();

        double worldWidth = worldEnvelop.getWidth();
        double worldHeight = worldEnvelop.getHeight();

        double x = -worldEnvelop.getMinX();
        double y = -worldEnvelop.getMinY();
        AffineTransformation translate = AffineTransformation.translationInstance(x,y);

        double xScale = cols / worldWidth;
        double yScale = rows / worldHeight;
        AffineTransformation scale = AffineTransformation.scaleInstance(xScale, yScale);

        AffineTransformation mirror_y = new AffineTransformation(1, 0, 0, 0, -1, rows);

        AffineTransformation world2pixel = new AffineTransformation(translate);
        world2pixel.compose(scale);
        world2pixel.compose(mirror_y);

        return world2pixel;
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

    public boolean isWithinBounds(Tile tile)
    {
        return tile.getBoundingBox().intersects(mImageOverlayBounds);
    }

    // ------------------------------------------------------------------------
    // Private Methods
    // ------------------------------------------------------------------------

    private void calculateZoomConstraints() {
        this.mMinimumZoom = 10;
        this.mMaximumZoom = 20;
    }

    private void calculateBounds() {
        mBounds = new Box();
        mBounds.add(mImageOverlayBounds.getMinLongitude(), mImageOverlayBounds.getMaxLatitude());
        mBounds.add(mImageOverlayBounds.getMaxLongitude(), mImageOverlayBounds.getMinLatitude());
    }
}
