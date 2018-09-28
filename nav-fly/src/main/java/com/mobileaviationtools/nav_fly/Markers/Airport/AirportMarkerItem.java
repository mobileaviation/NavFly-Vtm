package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;
import android.graphics.Bitmap;

import com.mobileaviationtools.airnavdata.Classes.AirportType;
import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.oscim.android.canvas.AndroidBitmap;
import org.oscim.core.BoundingBox;
import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.util.ResourceBundle;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class AirportMarkerItem extends MarkerItem{
    private Airport airport;
    private Context context;
    private Point point;

    public AirportMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public AirportMarkerItem(Airport airport, Context context) {
        this(airport.ident, airport.name, new GeoPoint(airport.latitude_deg, airport.longitude_deg));
        Coordinate coordinate = new Coordinate(airport.longitude_deg, airport.latitude_deg);
        point = new GeometryFactory().createPoint(coordinate);
        this.airport = airport;
        this.context = context;
    }

    public void InitMarker()
    {
        MarkerSymbol symbol = new MarkerSymbol(new AndroidBitmap(GetMarkerBitmap(airport)), MarkerSymbol.HotspotPlace.CENTER, false);
        this.setMarker(symbol);
    }

    public static Bitmap GetMarkerBitmap(Airport airport)
    {
        Bitmap icon = null;
        switch (airport.type){
            case small_airport: { icon = AirportSymbolSmall.DrawAirportIcon(airport);  break;}
            case medium_airport: { icon = AirportSymbolMedium.DrawAirportIcon(airport); break;}
            case large_airport: { icon = AirportSymbolLarge.DrawAirportIcon(airport); break;}
            case seaplane_base: { icon = AirportSymbolSeaBase.DrawAirportIcon(airport); break;}
            case heliport: { icon = AirportSymbolHeliport.DrawAirportIcon(airport); break;}
            case balloonport: { icon = AirportSymbolBaloonBase.DrawAirportIcon(airport); break;}
            case closed: { icon = AirportSymbolClosed.DrawAirportIcon(airport); break;}
        }
        return icon;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AirportMarkerItem) {
            return (((AirportMarkerItem)obj).airport.ident.equals(this.airport.ident));
        }
        else
        return false;
    }

    public boolean WithinBounds(BoundingBox box)
    {
        return box.contains(this.geoPoint);
    }

    public AirportType getAirportType()
    {
        return airport.type;
    }

    public Airport getAirport()
    {
        return airport;
    }
}
