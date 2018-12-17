package com.mobileaviationtools.weater_notam_data.notams;

public interface NotamResponseEvent {
    void OnNotamsResponse(Notams notams, NotamCount count, String message);
    void OnNotamsCountResponse(NotamCounts counts, String message);
    void OnFailure(String message);
}
