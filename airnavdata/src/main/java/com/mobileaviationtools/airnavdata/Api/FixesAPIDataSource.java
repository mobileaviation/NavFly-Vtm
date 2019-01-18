package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Fix;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class FixesAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public FixesAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public FixesAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface FixesService
    {
        @GET("v1/airnavdb/fixes/limit/{start}/{count}")
        Call<List<Fix>> getFixes(@Path("start") int start, @Path("count") int count);

        @GET("v1/airnavdb/fixes/limit/{continent}/{start}/{count}")
        Call<List<Fix>> getFixesByContinent(@Path("start") int start, @Path("count") int count, @Path("continent") String continent);
    }

    public void loadfixes(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        this.continent = "";
        db.beginTransaction();
        doCall();
    }

    public void loadfixesByContinent(int totalCount, String continent) {
        this.totalCount = totalCount;
        this.position= 0;
        this.continent = continent;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;
    private String continent;

    private void doCall()
    {
        FixesAPIDataSource.FixesService service = retrofit.create(FixesAPIDataSource.FixesService.class);
        Call<List<Fix>> fixesCall;
        if (continent.length()==0) {
            fixesCall = service.getFixes(position, 500);
        }else
        {
            fixesCall = service.getFixesByContinent(position, 500, continent);
        }
        fixesCall.enqueue(new Callback<List<Fix>>() {
            @Override
            public void onResponse(Call<List<Fix>> call, Response<List<Fix>> response) {
                if (response.isSuccessful())
                {
                    if (insertFixes(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Fixes");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.fixes, continent);
                    }
                }
                else
                {
                    db.setTransactionSuccessful();
                    db.endTransaction();
                    Log.e(TAG, "Error recieving Fixes results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.fixes);
                }
            }

            @Override
            public void onFailure(Call<List<Fix>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Fixes results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.fixes);
            }
        });

    }

    public boolean insertFixes(List<Fix> fixes)
    {
        db.getFixes().insertFixes(fixes);
        position = position + fixes.size();
        Log.i(TAG, "Fixes Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.fixes);

        return (position < totalCount);
    }
}
