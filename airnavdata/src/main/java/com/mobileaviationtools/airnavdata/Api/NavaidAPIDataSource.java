package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class NavaidAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public NavaidAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public NavaidAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    public interface NavaidsService
    {
        @GET("v1/airnavdb/navaids/limit/{start}/{count}")
        Call<List<Navaid>> getNavaids(@Path("start") int start, @Path("count") int count);
    }

    public void loadNavaids(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        NavaidsService service = retrofit.create(NavaidsService.class);
        Call<List<Navaid>> navaidsCall = service.getNavaids(position, 500);
        navaidsCall.enqueue(new Callback<List<Navaid>>() {
            @Override
            public void onResponse(Call<List<Navaid>> call, Response<List<Navaid>> response) {
                if (response.isSuccessful())
                {
                    if (insertNaviads(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Navaids");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving Navaid results");
                }
            }

            @Override
            public void onFailure(Call<List<Navaid>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Navaid results: " + t.getMessage());
            }
        });

    }

    public boolean insertNaviads(List<Navaid> navaids)
    {
        db.getNavaids().insertNavaids(navaids);
        position = position + navaids.size();
        Log.i(TAG, "Navaids Position: " + position);

        return (position < totalCount);
    }

}
