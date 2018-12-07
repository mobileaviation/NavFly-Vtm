package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Region;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RegionsAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public RegionsAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public RegionsAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface RegionsService
    {
        @GET("v1/airnavdb/regions/limit/{start}/{count}")
        Call<List<Region>> getRegions(@Path("start") int start, @Path("count") int count);
    }

    public void loadRegions(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        RegionsAPIDataSource.RegionsService service = retrofit.create(RegionsAPIDataSource.RegionsService.class);
        Call<List<Region>> regionsCall = service.getRegions(position, 500);
        regionsCall.enqueue(new Callback<List<Region>>() {
            @Override
            public void onResponse(Call<List<Region>> call, Response<List<Region>> response) {
                if (response.isSuccessful())
                {
                    if (insertRegions(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Regions");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.regions, "");
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving Regions results: " + response.message());
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.regions);
                }
            }

            @Override
            public void onFailure(Call<List<Region>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Regions results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.regions);
            }
        });

    }

    public boolean insertRegions(List<Region> regions)
    {
        db.getRegions().insertRegions(regions);
        position = position + regions.size();
        Log.i(TAG, "Regions Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.regions);

        return (position < totalCount);
    }
}
