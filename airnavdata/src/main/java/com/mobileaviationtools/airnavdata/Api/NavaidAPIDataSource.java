package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Navaid;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
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

    public void loadNavaids(int totalCount)
    {
        //int totalcount = 11117;

        class GetNavaidsAsync extends AsyncTask {
            public GetNavaidsAsync(int totalCount) {
                this.totalCount = totalCount;
            }

            private int totalCount;


            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    int position = 0;
                    db.beginTransaction();

                    while (position < totalCount) {
                        NavaidsService service = retrofit.create(NavaidsService.class);
                        Call<List<Navaid>> navaidsCall = service.getNavaids(position, 500);
                        List<Navaid> navaids = navaidsCall.execute().body();
                        

                        db.getNavaids().insertNavaids(navaids);

                        position = position + navaids.size();
                        Log.i(TAG, "Navaids Position: " + position);

                        int i = 0;
                    }

                    db.setTransactionSuccessful();
                    db.endTransaction();


                } catch (IOException e) {
                    e.printStackTrace();
                }

                return null;
            }
        }

            GetNavaidsAsync getNavaids = new GetNavaidsAsync(totalCount);
            getNavaids.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);



    }

}
