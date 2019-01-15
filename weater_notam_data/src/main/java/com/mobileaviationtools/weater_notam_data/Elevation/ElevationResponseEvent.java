package com.mobileaviationtools.weater_notam_data.Elevation;

public interface ElevationResponseEvent {
    public void OnElevationResponse(elevation resp, String json, String message);
    public void OnFailure(String message);
}
