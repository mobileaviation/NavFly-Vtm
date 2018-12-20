package com.mobileaviationtools.nav_fly.Startup;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartupInfoFragment extends Fragment {


    public StartupInfoFragment() {
        // Required empty public constructor
    }

    private static StartupInfoFragment instance;
    private DialogFragment parentDialog;
    private View view;
    private StartupDialog.NextPrevEventListener nextEvent;
    public void SetNextEventListener(StartupDialog.NextPrevEventListener listener)
    {
        nextEvent = listener;
    }

    public static StartupInfoFragment getInstance(DialogFragment parentDialog, GlobalVars vars)
    {
        if (instance == null) {
            StartupInfoFragment instance = new StartupInfoFragment();
            instance.vars = vars;
            instance.parentDialog = parentDialog;
            return instance;
        }
        else
            return instance;
    }

    private GlobalVars vars;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_startup_info, container, false);
        setup(view);
        return view;
    }

    private void setup(View view)
    {
        Button startupInfoNextBtn = (Button) view.findViewById(R.id.startupInfoNextBtn);
        startupInfoNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nextEvent != null) nextEvent.OnNext(StartupInfoFragment.this);
            }
        });
    }

}
