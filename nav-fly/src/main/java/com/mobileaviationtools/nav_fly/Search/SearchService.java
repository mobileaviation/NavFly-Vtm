package com.mobileaviationtools.nav_fly.Search;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.GlobalVars;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.oscim.core.GeoPoint;

import java.util.List;

public class SearchService {
    public SearchService(GlobalVars vars)
    {
        this.vars = vars;
    }
    private GlobalVars vars;

    private Coordinate[] getCoordinates(GeoPoint location, Long nm)
    {
        Long m = nm * 1609l;
        Geometry c = GeometryHelpers.getCircle(location, m);
        Geometry b = c.getEnvelope();
        return b.getCoordinates();
    }

    public List<Airport> searchAirports(String searchString, Long limit)
    {
        String s = "%" + searchString + "%";

        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        List<Airport> airports = db.getAirport().searchAirportsByNameOrIdentLimit(s, limit);

        return airports;
    }

    public List<Airport> getAirportsByLocationLimit(GeoPoint location, Long limit)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        Coordinate[] coordinates = getCoordinates(location, 200l);

        List<Airport> airports = null;
        if (coordinates.length>3) {
            airports =
                    db.getAirport().getAirportListWithinBoundsLimit(coordinates[0].x,
                            coordinates[2].x,
                            coordinates[2].y,
                            coordinates[0].y, limit);
        }
        return airports;
    }

    public List<Navaid> searchNavaids(String searchString, Long limit)
    {
        String s = "%" + searchString + "%";

        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        List<Navaid> navaids = db.getNavaids().searchNaviadsByNameOrIdentLimit(s, limit);

        return navaids;
    }

    public List<Navaid> getNavaidsByLocationLimit(GeoPoint location, Long limit)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        Coordinate[] coordinates = getCoordinates(location, 200l);

        List<Navaid> navaids = null;
        if (coordinates.length>3) {
            navaids =
                    db.getNavaids().getNavaidsListWithinBoundsLimit(coordinates[0].x,
                            coordinates[2].x,
                            coordinates[2].y,
                            coordinates[0].y, limit);
        }
        return navaids;
    }

    public List<Fix> searchFixes(String searchString, Long limit)
    {
        String s = "%" + searchString + "%";

        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        List<Fix> fixes = db.getFixes().searchFixesByNameOrIdentLimit(s, limit);

        return fixes;
    }

    public List<Fix> getFixesByLocationLimit(GeoPoint location, Long limit)
    {
        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        Coordinate[] coordinates = getCoordinates(location, 200l);

        List<Fix> fixes = null;
        if (coordinates.length>3) {
            fixes =
                    db.getFixes().getFixesListWithinBoundsLimit(coordinates[0].x,
                            coordinates[2].x,
                            coordinates[2].y,
                            coordinates[0].y, limit);
        }
        return fixes;
    }
}
