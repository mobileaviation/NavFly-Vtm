package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;

import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Airport;
import com.mobileaviationtools.airnavdata.Entities.Frequency;
import com.mobileaviationtools.airnavdata.Entities.Runway;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
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

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
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

    public void loadAirports(int airports_count) {
        totalCount = airports_count;
        position = 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        AirportsService service = retrofit.create(AirportsService.class);
        Call<List<Airport>> airportsCall = service.getAirport(position, 500);
        airportsCall.enqueue(new Callback<List<Airport>>() {
            @Override
            public void onResponse(Call<List<Airport>> call, Response<List<Airport>> response) {
                if (response.isSuccessful())
                {
                    if (insertAirports(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Airports");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.airports);
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.airports);
                }
            }

            @Override
            public void onFailure(Call<List<Airport>> call, Throwable t) {
                Log.e(TAG, "Failure recieving results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.airports);
            }
        });
    }

    private boolean insertAirports(List<Airport> airports)
    {
        db.getAirport().insertAirports(airports);

        ArrayList<Runway> runways = new ArrayList<>();
        ArrayList<Frequency> frequencies = new ArrayList<>();
        for (Airport a : airports) {
            runways.addAll(a.runways);
            frequencies.addAll(a.frequencies);
        }
        if (runways.size() > 0) db.getRunways().insertRunways(runways);
        if (frequencies.size() > 0)
            db.getFrequency().insertFrequencies(frequencies);


        position = position + airports.size();
        Log.i(TAG, "Airports Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.airports);

        return (position < totalCount);
    }

}
