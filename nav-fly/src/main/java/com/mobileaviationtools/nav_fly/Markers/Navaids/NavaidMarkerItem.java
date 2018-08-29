package com.mobileaviationtools.nav_fly.Markers.Navaids;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Classes.NavaidType;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

public class NavaidMarkerItem extends MarkerItem {
    private Navaid navaid;
    private Context context;
    private Point point;


    public NavaidMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public NavaidMarkerItem(Navaid navaid, Context context) {
        this(navaid.ident, navaid.name, new GeoPoint(navaid.latitude_deg, navaid.longitude_deg));
        Coordinate coordinate = new Coordinate(navaid.longitude_deg, navaid.latitude_deg);
        point = new GeometryFactory().createPoint(coordinate);
        this.navaid = navaid;
        this.context = context;
    }

    public void InitMarker() {
        MarkerSymbol symbol = null;

        switch (navaid.type)
        {
            case DME: symbol = NavaidSymbolDME.GetDMESymbol(navaid, context); break;
            case NDB: symbol = NavaidSymbolNDB.GetNDBSymbol(navaid, context); break;
            case VOR: symbol = NavaidSymbolVOR.GetVORSymbol(navaid, context); break;
            case TACAN: symbol = NavaidSymbolTACAN.GetTACANSymbol(navaid, context); break;
            case VORTAC: symbol = NavaidSymbolVORTAC.GetVORTACSymbol(navaid, context); break;
            case NDB_DME: symbol = NavaidSymbolNDBDME.GetNDBDMESymbol(navaid, context); break;
            case VOR_DME: symbol = NavaidSymbolVORDME.GetVORDMESymbol(navaid, context); break;
        }

        this.setMarker(symbol);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NavaidMarkerItem) {
            return (((NavaidMarkerItem)obj).navaid.ident.equals(this.navaid.ident));
        }
        else
            return false;
    }

    public boolean WithinBounds(BoundingBox box)
    {
        return box.contains(this.geoPoint);
    }

    public NavaidType GetNavaidType()
    {
        return navaid.type;
    }

    public Navaid getHavaid()
    {
        return navaid;
    }
}