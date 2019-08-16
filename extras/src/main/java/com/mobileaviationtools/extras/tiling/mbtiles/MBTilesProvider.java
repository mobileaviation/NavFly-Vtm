package com.mobileaviationtools.extras.tiling.mbtiles;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;



import org.oscim.core.BoundingBox;
import org.oscim.core.Box;
import org.oscim.core.PointF;

import java.io.File;


public class MBTilesProvider {
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

    private SQLiteDatabase mDatabase;

    private String mPathToFile;

    // ------------------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------------------
    public MBTilesProvider(String pathToFile) {
        mPathToFile = pathToFile;
    }

    public Boolean open()
    {
        if (new File(mPathToFile).exists()) {
            int flags = SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS;
            try {
                this.mDatabase = SQLiteDatabase.openDatabase(mPathToFile, null, flags);
                mErrorMessage = "";
                if (this.mDatabase != null) {
                    this.calculateZoomConstraints();
                    this.calculateBounds();
                    return this.mDatabase.isOpen();
                } else {
                    mErrorMessage = "Error opening database file: " + mPathToFile;
                    return false;
                }
            } catch (Exception ee) {
                mErrorMessage = "Exception opening database file: " + mPathToFile +
                        " with message: " + ee.getMessage();
                return false;
            }
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
        if (this.isZoomLevelAvailable(z) && this.isDatabaseAvailable()) {
            String[] projection = {
                    "tile_data"
            };
            int row = ((int) (Math.pow(2, z) - y) - 1);
            String predicate = "tile_row = ? AND tile_column = ? AND zoom_level = ?";
            String[] values = {
                    String.valueOf(row), String.valueOf(x), String.valueOf(z)
            };
            Cursor c = this.mDatabase.query("tiles", projection, predicate, values, null, null, null);
            if (c != null) {
                c.moveToFirst();
                if (!c.isAfterLast()) {
                    tile = c.getBlob(0);
                }
                c.close();
            }
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
        if (this.mDatabase != null) {
            this.mDatabase.close();
            this.mDatabase = null;
        }
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
        if (this.isDatabaseAvailable()) {
            String[] projection = new String[] {
                    "value"
            };

            String[] minArgs = new String[] {
                    "minzoom"
            };

            String[] maxArgs = new String[] {
                    "maxzoom"
            };

            Cursor c;

            c = this.mDatabase.query("metadata", projection, "name = ?", minArgs, null, null, null);

            c.moveToFirst();
            if (!c.isAfterLast()) {
                this.mMinimumZoom = c.getInt(0);
            }
            c.close();

            c = this.mDatabase.query("metadata", projection, "name = ?", maxArgs, null, null, null);

            c.moveToFirst();
            if (!c.isAfterLast()) {
                this.mMaximumZoom = c.getInt(0);
            }
            c.close();
        }
    }

    private void calculateBounds() {
        if (this.isDatabaseAvailable()) {
            String[] projection = new String[] {
                    "value"
            };

            String[] subArgs = new String[] {
                    "bounds"
            };

            Cursor c = this.mDatabase.query("metadata", projection, "name = ?", subArgs, null, null, null);

            c.moveToFirst();
            if (!c.isAfterLast()) {
                String[] parts = c.getString(0).split(",\\s*");

                float w = Float.parseFloat(parts[0]);
                float s = Float.parseFloat(parts[1]);
                float e = Float.parseFloat(parts[2]);
                float n = Float.parseFloat(parts[3]);

                mBounds = new Box();
                mBounds.add(e, n);
                mBounds.add(w, s);
            }
            c.close();
        }
    }

    private boolean isDatabaseAvailable() {
        return (this.mDatabase != null) && (this.mDatabase.isOpen());
    }
}
