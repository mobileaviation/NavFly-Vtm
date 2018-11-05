package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mobileaviationtools.nav_fly.R;

import org.oscim.android.cache.OfflineTileDownloadEvent;
import org.oscim.core.BoundingBox;

public class SettingsFragmentOffline extends Fragment {
    public SettingsFragmentOffline() {
        // Required empty public constructor
    }

    private static SettingsFragmentOffline instance;

    public static SettingsFragmentOffline getInstance(Context context, SettingsObject settingsObject)
    {
        if (instance == null) {
            SettingsFragmentOffline instance = new SettingsFragmentOffline();
            instance.context = context;
            instance.settingsObject = settingsObject;
            return instance;
        }
        else
            return instance;
    }

    private Context context;
    private SettingsObject settingsObject;

    private ProgressBar baseTilesDownloadProgress;
    private Button startDownloadBaseTilesBtn;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.settings_fragment_offline, container, false);

        startDownloadBaseTilesBtn = (Button) view.findViewById(R.id.startDownloadBaseTilesBtn);
        baseTilesDownloadProgress = (ProgressBar) view.findViewById(R.id.baseTilesDownloadProgress);

        startDownloadBaseTilesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDownload();
            }
        });

        return view;
    }

    private void startDownload()
    {
        startDownloadBaseTilesBtn.setEnabled(false);

        settingsObject.DownloadTiles(new OfflineTileDownloadEvent() {
            @Override
            public void onProgress(long percent) {
                baseTilesDownloadProgress.setProgress((int)percent);
            }

            @Override
            public void onFinished() {
                startDownloadBaseTilesBtn.setEnabled(true);
            }
        });

    }
}
