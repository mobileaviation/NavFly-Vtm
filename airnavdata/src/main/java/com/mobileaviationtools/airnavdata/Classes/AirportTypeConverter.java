package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class AirportTypeConverter {
    @TypeConverter
    public static AirportType toAirportType(String type)
    {
        return AirportType.valueOf(type);
    }

    @TypeConverter
    public static String toString(AirportType type)
    {
        return type.toString();
    }
}
