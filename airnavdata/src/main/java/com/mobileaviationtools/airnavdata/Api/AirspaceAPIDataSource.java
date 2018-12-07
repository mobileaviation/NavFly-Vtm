package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.ATCStation;
import com.mobileaviationtools.airnavdata.Entities.ActiveDay;
import com.mobileaviationtools.airnavdata.Entities.ActivePeriod;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class AirspaceAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;

    Retrofit retrofit;

    public AirspaceAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }

    public AirspaceAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface AirspacesService
    {
        @GET("v1/airnavdb/airspaces/limit/{start}/{count}")
        Call<List<Airspace>> getAirspace(@Path("start") int start, @Path("count") int count);

        @GET("v1/airnavdb/airspaces/limit/{continent}/{start}/{count}")
        Call<List<Airspace>> getAirspacesByContinent(@Path("start") int start, @Path("count") int count, @Path("continent") String continent);

        @GET("v1/airnavdb/airspaces/country/{country}")
        Call<List<Airspace>> getAirspacesByCountry(@Path("country") String country);
    }

    public void loadAirspaces(int airspaces_count) {
        totalCount = airspaces_count;
        position = 0;
        continent = "";
        db.beginTransaction();
        doCall();
    }

    public void loadAirspacesByContinent(int airspaces_count, String continent) {
        totalCount = airspaces_count;
        position = 0;
        this.continent = continent;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;
    private String continent;

    private void doCall()
    {
        AirspacesService service = retrofit.create(AirspacesService.class);
        Call<List<Airspace>> airspacesCall;
        if (continent.length()==0) {
            airspacesCall = service.getAirspace(position, 500);
        }else
        {
            airspacesCall = service.getAirspacesByContinent(position, 500, continent);
        }
        airspacesCall.enqueue(new Callback<List<Airspace>>() {
            @Override
            public void onResponse(Call<List<Airspace>> call, Response<List<Airspace>> response) {
                if (response.isSuccessful())
                {
                    if (insertAirspaces(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Airspaces");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.airspaces);
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.airspaces);
                }
            }

            @Override
            public void onFailure(Call<List<Airspace>> call, Throwable t) {
                Log.e(TAG, "Failure recieving results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.airspaces);
            }
        });
    }

    private boolean insertAirspaces(List<Airspace> airspaces)
    {
        for (Airspace a: airspaces)
        {
            a.processGeometry();

            db.getAirpaces().insertAirspace(a);

            for (ActiveDay d: a.activeDays) d.airspace_id = a.id;
            if (a.activeDays.size()>0) db.getActiveDays().insertActiveDays(a.activeDays);

            for(ActivePeriod p : a.activePeriods) {
                p.setDates();
                p.airspace_id = a.id;
            }
            if (a.activePeriods.size()>0) db.getActivePeriods().insertActivePeriods(a.activePeriods);

            for (ATCStation atc: a.atcStations) atc.airspace_id = a.id;
            if (a.atcStations.size()>0) db.getAtcStations().insertATCStations(a.atcStations);

        }

        position = position + airspaces.size();
        Log.i(TAG, "Airspaces Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.airspaces);

        return (position < totalCount);
    }

}
