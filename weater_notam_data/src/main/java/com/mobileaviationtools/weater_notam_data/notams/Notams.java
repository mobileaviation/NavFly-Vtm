package com.mobileaviationtools.weater_notam_data.notams;

public class Notams {
    public Notam[] notamList;
    public Long startRecordCount;
    public Long endRecordCount;
    public Long totalNotamCount;
    public Long filteredResultCount;
    public String criteriaCaption;
    public String searchDateTime;
    public String error;

    public countsByTypeClass[] countsByType;

    public class countsByTypeClass
    {
        public String name;
        public Long value;
    }
}
