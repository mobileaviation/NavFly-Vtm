package com.mobileaviationtools.nav_fly.Search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.Classes.GeometryHelpers;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Info.InfoItemAdapter;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.oscim.core.GeoPoint;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchAirportFragment extends Fragment {
    public SearchAirportFragment() {
        // Required empty public constructor
    }

    private static SearchAirportFragment instance;

    public static SearchAirportFragment getInstance(GlobalVars vars)
    {
        if (instance == null) {
            SearchAirportFragment instance = new SearchAirportFragment();
            instance.vars = vars;
            return instance;
        }
        else
            return instance;
    }

    private GlobalVars vars;
    private View view;
    private InfoItemAdapter infoAirportItemAdapter;
    private InfoItemAdapter infoNavaidsItemAdapter;
    private InfoItemAdapter infoFixesItemAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_search_airport, container, false);

        ListView airportsListView = view.findViewById(R.id.searchAirportsList);
        ListView navaidsListView = view.findViewById(R.id.searchNavaidsList);
        ListView fixesListView = view.findViewById(R.id.searchfixesList);

        // Basic fill...
        infoAirportItemAdapter = new InfoItemAdapter(vars.context);
        infoAirportItemAdapter.setAirports(getAirportsByLocationLimit(vars.airplaneLocation.getGeopoint(), 50l));

        infoNavaidsItemAdapter = new InfoItemAdapter(vars.context);
        infoNavaidsItemAdapter.setNavaids(getNavaidsByLocationLimit(vars.airplaneLocation.getGeopoint(), 50l));

        infoFixesItemAdapter = new InfoItemAdapter(vars.context);
        infoFixesItemAdapter.setFix(getFixesByLocationLimit(vars.airplaneLocation.getGeopoint(), 50l));

        final EditText searchAirportsText = view.findViewById(R.id.searchAirportsText);
        searchAirportsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Airport> s = searchAirports(charSequence.toString(), 50l);
                infoAirportItemAdapter.setAirports(s);
                infoAirportItemAdapter.notifyDataSetChanged();

                List<Navaid> n = searchNavaids(charSequence.toString(), 50l);
                infoNavaidsItemAdapter.setNavaids(n);
                infoNavaidsItemAdapter.notifyDataSetChanged();

                List<Fix> f = searchFixes(charSequence.toString(), 50l);
                infoFixesItemAdapter.setFix(f);
                infoFixesItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        airportsListView.setAdapter(infoAirportItemAdapter);
        navaidsListView.setAdapter(infoNavaidsItemAdapter);
        fixesListView.setAdapter(infoFixesItemAdapter);

        return view;
    }

    private List<Airport> searchAirports(String searchString, Long limit)
    {
        String s = "%" + searchString + "%";

        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        List<Airport> airports = db.getAirport().searchAirportsByNameOrIdentLimit(s, limit);

        return airports;
    }

    private List<Airport> getAirportsByLocationLimit(GeoPoint location, Long limit)
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

    private Coordinate[] getCoordinates(GeoPoint location, Long nm)
    {
        Long m = nm * 1609l;
        Geometry c = GeometryHelpers.getCircle(location, m);
        Geometry b = c.getEnvelope();
        return b.getCoordinates();
    }

    private List<Navaid> searchNavaids(String searchString, Long limit)
    {
        String s = "%" + searchString + "%";

        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        List<Navaid> navaids = db.getNavaids().searchNaviadsByNameOrIdentLimit(s, limit);

        return navaids;
    }

    private List<Navaid> getNavaidsByLocationLimit(GeoPoint location, Long limit)
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

    private List<Fix> searchFixes(String searchString, Long limit)
    {
        String s = "%" + searchString + "%";

        AirnavDatabase db = AirnavDatabase.getInstance(vars.context);
        List<Fix> fixes = db.getFixes().searchFixesByNameOrIdentLimit(s, limit);

        return fixes;
    }

    private List<Fix> getFixesByLocationLimit(GeoPoint location, Long limit)
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
