package com.mobileaviationtools.nav_fly.SimConnect;

import com.mobileaviationtools.nav_fly.SimConnect.Responses.ConnectResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.OffsetResponse;
import com.mobileaviationtools.nav_fly.SimConnect.Responses.VersionResponse;

import java.util.List;

public interface WebAPIEvents {
    void OnVersionRetrieved(VersionResponse version);
    void OnConnected(ConnectResponse response);
    void OnClosed(String response);
    void OnRetrievedOffsets(List<OffsetResponse> response);
    void OnFailure(String message);
}
