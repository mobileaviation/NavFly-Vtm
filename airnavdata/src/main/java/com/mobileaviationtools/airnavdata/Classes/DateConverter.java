package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public static Date toDate(long date){
        Date d = new Date();
        d.setTime(date);
        return d;
    }

    @TypeConverter
    public static Long toLong(Date date)
    {
        return date.getTime();
    }
}
