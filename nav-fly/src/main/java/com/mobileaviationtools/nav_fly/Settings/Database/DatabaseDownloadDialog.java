package com.mobileaviationtools.nav_fly.Settings.Database;

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
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Settings.ViewPagerAdapter;


public class DatabaseDownloadDialog extends DialogFragment {
    public DatabaseDownloadDialog()
    {
        super();
    }

    private GlobalVars vars;
    private View view;

    public static DatabaseDownloadDialog getInstance(GlobalVars vars)
    {
        DatabaseDownloadDialog dialog = new DatabaseDownloadDialog();
        dialog.vars = vars;
        return dialog;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_database_download, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialog);
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
        ViewPager viewPager = (ViewPager) view.findViewById(R.id.dbDownloadpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());

        // Add Fragments to adapter one by one
//        adapter.addFragment(SettingsFragmentBaseChart.getInstance(context, settingsObject), "Base Chart");
//        adapter.addFragment(SettingsFragmentAdditionalCharts.getInstance(context, settingsObject), "Extra Charts");
//        adapter.addFragment(SettingsFragmentOffline.getInstance(context, settingsObject), "Offline");
//        adapter.addFragment(SettingsFragmentOverlays.getInstance(context, settingsObject), "Overlays");
        adapter.addFragment(DatabaseDownloadFragment.getInstance(this, vars, false), "Download Databases");
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.dbDownloadtabs);
        tabLayout.setupWithViewPager(viewPager);
    }

}
