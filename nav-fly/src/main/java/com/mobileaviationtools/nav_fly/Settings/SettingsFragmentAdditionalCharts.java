package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobileaviationtools.nav_fly.R;

public class SettingsFragmentAdditionalCharts extends Fragment {
    public SettingsFragmentAdditionalCharts() {
        // Required empty public constructor
    }

    private static SettingsFragmentAdditionalCharts instance;

    public static SettingsFragmentAdditionalCharts getInstance(Context context, SettingsObject settingsObject)
    {
        if (instance == null) {
            SettingsFragmentAdditionalCharts instance = new SettingsFragmentAdditionalCharts();
            instance.context = context;
            instance.settingsObject = settingsObject;
            return instance;
        }
        else
            return instance;
    }

    private Context context;
    private SettingsObject settingsObject;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.settings_fragment_basechart, container, false);
    }
}
