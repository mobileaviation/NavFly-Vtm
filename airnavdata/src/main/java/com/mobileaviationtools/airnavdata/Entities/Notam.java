package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "tbl_Notams", indices = {@Index(name="notams_loadedDate_index", value = {"loadedDate"}),
            @Index(name = "notams_airportRef_index", value = "airport_ref")})
public class Notam {
    @PrimaryKey(autoGenerate = true)
    public Integer id;

    public String icaoId;
    public Long loadedDate;
    public Integer airport_ref;

    public String notamNumber;
    public String issueDate;
    public String startDate;
    public String endDate;
    public String icaoMessage;   //1
    public String traditionalMessage;  //2
    public String plainLanguageMessage;   //3
    public String traditionalMessageFrom4thWord;
    public String airportName;
    public String mapPointer;
    public float latitude;
    public float longitude;
}
