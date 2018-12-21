package com.mobileaviationtools.nav_fly.Startup;

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
import com.mobileaviationtools.nav_fly.Settings.Database.DatabaseDownloadFragment;
import com.mobileaviationtools.nav_fly.Settings.HomeAirport.HomeAirportFragment;
import com.mobileaviationtools.nav_fly.Settings.LoationProviderSetupFragment;
import com.mobileaviationtools.nav_fly.Settings.ViewPagerAdapter;

public class StartupDialog extends DialogFragment {
    public interface NextPrevEventListener
    {
        public void OnNext(Fragment fragment);
    }

    public StartupDialog()
    {
        super();
    }

    private GlobalVars vars;
    private View view;
    private HomeAirportFragment homeAirportFragment;
    private NextPrevEventListener nextPrevEventListener;

    public static StartupDialog getInstance(GlobalVars vars)
    {
        StartupDialog dialog = new StartupDialog();
        dialog.vars = vars;
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.startup_dialog, container);

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

    public void setup(View view)
    {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.startuppager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add Fragments to adapter one by one
        StartupInfoFragment startupInfoFragment = StartupInfoFragment.getInstance(this, vars);
        startupInfoFragment.SetNextEventListener(nextPrevEventListener);
        adapter.addFragment(startupInfoFragment, "Information");

        DatabaseDownloadFragment databaseDownloadFragment = DatabaseDownloadFragment.getInstance(this, vars, true);
        databaseDownloadFragment.SetNextEventListener(nextPrevEventListener);
        adapter.addFragment(databaseDownloadFragment, "Download Databases");

        homeAirportFragment = HomeAirportFragment.getInstance(this, vars, true);
        homeAirportFragment.SetNextEventListener(nextPrevEventListener);
        adapter.addFragment(homeAirportFragment, "Home Airport");

        LoationProviderSetupFragment loationProviderSetupFragment = LoationProviderSetupFragment.getInstance(this, vars, true);
        loationProviderSetupFragment.SetNextEventListener(nextPrevEventListener);
        adapter.addFragment(loationProviderSetupFragment, "Location Provider");



        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.startuptabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void NextPage()
    {
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.startuppager);
        int currpos = viewPager.getCurrentItem();
        viewPager.setCurrentItem(currpos + 1);
    }

    public void setupNextPrevListener()
    {
        nextPrevEventListener = new NextPrevEventListener() {
            @Override
            public void OnNext(Fragment fragment) {
                if (fragment instanceof DatabaseDownloadFragment)
                {
                    homeAirportFragment.setBaseAirports();
                }
                NextPage();
            }
        };
    }
}
