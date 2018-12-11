package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileaviationtools.airnavdata.Classes.OnlineTileProviders;
import com.mobileaviationtools.nav_fly.R;

public class SettingsFragmentOverlays extends Fragment {
    public SettingsFragmentOverlays() {
        // Required empty public constructor
    }

    private static SettingsFragmentOverlays instance;

    public static SettingsFragmentOverlays getInstance(Context context, SettingsObject settingsObject)
    {
        if (instance == null) {
            SettingsFragmentOverlays instance = new SettingsFragmentOverlays();
            instance.context = context;
            instance.settingsObject = settingsObject;
            return instance;
        }
        else
            return instance;
    }

    private Context context;
    private SettingsObject settingsObject;
    private View view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.settings_fragment_overlays, container, false);

        for (OnlineTileProviders provider : OnlineTileProviders.values())
        {

        }

        return view;
    }
}
