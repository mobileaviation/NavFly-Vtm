package com.mobileaviationtools.nav_fly.Route.Notams;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.mobileaviationtools.airnavdata.AirnavAirportInfoDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Notam;
import com.mobileaviationtools.nav_fly.Classes.Helpers;
import com.mobileaviationtools.nav_fly.Classes.MapperHelper;
import com.mobileaviationtools.nav_fly.GlobalVars;
import com.mobileaviationtools.nav_fly.R;
import com.mobileaviationtools.weater_notam_data.notams.NotamCount;
import com.mobileaviationtools.weater_notam_data.notams.NotamCounts;
import com.mobileaviationtools.weater_notam_data.notams.NotamResponseEvent;
import com.mobileaviationtools.weater_notam_data.notams.Notams;
import com.mobileaviationtools.weater_notam_data.services.NotamService;

import org.oscim.core.MapPosition;
import org.oscim.map.Map;

public class NotamsListLayout extends LinearLayout {
    private String TAG = "NotamsListLayout";

    private NotamsAirportItemAdapter notamsAirportItemAdapter;
    private NotamsItemAdapter notamsItemAdapter;
    private ImageButton notamsRefreshBtn;
    private ListView airportsList;
    private ListView notamsList;
    private Context context;
    private Activity activity;
    private ProgressBar notamsProgressBar;

    public NotamsListLayout(Context context) {
        super(context);
    }

    public NotamsListLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    private GlobalVars vars;
    public void setGlobalVars(GlobalVars vars)
    {
        this.vars = vars;
    }

    public void init(Context context, Activity activity, ProgressBar notamsProgressBar) {
        this.context = context;
        this.activity = activity;
        this.notamsProgressBar = notamsProgressBar;
        toggleNotamsProgressVisibility(false);
        notamsRefreshBtn = (ImageButton) findViewById(R.id.notamsRefreshBtn);
        setNotamsRefreshBtn();
        setNotamResponseEvent();
    }

    private void toggleNotamsProgressVisibility(final Boolean visible)
    {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (notamsProgressBar != null) notamsProgressBar.setVisibility(
                        (visible) ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

    private NotamResponseEvent notamResponseEvent;
    public NotamResponseEvent getNotamResponseEvent() { return notamResponseEvent; }
    private void setNotamResponseEvent()
    {
        notamResponseEvent = new NotamResponseEvent() {
            @Override
            public void OnNotamsResponse(final Notams notams, NotamCount count, String message) {

            }

            @Override
            public void OnNotamsCountResponse(final NotamCounts counts, String message) {

                Log.i(TAG, message);
                toggleNotamsProgressVisibility(false);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notamsAirportItemAdapter = new NotamsAirportItemAdapter(counts, NotamsListLayout.this.getContext());
                        airportsList = (ListView) findViewById(R.id.notamsAirportsListView);
                        airportsList.setAdapter(notamsAirportItemAdapter);
                        setNotamItemListClickItem();
                    }
                });
                toggleNotamsProgressVisibility(false);
            }

            @Override
            public void OnFailure(String message) {
                Log.i(TAG, "Failure: " + message);
                getNotams(true);
                toggleNotamsProgressVisibility(false);
            }
        };
    }

    public void notamBtnClick() {
        if (notamsAirportItemAdapter == null) {
            toggleNotamsProgressVisibility(true);
            getNotams(true);
        }
    }

    private void setNotamsRefreshBtn()
    {
        notamsRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleNotamsProgressVisibility(true);
                getNotams(false);
            }
        });
    }

    private void getNotams(Boolean fromDatabase)
    {
        NotamRetrieval notamRetrieval = new NotamRetrieval(vars);
        notamRetrieval.setNotamsRetrievedResponseEvent(notamResponseEvent);
        notamRetrieval.startNotamRetrieval(fromDatabase);
    }

    private void setNotamItemListClickItem()
    {
        airportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NotamsAirportItemAdapter adapter = (NotamsAirportItemAdapter)adapterView.getAdapter();
                Airport airport = adapter.getAirport(i);
                getNotamsFromDB(airport);
            }
        });
    }

    private void getNotamsFromDB(Airport airport)
    {
        AirnavAirportInfoDatabase db = AirnavAirportInfoDatabase.getInstance(vars.context);
        Notam[] notams = db.getNotam().getLatestStoredNotamsByStationId(airport.ident);

        notamsItemAdapter = new NotamsItemAdapter(NotamsListLayout.this.getContext(), notams);
        notamsList = (ListView) findViewById(R.id.notamsList);
        notamsList.setAdapter(notamsItemAdapter);
    }

}
