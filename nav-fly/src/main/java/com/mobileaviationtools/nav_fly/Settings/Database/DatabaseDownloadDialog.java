package com.mobileaviationtools.nav_fly.Settings.Database;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobileaviationtools.airnavdata.AirnavUserSettingsDatabase;
import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Database;
import com.mobileaviationtools.airnavdata.Models.Statistics;
import com.mobileaviationtools.nav_fly.DatabaseDownloadActivity;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;

import java.util.Date;

public class DatabaseDownloadDialog extends DialogFragment {
    public DatabaseDownloadDialog()
    {
        super();
    }

    private GlobalVars vars;

    private Button actionBtn;
    private ProgressBar airportsProgressBar;
    private ProgressBar navaidsProgressBar;
    private ProgressBar countriesProgressBar;
    private ProgressBar regionsProgressBar;
    private ProgressBar firsProgressBar;
    private ProgressBar fixesProgressBar;
    private ProgressBar airspacesProgressBar;
    private ProgressBar chartsProgressBar;
    private ProgressBar citiesProgressBar;
    private RadioGroup continentsGroup;

    private Integer finishedCount;
    private String continent;
    private String TAG = "DatabaseDownloadActivity";
    private Integer version;
    private View view;

    public static DatabaseDownloadDialog getInstance(GlobalVars vars)
    {
        DatabaseDownloadDialog dialog = new DatabaseDownloadDialog();
        dialog.vars = vars;
        dialog.fromMenu = false;
        return dialog;
    }

    public Boolean fromMenu;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_database_download, container);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.SettingsDialog);
        setup();
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

    private void setup()
    {
        continent = "NA";

        continentsGroup = (RadioGroup) view.findViewById(R.id.continent_radiogroup);

        actionBtn = (Button)view.findViewById(R.id.downloadDatabasesBtn);
        actionBtn.setTag(false);

        airportsProgressBar = (ProgressBar)view.findViewById(R.id.airportsProgress);
        navaidsProgressBar = (ProgressBar)view.findViewById(R.id.navaidsProgress);
        countriesProgressBar = (ProgressBar) view.findViewById(R.id.countriesProgress);
        regionsProgressBar = (ProgressBar) view.findViewById(R.id.regionsProgress);
        fixesProgressBar = (ProgressBar)view.findViewById(R.id.fixesProgress);
        firsProgressBar = (ProgressBar)view.findViewById(R.id.firsProgress);
        airspacesProgressBar = (ProgressBar)view.findViewById(R.id.airspacesProgress);
        chartsProgressBar = (ProgressBar)view.findViewById(R.id.chartsProgress);
        citiesProgressBar = (ProgressBar)view.findViewById(R.id.citiesProgress);

        continentsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                int id = group.getCheckedRadioButtonId();
                RadioButton r = (RadioButton) group.findViewById(id);
                continent = r.getTag().toString();
                Log.i(TAG, "Selected continent: " + continent);
            }
        });

        actionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                if (!(Boolean)actionBtn.getTag()) {
                    AirnavClient airnavClient = new AirnavClient(vars.context);
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
                                case cities:
                                    bar = citiesProgressBar;
                                    break;
                            }

                            if (bar != null) {
                                bar.setMax(count);
                                bar.setProgress(downloaded);
                            }
                        }

                        @Override
                        public void OnFinished(TableType tableType, String continent) {
                            finishedCount++;

                            AirnavUserSettingsDatabase db = AirnavUserSettingsDatabase.getInstance(vars.context);
                            Database database = new Database();
                            database.continent = continent;
                            database.download_date = new Date();
                            database.version = version;
                            database.database_name = tableType.toString();
                            db.getDatabase().InsertDatabase(database);

                            if (finishedCount == 9) {
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
                            Log.i(TAG, "found statistics: " + statistics.AirportsCount.toString());

                            ((TextView) getDialog().findViewById(R.id.airportCountTxt)).setText(statistics.AirportsCount.toString());
                            ((TextView) getDialog().findViewById(R.id.airspacesCountTxt)).setText(statistics.AirspacesCount.toString());
                            ((TextView) getDialog().findViewById(R.id.regionsCountTxt)).setText(statistics.RegionsCount.toString());
                            ((TextView) getDialog().findViewById(R.id.countriesCountTxt)).setText(statistics.CountriesCount.toString());
                            ((TextView) getDialog().findViewById(R.id.firsCountTxt)).setText(statistics.FirsCount.toString());
                            ((TextView) getDialog().findViewById(R.id.fixesCountTxt)).setText(statistics.FixesCount.toString());
                            ((TextView) getDialog().findViewById(R.id.navaidsCountTxt)).setText(statistics.NavaidsCount.toString());
                            ((TextView) getDialog().findViewById(R.id.chartsCountTxt)).setText(statistics.MBTilesCount.toString());
                            ((TextView) getDialog().findViewById(R.id.citiesCountTxt)).setText(statistics.CitiesCount.toString());
                            version = statistics.Version;
                        }


                    });

                    airnavClient.StartDownload(continent);
                }
                else
                {
                    DatabaseDownloadDialog.this.dismiss();
                }
            }
        });
    }
}
