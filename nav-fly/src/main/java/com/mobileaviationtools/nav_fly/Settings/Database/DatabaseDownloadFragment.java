package com.mobileaviationtools.nav_fly.Settings.Database;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mobileaviationtools.airnavdata.AirnavUserSettingsDatabase;
import com.mobileaviationtools.airnavdata.Api.AirnavClient;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Database;
import com.mobileaviationtools.airnavdata.Models.Statistics;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.nav_fly.Startup.StartupDialog;

import org.w3c.dom.Text;

import java.util.Date;
import java.util.EventListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class DatabaseDownloadFragment extends Fragment {
    public DatabaseDownloadFragment() {
        // Required empty public constructor
    }

    private static DatabaseDownloadFragment instance;

    public static DatabaseDownloadFragment getInstance(DialogFragment parentDialog, GlobalVars vars, Boolean startup)
    {
        if (instance == null) {
            DatabaseDownloadFragment instance = new DatabaseDownloadFragment();
            instance.vars = vars;
            instance.parentDialog = parentDialog;
            instance.startup = startup;
            return instance;
        }
        else
            return instance;
    }

    private GlobalVars vars;
    private DialogFragment parentDialog;
    private Boolean startup;
    private StartupDialog.NextPrevEventListener nextEvent;
    private Statistics statistics;

    public void SetNextEventListener(StartupDialog.NextPrevEventListener listener)
    {
        nextEvent = listener;
    }

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

    private int airportsRetry = 0;
    private int navaidsRetry = 0;
    private int countriesRetry = 0;
    private int regionsRetry = 0;
    private int firsRetry = 0;
    private int fixesRetry = 0;
    private int airspacesRetry = 0;
    private int chartsRetry = 0;
    private int citiesRetry = 0;


    private TextView dbDownloadHelpTextView;

    private Integer finishedCount;
    private String continent;
    private String TAG = "DatabaseDownloadActivity";
    private Integer version;
    private View view;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_database_download, container, false);
        setup();
        return view;
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

        dbDownloadHelpTextView = (TextView)view.findViewById(R.id.dbDownloadHelpTextView);
        dbDownloadHelpTextView.setVisibility((startup) ? View.VISIBLE : View.GONE);

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
                    final AirnavClient airnavClient = new AirnavClient(vars.context);
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
                                actionBtn.setText((startup) ? "Next" : "Close");
                                actionBtn.setTag(true);
                                actionBtn.setEnabled(true);
                            }
                        }

                        @Override
                        public void OnError(String message, TableType tableType) {
                            Toast.makeText(vars.context, "Error downloading database, please try again in a few minutes!", Toast.LENGTH_LONG);

                            if (tableType != null) {
                                Statistics statistics = DatabaseDownloadFragment.this.statistics;
                                switch (tableType) {
                                    case airports:
                                        airportsRetry++;
                                        if (airportsRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case airspaces:
                                        airspacesRetry++;
                                        if (airspacesRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case firs:
                                        firsRetry++;
                                        if (firsRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case fixes:
                                        fixesRetry++;
                                        if (fixesRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case navaids:
                                        navaidsRetry++;
                                        if (navaidsRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case countries:
                                        countriesRetry++;
                                        if (countriesRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case regions:
                                        regionsRetry++;
                                        if (regionsRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case mbtiles:
                                        chartsRetry++;
                                        if (chartsRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                    case cities:
                                        citiesRetry++;
                                        if (citiesRetry < 5) airnavClient.StartDownloadIndividualTable(tableType, statistics, continent);
                                        break;
                                }
                            }
                            else
                                actionBtn.setEnabled(true);
                        }

                        @Override
                        public void OnStatistics(Statistics statistics) {
                            DatabaseDownloadFragment.this.statistics = statistics;
                            ((TextView) getView().findViewById(R.id.airportCountTxt)).setText(statistics.AirportsCount.toString());
                            ((TextView) getView().findViewById(R.id.airspacesCountTxt)).setText(statistics.AirspacesCount.toString());
                            ((TextView) getView().findViewById(R.id.regionsCountTxt)).setText(statistics.RegionsCount.toString());
                            ((TextView) getView().findViewById(R.id.countriesCountTxt)).setText(statistics.CountriesCount.toString());
                            ((TextView) getView().findViewById(R.id.firsCountTxt)).setText(statistics.FirsCount.toString());
                            ((TextView) getView().findViewById(R.id.fixesCountTxt)).setText(statistics.FixesCount.toString());
                            ((TextView) getView().findViewById(R.id.navaidsCountTxt)).setText(statistics.NavaidsCount.toString());
                            ((TextView) getView().findViewById(R.id.chartsCountTxt)).setText(statistics.MBTilesCount.toString());
                            ((TextView) getView().findViewById(R.id.citiesCountTxt)).setText(statistics.CitiesCount.toString());
                            version = statistics.Version;
                        }


                    });

                    airnavClient.StartDownload(continent);
                }
                else
                {
                    if (startup)
                    {
                        if (nextEvent != null) nextEvent.OnNext(DatabaseDownloadFragment.this);
                    }
                    else {
                        parentDialog.dismiss();
                    }
                }

            }
        });
    }

}
