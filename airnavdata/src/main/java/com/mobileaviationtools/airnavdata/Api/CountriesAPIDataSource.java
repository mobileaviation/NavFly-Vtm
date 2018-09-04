package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Country;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class CountriesAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public CountriesAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public CountriesAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    public interface CountriesService
    {
        @GET("v1/airnavdb/countries/limit/{start}/{count}")
        Call<List<Country>> getCountries(@Path("start") int start, @Path("count") int count);
    }

    public void loadcountries(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        CountriesAPIDataSource.CountriesService service = retrofit.create(CountriesAPIDataSource.CountriesService.class);
        Call<List<Country>> countriesCall = service.getCountries(position, 500);
        countriesCall.enqueue(new Callback<List<Country>>() {
            @Override
            public void onResponse(Call<List<Country>> call, Response<List<Country>> response) {
                if (response.isSuccessful())
                {
                    if (insertCountries(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Countries");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving Countries results");
                }
            }

            @Override
            public void onFailure(Call<List<Country>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Countries results: " + t.getMessage());
            }
        });

    }

    public boolean insertCountries(List<Country> countries)
    {
        db.getCountries().insertCountries(countries);
        position = position + countries.size();
        Log.i(TAG, "Countries Position: " + position);

        return (position < totalCount);
    }
}
