package com.mobileaviationtools.nav_fly.Settings;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoationProviderSetupFragment extends Fragment {


    public LoationProviderSetupFragment() {
        // Required empty public constructor
    }

    private static LoationProviderSetupFragment instance;

    public static LoationProviderSetupFragment getInstance(DialogFragment parentDialog, GlobalVars vars, Boolean startup)
    {
        if (instance == null) {
            LoationProviderSetupFragment instance = new LoationProviderSetupFragment();
            instance.vars = vars;
            instance.parentDialog = parentDialog;
            instance.startup = startup;
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
        Button nextBtn = (Button) view.findViewById(R.id.locationSetupNextBtn);
        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextEvent != null) nextEvent.OnNext(LoationProviderSetupFragment.this);
            }
        });
    }

}
