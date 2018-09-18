package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.mobileaviationtools.airnavdata.Classes.AirspaceCategory;
import com.mobileaviationtools.airnavdata.Classes.AirspaceCategoryConverter;
import com.mobileaviationtools.airnavdata.Classes.AltitudeReference;
import com.mobileaviationtools.airnavdata.Classes.AltitudeReferenceConverter;
import com.mobileaviationtools.airnavdata.Classes.AltitudeUnit;
import com.mobileaviationtools.airnavdata.Classes.AltitudeUnitConverter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.io.WKTReader;
import org.oscim.core.GeoPoint;
import org.oscim.layers.vector.geometries.LineDrawable;
import org.oscim.layers.vector.geometries.PolygonDrawable;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "tbl_Airspaces",
        indices = {@Index(name = "airspace_location_index", value = {"lat_top_left","lon_top_left","lat_bottom_right", "lot_bottom_right"})})
public class Airspace {
    @PrimaryKey
    public long id;
    public String name;
    public String version;
    @TypeConverters({AirspaceCategoryConverter.class})
    public AirspaceCategory category;
    public long airspace_id;
    public String country;
    public long altLimit_top;
    @TypeConverters({AltitudeUnitConverter.class})
    public AltitudeUnit altLimit_top_unit;
    @TypeConverters({AltitudeReferenceConverter.class})
    public AltitudeReference altLimit_top_ref;
    public long altLimit_bottom;
    @TypeConverters({AltitudeUnitConverter.class})
    public AltitudeUnit altLimit_bottom_unit;
    @TypeConverters({AltitudeReferenceConverter.class})
    public AltitudeReference altLimit_bottom_ref;

    public double lat_top_left;
    public double lon_top_left;
    public double lat_bottom_right;
    public double lot_bottom_right;

    public String geometry;

    @Ignore
    public PolygonDrawable airspacePolygon1;
    @Ignore
    public LineDrawable airspacePolygon2;

    @Ignore
    public void processGeometry() {
        WKTReader reader = new WKTReader();
        try {
            Geometry airspaceGeometry = reader.read(geometry);
            Coordinate[] env = airspaceGeometry.getEnvelope().getCoordinates();
            lat_top_left = env[1].y;
            lon_top_left = env[1].x;
            lat_bottom_right = env[3].y;
            lot_bottom_right = env[3].x;
        }
        catch (Exception ee)
        {

        }
    }

    @Ignore
    public Geometry getAirspaceGeometry()
    {
        WKTReader reader = new WKTReader();
        try {
            return reader.read(geometry);
        }
        catch (Exception ee)
        {
            return null;
        }
    }


    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Airspace) {
            return (((Airspace)obj).id == this.id);
        }
        else
            return false;
    }
}
