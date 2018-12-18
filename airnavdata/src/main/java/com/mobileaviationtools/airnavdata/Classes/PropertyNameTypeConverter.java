package com.mobileaviationtools.airnavdata.Classes;

import android.arch.persistence.room.TypeConverter;

public class PropertyNameTypeConverter {
    @TypeConverter
    public static PropertiesName toProperiesGroup(String name)
    {
        return PropertiesName.valueOf(name);
    }

    @TypeConverter
    public static String toString(PropertiesName name)
    {
        return name.toString();
    }
}
