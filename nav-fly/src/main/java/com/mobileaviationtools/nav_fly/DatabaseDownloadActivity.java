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
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Models.Statistics;

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

    private Integer finishedCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database_download);

        actionBtn = (Button)this.findViewById(R.id.downloadDatabasesBtn);
        actionBtn.setTag(false);

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
                if (!(Boolean)actionBtn.getTag()) {
                    AirnavClient airnavClient = new AirnavClient(DatabaseDownloadActivity.this);
                    actionBtn.setEnabled(false);
                    finishedCount = 0;

                    airnavClient.SetProgressStatus(new DataDownloadStatusEvent() {
                        @Override
                        public void onProgress(Integer count, Integer downloaded, TableType tableType) {
                            ProgressBar bar = null;
                            switch (tableType) {
                                case airports:
                                    bar = airportsProgressBar;
                                    break;
                                case airspaces:
                                    bar = airspacesProgressBar;
                                    break;
                                case firs:
                                    bar = firsProgressBar;
                                    break;
                                case fixes:
                                    bar = fixesProgressBar;
                                    break;
                                case navaids:
                                    bar = navaidsProgressBar;
                                    break;
                                case countries:
                                    bar = countriesProgressBar;
                                    break;
                                case regions:
                                    bar = regionsProgressBar;
                                    break;
                                case mbtiles:
                                    bar = chartsProgressBar;
                                    break;
                            }

                            if (bar != null) {
                                bar.setMax(count);
                                bar.setProgress(downloaded);
                            }
                        }

                        @Override
                        public void OnFinished(TableType tableType) {
                            finishedCount++;
                            if (finishedCount == 8) {
                                actionBtn.setText("Close");
                                actionBtn.setTag(true);
                                actionBtn.setEnabled(true);
                            }
                        }

                        @Override
                        public void OnError(String message, TableType tableType) {

                        }

                        @Override
                        public void OnStatistics(Statistics statistics) {
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.airportCountTxt)).setText(statistics.AirportsCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.airspacesCountTxt)).setText(statistics.AirspacesCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.regionsCountTxt)).setText(statistics.RegionsCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.countriesCountTxt)).setText(statistics.CountriesCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.firsCountTxt)).setText(statistics.FirsCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.fixesCountTxt)).setText(statistics.FixesCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.navaidsCountTxt)).setText(statistics.NavaidsCount.toString());
                            ((TextView) DatabaseDownloadActivity.this.findViewById(R.id.chartsCountTxt)).setText(statistics.MBTilesCount.toString());

                        }


                    });

                    airnavClient.StartDownload();
                }
                else
                {
                    DatabaseDownloadActivity.this.finish();
                }
            }
        });
    }


}
