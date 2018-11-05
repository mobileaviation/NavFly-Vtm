package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mobileaviationtools.nav_fly.R;

public class SettingsDialog extends DialogFragment {
    public SettingsDialog()
    {
        super();
    }

    public static SettingsDialog getInstance(Context context, SettingsObject object)
    {
        SettingsDialog settingsDialog = new SettingsDialog();
        settingsDialog.context = context;
        settingsDialog.settingsObject = object;
        return settingsDialog;
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

    public void setup(View view)
    {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vppager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add Fragments to adapter one by one
        adapter.addFragment(new SettingsFragmentBaseChart(), "Base Chart");
        adapter.addFragment(SettingsFragmentOffline.getInstance(context, settingsObject), "Offline");
        adapter.addFragment(new SettingsFragmentOverlays(), "Overlays");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
