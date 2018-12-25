package com.mobileaviationtools.nav_fly.Settings;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Settings.HomeAirport.HomeAirportFragment;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;

public class GeneralSettingsDialog extends DialogFragment {
    public GeneralSettingsDialog()
    {
        super();
    }

    private GlobalVars vars;
    private View view;
    private HomeAirportFragment homeAirportFragment;
    private StartupDialog.NextPrevEventListener nextPrevEventListener;

    public static GeneralSettingsDialog getInstance(GlobalVars vars)
    {
        GeneralSettingsDialog dialog = new GeneralSettingsDialog();
        dialog.vars = vars;
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.settings_base_layout, container);

        setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialog);
        setupNextPrevListener();
        setup(view);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ColorDrawable back = new ColorDrawable(Color.TRANSPARENT);
        InsetDrawable inset = new InsetDrawable(back, 50);
        getDialog().getWindow().setBackgroundDrawable(inset);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                        WindowManager.LayoutParams.MATCH_PARENT);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        Activity activity = this.getActivity();
        ((DialogInterface.OnDismissListener)activity).onDismiss(dialog);
    }

    private void setup(View view)
    {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vppager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        homeAirportFragment = HomeAirportFragment.getInstance(this, vars, true);
        homeAirportFragment.SetNextEventListener(nextPrevEventListener);
        adapter.addFragment(homeAirportFragment, "Home Airport");

        LocationProviderSetupFragment loationProviderSetupFragment = LocationProviderSetupFragment.getInstance(this, vars, true);
        loationProviderSetupFragment.SetNextEventListener(nextPrevEventListener);
        adapter.addFragment(loationProviderSetupFragment, "Location Provider");

        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void NextPage()
    {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.vppager);
        int currpos = viewPager.getCurrentItem();
        viewPager.setCurrentItem(currpos + 1);
    }

    public void setupNextPrevListener()
    {
        nextPrevEventListener = new StartupDialog.NextPrevEventListener() {
            @Override
            public void OnNext(Fragment fragment) {
                NextPage();
            }
            @Override
            public void OnPrev(Fragment fragment)
            {

            }
            @Override
            public void OnClose(Fragment fragment)
            {
                dismiss();
            }
        };
    }
}
