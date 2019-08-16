package com.mobileaviationtools.nav_fly.Settings;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mobileaviationtools.nav_fly.R;

import com.mobileaviationtools.extras.Cache.OfflineTileDownloadEvent;
import org.oscim.backend.canvas.Color;
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
    private TextView dowloadInfoTextView;

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
        dowloadInfoTextView = (TextView) view.findViewById(R.id.dowloadInfoTextView);

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
            public void onProgress(long percent, String message) {
                dowloadInfoTextView.setText(message);
                dowloadInfoTextView.setTextColor(Color.GRAY);
                baseTilesDownloadProgress.setProgress((int)percent);
            }

            @Override
            public void onFinished() {
                startDownloadBaseTilesBtn.setEnabled(true);
            }

            @Override
            public void onError(String message) {
                dowloadInfoTextView.setText(message);
                dowloadInfoTextView.setTextColor(Color.RED);
                //startDownloadBaseTilesBtn.setEnabled(true);
            }
        });

    }
}
