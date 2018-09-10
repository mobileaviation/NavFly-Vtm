package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Fir;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class FirsAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public FirsAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public FirsAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface FirsService
    {
        @GET("v1/airnavdb/firs/limit/{start}/{count}")
        Call<List<Fir>> getFirs(@Path("start") int start, @Path("count") int count);
    }

    public void loadfirs(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        FirsAPIDataSource.FirsService service = retrofit.create(FirsAPIDataSource.FirsService.class);
        Call<List<Fir>> firsCall = service.getFirs(position, 500);
        firsCall.enqueue(new Callback<List<Fir>>() {
            @Override
            public void onResponse(Call<List<Fir>> call, Response<List<Fir>> response) {
                if (response.isSuccessful())
                {
                    if (insertFixes(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Firs");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.firs);
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving Firs results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.firs);
                }
            }

            @Override
            public void onFailure(Call<List<Fir>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Firs results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.firs);
            }
        });

    }

    public boolean insertFixes(List<Fir> firs)
    {
        db.getFirs().insertFirs(firs);
        position = position + firs.size();
        Log.i(TAG, "Firs Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.firs);

        return (position < totalCount);
    }
}
