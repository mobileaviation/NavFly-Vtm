package com.mobileaviationtools.nav_fly;

import android.drm.DrmStore;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;

public class DatabaseDownloadActivity extends AppCompatActivity {

    private Button actionBtn;
    private ProgressBar airportsProgressBar;
    private ProgressBar navaidsProgressBar;
    private ProgressBar countriesProgressBar;
    private ProgressBar regionsProgressBar;
    private ProgressBar firsProgressBar;
    private ProgressBar fixesProgressBar;
    private ProgressBar airspacesProgressBar;
    private ProgressBar chartsProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_download);

        actionBtn = (Button)this.findViewById(R.id.downloadDatabasesBtn);

        airportsProgressBar = (ProgressBar)this.findViewById(R.id.airportsProgress);
        navaidsProgressBar = (ProgressBar)this.findViewById(R.id.navaidsProgress);
        countriesProgressBar = (ProgressBar) this.findViewById(R.id.countriesProgress);
        regionsProgressBar = (ProgressBar) this.findViewById(R.id.regionsProgress);
        fixesProgressBar = (ProgressBar)this.findViewById(R.id.fixesProgress);
        firsProgressBar = (ProgressBar)this.findViewById(R.id.firsProgress);
        airspacesProgressBar = (ProgressBar)this.findViewById(R.id.airspacesProgress);
        chartsProgressBar = (ProgressBar)this.findViewById(R.id.chartsProgress);

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AirnavClient airnavClient = new AirnavClient(DatabaseDownloadActivity.this);
                airnavClient.SetProgressStatus(new DataDownloadStatusEvent() {
                    @Override
                    public void onProgress(Integer count, Integer downloaded, TableType tableType) {
                        ProgressBar bar = null;
                        switch (tableType)
                        {
                            case airports : bar = airportsProgressBar; break;

                        }
                    }

                    @Override
                    public void OnFinished(TableType tableType) {

                    }

                    @Override
                    public void OnError(String message, TableType tableType) {

                    }
                });

                airnavClient.StartDownload();
            }
        });
    }


}
