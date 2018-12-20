package com.mobileaviationtools.nav_fly.Settings;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Fix;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Route.Info.InfoItemAdapter;
import com.mobileaviationtools.nav_fly.Search.SearchService;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;
import com.mobileaviationtools.nav_fly.Startup.StartupInfoFragment;

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

    public static HomeAirportFragment getInstance(DialogFragment parentDialog, GlobalVars vars, Boolean startup)
    {
        if (instance == null) {
            HomeAirportFragment instance = new HomeAirportFragment();
            instance.vars = vars;
            instance.parentDialog = parentDialog;
            instance.startup = startup;
            instance.service = new SearchService(instance.vars);
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
    private SearchService service;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home_airport, container, false);
        setup();
        return view;
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

        final ListView airportsListView = view.findViewById(R.id.searchHomeAirportsList);

        infoAirportItemAdapter = new InfoItemAdapter(vars.context);
        infoAirportItemAdapter.setAirports(service.getAirportsLimit(50l));

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
                InfoItemAdapter adapter = (InfoItemAdapter)parent.getAdapter();
                Object item = adapter.getItem(position);
                if (item instanceof Airport){
                    ArrayList<Airport> selectedAirportList = new ArrayList<>();
                    selectedAirportList.add((Airport)item);
                    infoAirportItemAdapter = new InfoItemAdapter(vars.context);
                    infoAirportItemAdapter.setAirports(selectedAirportList);
                    ListView selectedListView = getView().findViewById(R.id.selectedHomeAirportsList);
                    selectedListView.setAdapter(infoAirportItemAdapter);
                }
            }
        });
    }
}
