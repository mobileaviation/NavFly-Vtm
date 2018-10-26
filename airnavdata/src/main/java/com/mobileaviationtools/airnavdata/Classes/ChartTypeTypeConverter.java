package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;


public class ChartTypeTypeConverter {
    @TypeConverter
    public static ChartType toChartType(String type)
    {
        return ChartType.valueOf(type);
    }

    @TypeConverter
    public static String toString(ChartType type)
    {
        return type.toString();
    }
}

