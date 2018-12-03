package com.mobileaviationtools.nav_fly.Search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

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

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchAirnavItemsFragment extends Fragment {
    public SearchAirnavItemsFragment() {
        // Required empty public constructor
    }

    private static SearchAirnavItemsFragment instance;

    public static SearchAirnavItemsFragment getInstance(GlobalVars vars)
    {
        if (instance == null) {
            SearchAirnavItemsFragment instance = new SearchAirnavItemsFragment();
            instance.vars = vars;
            instance.service = new SearchService(instance.vars);
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
    private SearchService service;

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
        infoAirportItemAdapter.setAirports(service.getAirportsByLocationLimit(vars.airplaneLocation.getGeopoint(), 50l));

        infoNavaidsItemAdapter = new InfoItemAdapter(vars.context);
        infoNavaidsItemAdapter.setNavaids(service.getNavaidsByLocationLimit(vars.airplaneLocation.getGeopoint(), 50l));

        infoFixesItemAdapter = new InfoItemAdapter(vars.context);
        infoFixesItemAdapter.setFix(service.getFixesByLocationLimit(vars.airplaneLocation.getGeopoint(), 50l));

        final EditText searchAirportsText = view.findViewById(R.id.searchAirportsText);
        searchAirportsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Airport> s = service.searchAirports(charSequence.toString(), 50l);
                infoAirportItemAdapter.setAirports(s);
                infoAirportItemAdapter.notifyDataSetChanged();

                List<Navaid> n = service.searchNavaids(charSequence.toString(), 50l);
                infoNavaidsItemAdapter.setNavaids(n);
                infoNavaidsItemAdapter.notifyDataSetChanged();

                List<Fix> f = service.searchFixes(charSequence.toString(), 50l);
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


}
