package com.mobileaviationtools.airnavdata.Api;

import android.os.AsyncTask;

import com.mobileaviationtools.airnavdata.Entities.Navaid;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RetrofitTest {
    public RetrofitTest()
    {

    }

    public interface NavaidsService
    {
        @GET("v1/airnavdb/navaids/limit/{start}/{count}")
        Call<List<Navaid>> getNavaids(@Path("start") int start, @Path("count") int count);
    }

    public List<Navaid> getNavaids(int start, int count)
    {
        List<Navaid> navaids = null;

        class GetNavaidsAsync extends AsyncTask
        {
            public GetNavaidsAsync(List<Navaid> navaids, int start, int count)
            {
                this.navaids  = navaids;
                this.start = start;
                this.count = count;
            }

            private List<Navaid> navaids;
            private int start;
            private int count;

            @Override
            protected Object doInBackground(Object[] objects) {
                try {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://api.mobileaviationtools.com/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    NavaidsService service = retrofit.create(NavaidsService.class);
                    Call<List<Navaid>> navaidsCall = service.getNavaids(start,count);
                    navaids = navaidsCall.execute().body();
                    int i = 0;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        }


        GetNavaidsAsync getNavaidsAsync = new GetNavaidsAsync(navaids, start, count);
        getNavaidsAsync.execute();

        return navaids;
    }
}
