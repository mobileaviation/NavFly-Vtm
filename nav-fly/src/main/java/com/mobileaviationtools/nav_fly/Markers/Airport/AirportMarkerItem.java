package com.mobileaviationtools.nav_fly.Markers.Airport;

import android.content.Context;

import com.mobileaviationtools.airnavdata.Entities.Airport;

import org.oscim.core.GeoPoint;
import org.oscim.layers.marker.MarkerItem;
import org.oscim.layers.marker.MarkerSymbol;

import java.util.ResourceBundle;

import static org.oscim.android.canvas.AndroidGraphics.drawableToBitmap;

public class AirportMarkerItem extends MarkerItem{
    private Airport airport;
    private Context context;

    public AirportMarkerItem(String title, String description, GeoPoint geoPoint) {
        super(title, description, geoPoint);
    }

    public AirportMarkerItem(Airport airport, Context context) {
        this(airport.ident, airport.name, new GeoPoint(airport.latitude_deg, airport.longitude_deg));
        this.airport = airport;
        this.context = context;

        //Bitmap bitmapPoi = null;//drawableToBitmap(context.getResources().getDrawable(R.drawable.marker_poi));
        MarkerSymbol symbol = null;// new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.CENTER, false);
        switch (airport.type){
            case small_airport: { symbol = AirportSymbolSmall.GetAirportSymbol(airport, this.context);  break;}
            case medium_airport: { symbol = AirportSymbolMedium.GetAirportSymbol(airport, this.context); break;}
            case large_airport: { symbol = AirportSymbolLarge.GetAirportSymbol(airport, this.context); break;}
            case seaplane_base: { symbol = AirportSymbolSeaBase.GetAirportSymbol(airport, this.context); break;}
            case heliport: { symbol = AirportSymbolHeliport.GetAirportSymbol(airport, this.context); break;}
            case balloonport: { symbol = AirportSymbolBaloonBase.GetAirportSymbol(airport, this.context); break;}
            case closed: { symbol = AirportSymbolClosed.GetAirportSymbol(airport, this.context); break;}
        }

        //MarkerSymbol symbol = new MarkerSymbol(bitmapPoi, MarkerSymbol.HotspotPlace.CENTER, false);
        this.setMarker(symbol);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AirportMarkerItem) {
            return (((AirportMarkerItem)obj).airport.ident.equals(this.airport.ident));
        }
        else
        return false;
    }
}
