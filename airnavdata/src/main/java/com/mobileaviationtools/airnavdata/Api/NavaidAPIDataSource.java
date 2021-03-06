package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
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

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface NavaidsService
    {
        @GET("v1/airnavdb/navaids/limit/{start}/{count}")
        Call<List<Navaid>> getNavaids(@Path("start") int start, @Path("count") int count);
        @GET("v1/airnavdb/navaids/limit/{continent}/{start}/{count}")
        Call<List<Navaid>> getNavaidsByContinent(@Path("start") int start, @Path("count") int count, @Path("continent") String continent);
    }

    public void loadNavaids(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        this.continent = "";
        deleteByContinent(continent);
        //db.beginTransaction();
        doCall();
    }

    public void loadNavaidsByContinent(int totalCount, String continent) {
        this.totalCount = totalCount;
        this.position= 0;
        this.continent = continent;
        deleteByContinent(continent);
        //db.beginTransaction();
        doCall();
    }

    private void deleteByContinent(String continent)
    {
        if (continent.length()>0)
        {
            db.getNavaids().deleteFromNavaidsByContinent(continent);
        }
        else
        {
            db.getNavaids().deleteFromNavaids();
        }
    }

    private int totalCount;
    private int position;
    private String continent;

    private void doCall()
    {
        NavaidsService service = retrofit.create(NavaidsService.class);
        Call<List<Navaid>> navaidsCall;
        if (this.continent.length()==0) {
            navaidsCall = service.getNavaids(position, 500);
        }else
        {
            navaidsCall = service.getNavaidsByContinent(position, 500, continent);
        }
        navaidsCall.enqueue(new Callback<List<Navaid>>() {
            @Override
            public void onResponse(Call<List<Navaid>> call, Response<List<Navaid>> response) {
                if (response.isSuccessful())
                {
                    if (insertNaviads(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Navaids");
                        //db.setTransactionSuccessful();
                        //db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.navaids, continent);
                    }
                }
                else
                {
                    //db.setTransactionSuccessful();
                    //db.endTransaction();
                    Log.e(TAG, "Error recieving Navaid results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.navaids);
                }
            }

            @Override
            public void onFailure(Call<List<Navaid>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Navaid results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.navaids);
            }
        });

    }

    public boolean insertNaviads(List<Navaid> navaids)
    {
        db.getNavaids().insertNavaids(navaids);
        position = position + navaids.size();
        Log.i(TAG, "Navaids Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.navaids);

        return (position < totalCount);
    }

}
