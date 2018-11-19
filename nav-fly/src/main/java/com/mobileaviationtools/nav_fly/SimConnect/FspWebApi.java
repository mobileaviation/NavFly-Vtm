package com.mobileaviationtools.nav_fly.SimConnect;

import com.mobileaviationtools.nav_fly.SimConnect.Requests.ConnectRequest;
import com.mobileaviationtools.nav_fly.SimConnect.Requests.OffsetRequest;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.ConnectResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.OffsetResponse;

import java.util.List;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public class FspWebApi {
    public static final String  OPENENDPOINT = "v1/fsuipc/open";
    public static final String  CLOSEENDPOINT = "v1/fsuipc/close";
    public static final String  STATUSENDPOINT = "v1/fsuipc/status";
    public static final String  PROCESSENDPOINT = "v1/fsuipc/offsets";
    public static final String  VALUESENDPOINT = "v1/fsuipc/offsets";

    public FspWebApi(String IP, int Port)
    {
        _url = "http://" + IP + ":" + Integer.toString(Port) + "/";
        setupConnectionBase();
        _isConnected = false;
    }

    private void setupConnectionBase()
    {
        Dispatcher dispatcher = new Dispatcher();
        dispatcher.setMaxRequests(10);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.dispatcher(dispatcher);

        OkHttpClient client = builder.build();
        client.dispatcher().setMaxRequests(10);
        retrofit = new Retrofit.Builder()
                .baseUrl(_url)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
    }

    private Retrofit retrofit;
    private String _url;
    private boolean _isConnected;
    public  boolean isConnected()
    {
        return false;
    }

    public interface ConnectService
    {
        @POST(OPENENDPOINT)
        Call<ConnectResponse> Connect(@Body ConnectRequest connectRequest);
    }

    public interface CloseService
    {
        @POST(CLOSEENDPOINT)
        Call<String> Close(@Body ConnectRequest connectRequest);
    }

    public interface AddOffsetService
    {
        @POST(VALUESENDPOINT)
        Call<List<OffsetResponse>> AddOffsets(@Body List<OffsetRequest> offsetRequests);
    }

    public interface ProcessOffsetService
    {
        @GET(PROCESSENDPOINT)
        Call<List<OffsetResponse>> ProcessOffsets(@Query("datagroup") String dataGroup);
    }

    public void doConnectCall(ConnectRequest request)
    {
        ConnectService connectService = retrofit.create(ConnectService.class);
        Call<ConnectResponse> connectCall = connectService.Connect(request);
        connectCall.enqueue(new Callback<ConnectResponse>() {
            @Override
            public void onResponse(Call<ConnectResponse> call, Response<ConnectResponse> response) {
                int i = 0;
            }

            @Override
            public void onFailure(Call<ConnectResponse> call, Throwable t) {
                int i = 0;
            }
        });
    }

    public void doDisconnectCall(ConnectRequest request)
    {
        CloseService closeService = retrofit.create(CloseService.class);
        Call<String> closeCall = closeService.Close(request);
        closeCall.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void doAddOffsetsCall(List<OffsetRequest> offsetRequests)
    {
        AddOffsetService addOffsetService = retrofit.create(AddOffsetService.class);
        Call<List<OffsetResponse>> addOffsetCall = addOffsetService.AddOffsets(offsetRequests);
        addOffsetCall.enqueue(new Callback<List<OffsetResponse>>() {
            @Override
            public void onResponse(Call<List<OffsetResponse>> call, Response<List<OffsetResponse>> response) {

            }

            @Override
            public void onFailure(Call<List<OffsetResponse>> call, Throwable t) {

            }
        });
    }

    public void doProcessOffsetsCall(String datagroup)
    {
        ProcessOffsetService processOffsetService = retrofit.create(ProcessOffsetService.class);
        Call<List<OffsetResponse>> processCall = processOffsetService.ProcessOffsets(datagroup);
        processCall.enqueue(new Callback<List<OffsetResponse>>() {
            @Override
            public void onResponse(Call<List<OffsetResponse>> call, Response<List<OffsetResponse>> response) {

            }

            @Override
            public void onFailure(Call<List<OffsetResponse>> call, Throwable t) {

            }
        });
    }
}
