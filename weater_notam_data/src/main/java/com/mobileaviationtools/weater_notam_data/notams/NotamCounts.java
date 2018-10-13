package com.mobileaviationtools.weater_notam_data.notams;

import java.util.Date;

public class NotamCounts {
    public NotamCounts()
    {
        retrievedDate = new Date();
    }

    public NotamCount[] counts;

    public Long totalNumberOfDesignators;
    public Long totalNumberOfNOTAMs;
    public Long totalRecordCountWithFilter;
    public Long noOfConst;
    public Long noOfLtas;
    public String geoJson;
    public Double centerLat;
    public Double centerLong;
    public Double north;
    public Double south;
    public Double east;
    public Double west;
    public String polygon;
    public Long requestID;

    public Date retrievedDate;
}
