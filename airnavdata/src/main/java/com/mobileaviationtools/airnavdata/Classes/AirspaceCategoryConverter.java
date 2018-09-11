package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class AirspaceCategoryConverter {
    @TypeConverter
    public static AirspaceCategory toAirspaceCategory(String category)
    {
        return AirspaceCategory.valueOf(category);
    }

    @TypeConverter
    public static String toString(AirspaceCategory type)
    {
        return type.toString();
    }
}
