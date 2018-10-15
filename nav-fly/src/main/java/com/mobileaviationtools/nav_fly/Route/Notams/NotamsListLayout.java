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

import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.nav_fly.R;
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

    public NotamsListLayout(Context context) {
        super(context);
    }

    public NotamsListLayout(Context context,  AttributeSet attrs) {
        super(context, attrs);
    }

    private Map map;

    public void setMap(Map map) {
        this.map = map;
    }

    public void init(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
        notamsRefreshBtn = (ImageButton) findViewById(R.id.notamsRefreshBtn);
        setNotamsRefreshBtn();
        setNotamResponseEvent();
    }

    private NotamResponseEvent notamResponseEvent;
    private void setNotamResponseEvent()
    {
        notamResponseEvent = new NotamResponseEvent() {
            @Override
            public void OnNotamsResponse(final Notams notams, String message) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notamsItemAdapter = new NotamsItemAdapter(NotamsListLayout.this.getContext(), notams);
                        notamsList = (ListView) findViewById(R.id.notamsList);
                        notamsList.setAdapter(notamsItemAdapter);
                    }
                });

            }

            @Override
            public void OnNotamsCountResponse(final NotamCounts counts, String message) {

                Log.i(TAG, message);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        notamsAirportItemAdapter = new NotamsAirportItemAdapter(counts, NotamsListLayout.this.getContext());
                        airportsList = (ListView) findViewById(R.id.notamsAirportsListView);
                        airportsList.setAdapter(notamsAirportItemAdapter);
                        setNotamItemListClickItem();
                    }
                });
            }

            @Override
            public void OnFailure(String message) {
                Log.i(TAG, "Failure: " + message);
            }
        };
    }

    public void notamBtnClick() {
        if (notamsAirportItemAdapter == null) {
            MapPosition pos = map.getMapPosition();
            NotamService notamService = new NotamService();
            notamService.GetCountsByLocationAndRadius(pos.getGeoPoint(), 100l, notamResponseEvent);
        }
    }

    private void setNotamsRefreshBtn()
    {
        notamsRefreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapPosition pos = map.getMapPosition();
                NotamService notamService = new NotamService();
                notamService.GetCountsByLocationAndRadius(pos.getGeoPoint(), 100l, notamResponseEvent);
            }
        });
    }

    private void setNotamItemListClickItem()
    {
        airportsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NotamsAirportItemAdapter adapter = (NotamsAirportItemAdapter)adapterView.getAdapter();
                Airport airport = adapter.getAirport(i);
                NotamService notamService = new NotamService();
                notamService.GetNotamsByICAO(airport.ident, notamResponseEvent);
            }
        });
    }
}
