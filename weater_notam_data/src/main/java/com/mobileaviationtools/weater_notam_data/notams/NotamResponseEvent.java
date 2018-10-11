package com.mobileaviationtools.weater_notam_data.notams;

public interface NotamResponseEvent {
    void OnNotamsResponse(Notams notams, String message);
    void OnFailure(String message);
}
