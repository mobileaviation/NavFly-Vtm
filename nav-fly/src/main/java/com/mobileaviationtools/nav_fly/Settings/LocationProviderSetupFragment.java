package com.mobileaviationtools.nav_fly.Settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.mobileaviationtools.nav_fly.Classes.IpAddressFilter;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.Location.LocationProviderType;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Settings.Services.LocationProviderService;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LocationProviderSetupFragment extends Fragment {


    public LocationProviderSetupFragment() {
        // Required empty public constructor
    }

    private static LocationProviderSetupFragment instance;

    public static LocationProviderSetupFragment getInstance(DialogFragment parentDialog, GlobalVars vars, Boolean startup)
    {
        if (instance == null) {
            LocationProviderSetupFragment instance = new LocationProviderSetupFragment();
            instance.vars = vars;
            instance.parentDialog = parentDialog;
            instance.startup = startup;
            instance.service = new LocationProviderService(instance.vars);
            return instance;
        }
        else
            return instance;
    }

    private LocationProviderService service;
    private GlobalVars vars;
    private DialogFragment parentDialog;
    private Boolean startup;
    private StartupDialog.NextPrevEventListener nextEvent;
    public void SetNextEventListener(StartupDialog.NextPrevEventListener listener)
    {
        nextEvent = listener;
    }

    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_loation_provider_setup, container, false);

        setup();

        return view;
    }

    private void setup()
    {
        final RadioGroup rg = view.findViewById(R.id.locationProviderBtns);
        final EditText ipEdit = LocationProviderSetupFragment.this.view.findViewById(R.id.simServerIpAddressEditText);
        ipEdit.setFilters(IpAddressFilter.getIpAddressFilter());
        final EditText portEdit = LocationProviderSetupFragment.this.view.findViewById(R.id.simServerTcpPortEditText);

        LocationProviderType type = service.getLocationProviderType();
        String[] network = service.getNetwerkInfo();

        if (type == LocationProviderType.simulator) rg.check(R.id.simLocationProviderBtn);
        if (type == LocationProviderType.gps) rg.check(R.id.gpsLocationProviderBtn);

        ipEdit.setText(network[0]);
        portEdit.setText(network[1]);

        Button nextBtn = (Button) view.findViewById(R.id.locationSetupNextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int checkedBtn = rg.getCheckedRadioButtonId();
                if (checkedBtn==R.id.gpsLocationProviderBtn)
                    service.storeProviderType(LocationProviderType.gps);
                if (checkedBtn==R.id.simLocationProviderBtn)
                    service.storeProviderType(LocationProviderType.simulator);

                service.storeNetworkConnectionInfo(ipEdit.getText().toString(), portEdit.getText().toString());

                if (nextEvent != null) nextEvent.OnClose(LocationProviderSetupFragment.this);
            }
        });
    }

}
