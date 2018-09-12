package com.mobileaviationtools.airnavdata.Classes;

import com.mobileaviationtools.airnavdata.Models.Statistics;

public interface StatisticsEvent {
    public void OnFinished(Statistics statistics);
    public void OnError(String message);
}
