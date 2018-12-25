package com.mobileaviationtools.nav_fly.Settings.HomeAirport;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Info.InfoItemAdapter;
import com.mobileaviationtools.nav_fly.Search.SearchService;
import com.mobileaviationtools.nav_fly.Settings.Services.HomeAirportService;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeAirportFragment extends Fragment {
    public HomeAirportFragment() {
        // Required empty public constructor
    }

    private static HomeAirportFragment instance;

    private final String TAG = "HomeAirportFragment";

    public static HomeAirportFragment getInstance(DialogFragment parentDialog, GlobalVars vars, Boolean startup)
    {
        if (instance == null) {
            HomeAirportFragment instance = new HomeAirportFragment();
            instance.vars = vars;
            instance.parentDialog = parentDialog;
            instance.startup = startup;
            instance.service = new SearchService(instance.vars);
            instance.homeAirportService = new HomeAirportService(instance.vars);
            return instance;
        }
        else
            return instance;
    }

    private GlobalVars vars;
    private DialogFragment parentDialog;
    private Boolean startup;
    private StartupDialog.NextPrevEventListener nextEvent;
    public void SetNextEventListener(StartupDialog.NextPrevEventListener listener)
    {
        nextEvent = listener;
    }

    private View view;
    private InfoItemAdapter infoAirportItemAdapter;
    private InfoItemAdapter selectedAirportItemAdapter;
    private SearchService service;
    private HomeAirportService homeAirportService;

    private SelectedAirport selectedAirport;

    private ListView airportsListView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_airport, container, false);
        setup();
        return view;
    }

    public void setBaseAirports()
    {
        infoAirportItemAdapter.setAirports(service.getAirportsLimit(50l));
        infoAirportItemAdapter.notifyDataSetChanged();
    }

    private void setup()
    {
        Button nextBtn = (Button) view.findViewById(R.id.homeAirportNextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextEvent != null) nextEvent.OnNext(HomeAirportFragment.this);
            }
        });

        selectedAirport = homeAirportService.getSelectedHomeAirport();
        if (selectedAirport != null) Log.i(TAG,"Retrieved Selected Home Airport: " + selectedAirport.airport.name);
        else Log.i(TAG,"No Selected home airport found!");
        airportsListView = view.findViewById(R.id.searchHomeAirportsList);

        infoAirportItemAdapter = new InfoItemAdapter(vars.context);
        List<Airport> airports =  service.getAirportsLimit(50l);
        if (selectedAirport != null) airports.add(0, selectedAirport.airport);

        infoAirportItemAdapter.setAirports(airports);

        final EditText searchAirportsText = view.findViewById(R.id.homeAirportSearchText);
        searchAirportsText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                List<Airport> s = service.searchAirports(charSequence.toString(), 50l);
                infoAirportItemAdapter.setAirports(s);
                infoAirportItemAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        airportsListView.setAdapter(infoAirportItemAdapter);

        airportsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final InfoItemAdapter adapter = (InfoItemAdapter)parent.getAdapter();
                Object item = adapter.getItem(position);
                if (item instanceof Airport){
                    ArrayList<Airport> selectedAirportList = new ArrayList<>();
                    selectedAirportList.add((Airport)item);
                    selectedAirportItemAdapter = new InfoItemAdapter(vars.context);
                    selectedAirportItemAdapter.setAirports(selectedAirportList);
                    ListView selectedListView = HomeAirportFragment.this.view.findViewById(R.id.selectedHomeAirportsList);
                    selectedListView.setAdapter(selectedAirportItemAdapter);
                    // store Selected Airport in DB
                    selectedAirport = new SelectedAirport();
                    selectedAirport.airport = (Airport)item;
                    homeAirportService.storeAirport(selectedAirport);

                    RunwaysAdapter runwaysAdapter = new RunwaysAdapter(((Airport)item).runways);
                    ListView airportRunwaysListView = HomeAirportFragment.this.view.findViewById(R.id.selectedHomeAirportRunwaysList);
                    airportRunwaysListView.setAdapter(runwaysAdapter);
                    airportRunwaysListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            // Store selected Runway in DB
                            RunwaysAdapter runwaysAdapter = (RunwaysAdapter)parent.getAdapter();
                            Object item = runwaysAdapter.getItem(position);
                            if (item instanceof RunwayItem)
                            {
                                selectedAirport.runway = ((RunwayItem) item).runway;
                                selectedAirport.runwayIdent = (((RunwayItem) item).leHe)?
                                        ((RunwayItem) item).runway.he_ident :
                                        ((RunwayItem) item).runway.le_ident;
                                homeAirportService.storeAirport(selectedAirport);
                            }
                        }
                    });
                }
            }
        });

        if (selectedAirport != null) {
            airportsListView.requestFocusFromTouch();
            airportsListView.setSelection(0);
            Log.i(TAG,"Home Airport selected");
            airportsListView.performItemClick(airportsListView.getAdapter().getView(0, null, null),
                    0, airportsListView.getAdapter().getItemId(0));

        }
    }
}
