package com.mobileaviationtools.airnavdata.Entities;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import com.mobileaviationtools.airnavdata.Classes.DateConverter;

import java.text.SimpleDateFormat;
import java.util.Date;

@Entity(tableName = "tbl_ActivePeriods")
public class ActivePeriod {
    @PrimaryKey
    public Integer id;
    public Long airspace_id;

    @TypeConverters({DateConverter.class})
    public Date startDate;
    @TypeConverters({DateConverter.class})
    public Date endDate;

    @Ignore
    public String start;
    @Ignore
    public void setDates()
    {
        startDate = getDateFromString(start);
        endDate = getDateFromString(end);
    }
    @Ignore
    public String end;

    @Ignore
    private Date getDateFromString(String date)
    {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            return format.parse(date);
        }
        catch (Exception ee)
        {
            return null;
        }
    }

    public String timezone;
}
