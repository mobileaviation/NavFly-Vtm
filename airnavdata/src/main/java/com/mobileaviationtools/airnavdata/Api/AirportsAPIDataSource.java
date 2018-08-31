package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Navaid;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class AirportsAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;

    Retrofit retrofit;

    public AirportsAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }

    public AirportsAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    public interface AirportsService
    {
        @GET("v1/airnavdb/airports/limit/{start}/{count}")
        Call<List<Airport>> getAirport(@Path("start") int start, @Path("count") int count);
    }

    public void loadAirports(int airports_count)
    {
        class getAirportsAsync extends AsyncTask {
            public getAirportsAsync(int totalCount) {
                this.totalCount = totalCount;
            }

            private int totalCount;


            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    int position = 0;
                    db.beginTransaction();

                    while (position < totalCount) {
                        AirportsService service = retrofit.create(AirportsService.class);
                        Call<List<Airport>> airportsCall = service.getAirport(position, 500);
                        List<Airport> airports = airportsCall.execute().body();

                        db.getAirport().insertAirports(airports);

                        ArrayList<Runway> runways = new ArrayList<>();
                        ArrayList<Frequency> frequencies = new ArrayList<>();
                        for( Airport a : airports)
                        {
                            runways.addAll(a.runways);
                            frequencies.addAll(a.frequencies);
                        }
                        if (runways.size()>0) db.getRunways().insertRunways(runways);
                        if (frequencies.size()>0) db.getFrequency().insertFrequencies(frequencies);


                        position = position + airports.size();
                        Log.i(TAG, "Airports Position: " + position);

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

        getAirportsAsync getAirports = new getAirportsAsync(airports_count);
        getAirports.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
}
