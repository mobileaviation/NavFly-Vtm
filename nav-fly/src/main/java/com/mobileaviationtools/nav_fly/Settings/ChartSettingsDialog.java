package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

public class ChartSettingsDialog extends DialogFragment {
    public ChartSettingsDialog()
    {
        super();
    }

    public static ChartSettingsDialog getInstance(Context context, SettingsObject object, GlobalVars vars)
    {
        ChartSettingsDialog chartSettingsDialog = new ChartSettingsDialog();
        chartSettingsDialog.context = context;
        chartSettingsDialog.settingsObject = object;
        chartSettingsDialog.vars = vars;
        return chartSettingsDialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_base_layout, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialog);
        setup(view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        getDialog().getWindow().setBackgroundDrawable(inset);

        return view;
    }

    @Override public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
    }

    private SettingsObject settingsObject;
    private Context context;
    private GlobalVars vars;

    public void setup(View view)
    {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vppager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(SettingsFragmentBaseChart.getInstance(context, vars), "Base Chart");
        adapter.addFragment(SettingsFragmentAdditionalCharts.getInstance(context, settingsObject), "Extra Charts");
        adapter.addFragment(SettingsFragmentOffline.getInstance(context, settingsObject), "Offline");
        adapter.addFragment(SettingsFragmentOverlays.getInstance(context, settingsObject), "Overlays");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
