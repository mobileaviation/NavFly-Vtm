package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.City;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class CitiesAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public CitiesAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public CitiesAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface CitiesService
    {
        @GET("v1/airnavdb/cities/limit/{start}/{count}")
        Call<List<City>> getCities(@Path("start") int start, @Path("count") int count);

        @GET("v1/airnavdb/cities/limit/{continent}/{start}/{count}")
        Call<List<City>> getCitiesByContinent(@Path("start") int start, @Path("count") int count, @Path("continent") String continent);
    }

    public void loadcities(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        this.continent = "";
        deleteFromCitiesByContinent(continent);
        //db.beginTransaction();
        doCall();
    }

    public void loadcitiesByContinent(int totalCount, String continent) {
        this.totalCount = totalCount;
        this.position= 0;
        this.continent = continent;
        deleteFromCitiesByContinent(continent);
        //db.beginTransaction();
        doCall();
    }

    private void deleteFromCitiesByContinent(String continent)
    {
        if(continent.length()>0)
        {
            db.getCities().deleteFromCitiesByContinent(continent);
        }
        else
        {
            db.getCities().deleteFromCities();
        }
    }

    private int totalCount;
    private int position;
    private String continent;

    private void doCall()
    {
        CitiesAPIDataSource.CitiesService service = retrofit.create(CitiesAPIDataSource.CitiesService.class);
        Call<List<City>> citiesCall;
        if (continent.length()==0) {
            citiesCall = service.getCities(position, 500);
        } else
        {
            citiesCall = service.getCitiesByContinent(position, 500, continent);
        }
        citiesCall.enqueue(new Callback<List<City>>() {
            @Override
            public void onResponse(Call<List<City>> call, Response<List<City>> response) {
                if (response.isSuccessful())
                {
                    if (insertCities(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Countries");
                        //db.setTransactionSuccessful();
                        //db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.cities, continent);
                    }
                }
                else
                {
                    //db.setTransactionSuccessful();
                    //db.endTransaction();
                    Log.e(TAG, "Error recieving Countries results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.cities);
                }
            }

            @Override
            public void onFailure(Call<List<City>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Cities results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.cities);
            }
        });

    }

    public boolean insertCities(List<City> cities)
    {
        db.getCities().insertCities(cities);
        position = position + cities.size();
        Log.i(TAG, "Cities Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.cities);

        return (position < totalCount);
    }
}
