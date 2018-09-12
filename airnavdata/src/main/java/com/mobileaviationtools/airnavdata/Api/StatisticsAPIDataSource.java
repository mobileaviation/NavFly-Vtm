package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.StatisticsEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Airspace;
import com.mobileaviationtools.airnavdata.Models.Statistics;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class StatisticsAPIDataSource {
    private Context context;
    private static final String TAG = "StatisticsAPI";

    Retrofit retrofit;

    private StatisticsEvent statusEvent;
    public void SetStatusEvent(StatisticsEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public StatisticsAPIDataSource(Context context, Retrofit retrofit)
    {
        this.context = context;
        this.retrofit = retrofit;
    }

    public interface StatisticsService
    {
        @GET("v1/airnavdb/statistics")
        Call<Statistics> getStatistics();
    }

    public void GetStatistics()
    {
        doCall();
    }

    private void doCall()
    {
        StatisticsService service = retrofit.create(StatisticsService.class);
        Call<Statistics> statisticsCall = service.getStatistics();
        statisticsCall.enqueue(new Callback<Statistics>() {
            @Override
            public void onResponse(Call<Statistics> call, Response<Statistics> response) {
                if (response.isSuccessful()){
                    Statistics s = response.body();
                    if (statusEvent != null) statusEvent.OnFinished(response.body());
                }
                else
                {
                    Log.e(TAG, "Error recieving statistics");
                    if (statusEvent != null) statusEvent.OnError(response.message());
                }
            }

            @Override
            public void onFailure(Call<Statistics> call, Throwable t) {
                Log.e(TAG, "Error recieving statistics");
                if (statusEvent != null) statusEvent.OnError(t.getMessage());
            }
        });
    }
}
