package com.mobileaviationtools.nav_fly.Classes;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import java.io.File;
import java.util.ArrayList;

public class MBTilePreview {
    private class tile
    {
        public byte[] tile;
        public Integer tile_column;
        public Integer tile_row;
    }

    public MBTilePreview(String mbTileFile)
    {
        mPathToFile = mbTileFile;
    }

    private boolean openDatabase()
    {
        if (new File(mPathToFile).exists())
        {
            int flags = SQLiteDatabase.OPEN_READONLY | SQLiteDatabase.NO_LOCALIZED_COLLATORS;
            try {
                this.mDatabase = SQLiteDatabase.openDatabase(mPathToFile, null, flags);
                if (this.mDatabase != null) {

                    return this.mDatabase.isOpen();
                }
                else
                {
                    message = "Error opening mbTileFile " + mPathToFile;
                    return false;
                }
            }
            catch (Exception ee)
            {
                message = ee.getMessage();
                return false;
            }
        }
        else
        {
            message = "File: " + mPathToFile + " not present or accessible!";
            return false;
        }

    }

    private Integer getZoomLevel()
    {
        Integer zoomlevel = 10;
        Cursor c = this.mDatabase.rawQuery("SELECT zoom_level FROM (" +
                "SELECT zoom_level, Count(*) count FROM tiles group by zoom_level) " +
                "WHERE count > 10 order by zoom_level", null);
        if (c.getCount()>0) {
            c.moveToFirst();
            zoomlevel = c.getInt(0);
            c.close();
        }
        return zoomlevel;
    }

    private void selectAlltilesByZoomLevel(int zoomlevel)
    {
        tiles = new ArrayList<>();
        String[] projection = {"tile_data","tile_column","tile_row"};
        String predicate = "zoom_level = ?";
        String[] values = {String.valueOf(zoomlevel)};
        String orderBy = "tile_column, tile_row";

        Cursor c = this.mDatabase.query("tiles", projection, predicate, values, null, null, orderBy);
        if (c != null) {
            c.moveToFirst();
            while (!c.isAfterLast()) {
                tile _tile = new tile();
                _tile.tile = c.getBlob(0);
                _tile.tile_column = c.getInt(1);
                _tile.tile_row = c.getInt(2);
                tiles.add(_tile);
                c.moveToNext();
            }
            c.close();
        }
    }

    private Bitmap getBitmap()
    {
        if (openDatabase())
        {
            selectAlltilesByZoomLevel(getZoomLevel());
            if (tiles.size()>0) {
                tile firstTile = tiles.get(0);
                tile lastTile = tiles.get(tiles.size()-1);
                Integer width = ((lastTile.tile_column-firstTile.tile_column)+1) * 256;
                Integer height = ((lastTile.tile_row-firstTile.tile_row)+1) * 256;

                Bitmap tempbb = android.graphics.Bitmap.createBitmap(
                        width, height,
                        Bitmap.Config.ARGB_8888);
                Canvas cc = new Canvas(tempbb);
                for (tile t: tiles)
                {
                    Bitmap tilebb = BitmapFactory.decodeByteArray(t.tile, 0, t.tile.length);
                    float x = 255 * (t.tile_column - firstTile.tile_column);
                    float y = height - (255 * (t.tile_row - firstTile.tile_row)) - 255;
                    cc.drawBitmap(tilebb, x, y, null);
                }

                return tempbb;
            }
            return null;
        }
        return null;
    }

    private SQLiteDatabase mDatabase;
    private String mPathToFile;
    private ArrayList<tile> tiles;

    private String message;

    public Bitmap GetBitmap()
    {
        Bitmap b = getBitmap();
        mDatabase.close();
        return b;
    }

}
