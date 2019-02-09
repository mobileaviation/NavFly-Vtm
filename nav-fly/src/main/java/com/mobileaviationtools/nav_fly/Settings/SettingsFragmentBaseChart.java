package com.mobileaviationtools.nav_fly.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import com.mobileaviationtools.nav_fly.Classes.BaseChartType;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import static android.content.Context.MODE_PRIVATE;

public class SettingsFragmentBaseChart extends Fragment {


    public SettingsFragmentBaseChart() {
        // Required empty public constructor
    }

    private static SettingsFragmentBaseChart instance;

    public static SettingsFragmentBaseChart getInstance(Context context, GlobalVars vars)
    {
        if (instance == null) {
            SettingsFragmentBaseChart instance = new SettingsFragmentBaseChart();
            instance.context = context;
            instance.vars = vars;
            return instance;
        }
        else
            return instance;
    }

    private Context context;
    private GlobalVars vars;
    private View view;
    private Button clearCacheBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.settings_fragment_basechart, container, false);

        RadioGroup basemapRadioGroup = view.findViewById(R.id.basemapRadioGroup);
        CheckBox cacheMapCheckBox = view.findViewById(R.id.cacheMapCheckBox);
        clearCacheBtn = view.findViewById(R.id.clearCacheBtn);

        int r = 0;
        switch (getSelectedBaseChart())
        {
            case openmaptiles:
            {
                r = R.id.openMapTilesBtn;
                break;
            }
            case nextzen:
            {
                r = R.id.nextZenMapsBtn;
                break;
            }
            case opensciencemaps:
            {
                r = R.id.openScienceMapsBtn;
                break;
            }
        }

        basemapRadioGroup.check(r);
        cacheMapCheckBox.setChecked(getCache());
        setupClearCachBtn();

        basemapRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                BaseChartType type = BaseChartType.opensciencemaps;
                if (checkedId == R.id.openMapTilesBtn) type = BaseChartType.openmaptiles;
                if (checkedId == R.id.nextZenMapsBtn) type = BaseChartType.nextzen;
                setSelectedBaseChart(type);
            }
        });

        cacheMapCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                setCache(isChecked);
            }
        });

        return view;
    }

    private void setupClearCachBtn()
    {
        clearCacheBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder clearCacheDialog = new AlertDialog.Builder(getContext());
                clearCacheDialog.setPositiveButton("Clear", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        vars.settingsObject.ClearBaseCache();
                    }
                });
                clearCacheDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                clearCacheDialog.setTitle("Clear cache");
                clearCacheDialog.setMessage("Are you sure you want to delete all the maps from the local cache?");
                clearCacheDialog.show();
            }
        });
    }

    private BaseChartType getSelectedBaseChart()
    {
        SharedPreferences databasePrefs = getSharedPreferences();
        String baseChartTypeStr = databasePrefs.getString("BaseChartType", "unk");
        return BaseChartType.valueOf(baseChartTypeStr);
    }

    private void setSelectedBaseChart(BaseChartType baseChart)
    {
        SharedPreferences databasePrefs = getSharedPreferences();
        databasePrefs.edit().putString("BaseChartType",baseChart.toString()).apply();
        vars.settingsObject.setBaseCacheEnabled(getCache());
        //vars.baseChart.removeBaseLayers();
        //vars.baseChart.setBaseCharts(baseChart);
    }

    private Boolean getCache()
    {
        SharedPreferences databasePrefs = getSharedPreferences();
        return databasePrefs.getBoolean("BaseChartCache", true);
    }

    private void setCache(Boolean cache)
    {
        SharedPreferences databasePrefs = getSharedPreferences();
        databasePrefs.edit().putBoolean("BaseChartCache", cache).apply();
    }

    private SharedPreferences getSharedPreferences()
    {
        return this.getActivity().getSharedPreferences("Database", MODE_PRIVATE);
    }

}
