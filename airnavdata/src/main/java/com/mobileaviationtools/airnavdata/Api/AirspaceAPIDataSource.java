package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Airspace;

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
    }

    public void loadAirspaces(int airspaces_count) {
        totalCount = airspaces_count;
        position = 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        AirspacesService service = retrofit.create(AirspacesService.class);
        Call<List<Airspace>> airspacesCall = service.getAirspace(position, 500);
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
        db.getAirpaces().insertAirspaces(airspaces);

        position = position + airspaces.size();
        Log.i(TAG, "Airspaces Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.airspaces);

        return (position < totalCount);
    }

}
