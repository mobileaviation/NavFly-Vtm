package com.mobileaviationtools.airnavdata.Api;

import android.content.Context;
import android.util.Log;

import com.mobileaviationtools.airnavdata.AirnavDatabase;
import com.mobileaviationtools.airnavdata.Classes.DataDownloadStatusEvent;
import com.mobileaviationtools.airnavdata.Classes.TableType;
import com.mobileaviationtools.airnavdata.Entities.Country;
import com.mobileaviationtools.airnavdata.Entities.MBTile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class MBTilesAPIDataSource {
    private Context context;
    private static final String TAG = "LoadFromAPIDataSource";

    AirnavDatabase db;
    Retrofit retrofit;

    public MBTilesAPIDataSource(Context context, Retrofit retrofit)
    {
        this(context);
        this.retrofit = retrofit;
    }


    public MBTilesAPIDataSource(Context context) {
        this.context = context;
        db = AirnavDatabase.getInstance(context);
    }

    private DataDownloadStatusEvent statusEvent;
    public void SetStatusEvent(DataDownloadStatusEvent statusEvent)
    {
        this.statusEvent = statusEvent;
    }

    public interface TilesService
    {
        @GET("v1/airnavdb/tiles/limit/{start}/{count}")
        Call<List<MBTile>> getTiles(@Path("start") int start, @Path("count") int count);
    }

    public void loadTiles(int totalCount) {
        this.totalCount = totalCount;
        this.position= 0;
        db.beginTransaction();
        doCall();
    }

    private int totalCount;
    private int position;

    private void doCall()
    {
        MBTilesAPIDataSource.TilesService service = retrofit.create(MBTilesAPIDataSource.TilesService.class);
        Call<List<MBTile>> tilesCall = service.getTiles(position, 500);
        tilesCall.enqueue(new Callback<List<MBTile>>() {
            @Override
            public void onResponse(Call<List<MBTile>> call, Response<List<MBTile>> response) {
                if (response.isSuccessful())
                {
                    if (insertTiles(response.body()))
                        doCall();
                    else {
                        Log.i(TAG, "Finished reading Countries");
                        db.setTransactionSuccessful();
                        db.endTransaction();
                        if (statusEvent != null) statusEvent.OnFinished(TableType.mbtiles, "");
                    }
                }
                else
                {
                    Log.e(TAG, "Error recieving Countries results");
                    if (statusEvent != null) statusEvent.OnError(response.message(), TableType.mbtiles);
                }
            }

            @Override
            public void onFailure(Call<List<MBTile>> call, Throwable t) {
                Log.e(TAG, "Failure recieving Tiles results: " + t.getMessage());
                if (statusEvent != null) statusEvent.OnError(t.getMessage(), TableType.mbtiles);
            }
        });

    }

    public boolean insertTiles(List<MBTile> tiles)
    {
        db.getTiles().insertTiles(tiles);
        position = position + tiles.size();
        Log.i(TAG, "Tiles Position: " + position);

        if (statusEvent != null) statusEvent.onProgress(totalCount, position, TableType.mbtiles);

        return (position < totalCount);
    }
}
